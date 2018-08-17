package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.app.service.WikiScanningService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.RoleE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.Stage;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/18.
 */
@Service
public class WikiScanningServiceImpl implements WikiScanningService {

    private static final Logger logger = LoggerFactory.getLogger(WikiScanningServiceImpl.class);

    private static final String ORG_ICON = "domain";
    private static final String PROJECT_ICON = "project";

    private IamRepository iamRepository;
    private WikiSpaceRepository wikiSpaceRepository;
    private WikiSpaceService wikiSpaceService;
    private WikiGroupService wikiGroupService;
    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService;

    public WikiScanningServiceImpl(IamRepository iamRepository,
                                   WikiSpaceRepository wikiSpaceRepository,
                                   WikiSpaceService wikiSpaceService,
                                   WikiGroupService wikiGroupService,
                                   IWikiSpaceWebHomeService iWikiSpaceWebHomeService) {
        this.iamRepository = iamRepository;
        this.wikiSpaceRepository = wikiSpaceRepository;
        this.wikiSpaceService = wikiSpaceService;
        this.wikiGroupService = wikiGroupService;
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
    }

    @Override
    @Async("org-pro-sync")
    public void syncOrg(Long orgId) {
        OrganizationE organizationE = iamRepository.queryOrganizationById(orgId);
        if (organizationE != null) {
            logger.info("entry organization is : " + organizationE.getName());
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    organizationE.getId(), WikiSpaceResourceType.ORGANIZATION.getResourceType());
            logger.info("wikiSpaceList size : " + wikiSpaceEList.size());
            if (!wikiSpaceEList.isEmpty() && wikiSpaceEList.get(0).getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
                if (organizationE.getProjectCount() > 0) {
                    setProject(organizationE);
                }
            } else {
                setOrganization(organizationE);
            }
        } else {
            logger.info("failed to get organization, id is " + orgId);
        }
    }

    @Override
    @Async("org-pro-sync")
    public void scanning() {
        List<OrganizationE> organizationEList = new ArrayList<>();
        Page<OrganizationE> pageByOrganization = iamRepository.pageByOrganization(0, 400);
        int page = pageByOrganization.getTotalPages();
        organizationEList.addAll(pageByOrganization.getContent());
        if (page > 1) {
            for (int i = 1; i < page; i++) {
                Page<OrganizationE> list = iamRepository.pageByOrganization(i, 400);
                organizationEList.addAll(list.getContent());
            }
        }

        organizationEList.forEach(organizationE -> {
            if (organizationE.getEnabled()) {
                List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                        organizationE.getId(), WikiSpaceResourceType.ORGANIZATION.getResourceType());
                if (wikiSpaceEList != null && !wikiSpaceEList.isEmpty()) {
                    if (organizationE.getProjectCount() > 0) {
                        setProject(organizationE);
                    }
                } else {
                    setOrganization(organizationE);
                }
            }
        });
    }

    @Override
    @Async("org-pro-sync")
    public void updateWikiPage() {
        updateWikiOrgHomePage();
        updateWikiProjectHomePage();
    }

    public void updateWikiOrgHomePage() {
        List<WikiSpaceE> orgWikiSpaceEList = wikiSpaceRepository.getWikiSpaceByType(
                WikiSpaceResourceType.ORGANIZATION.getResourceType());
        orgWikiSpaceEList.forEach(p -> {
            Map<String, String> params = new HashMap<>();
            params.put("{{ SPACE_ICON }}", p.getIcon());
            params.put("{{ SPACE_TITLE }}", p.getName());
            params.put("{{ SPACE_LABEL }}", p.getName());
            params.put("{{ SPACE_TARGET }}", p.getName());

            InputStream orgIs = this.getClass().getResourceAsStream("/xml/webhome.xml");
            String orgXmlParam = FileUtil.replaceReturnString(orgIs, params);
            iWikiSpaceWebHomeService.createSpace1WebHome(p.getPath(), orgXmlParam, Stage.USERNAME);

            List<WikiSpaceE> orgUnderList = wikiSpaceRepository.getWikiSpaceList(p.getResourceId(),
                    WikiSpaceResourceType.ORGANIZATION_S.getResourceType());
            orgUnderList.forEach(space -> {
                String[] path = space.getPath().split("/");
                Map<String, String> orgUnderParams = new HashMap<>();
                orgUnderParams.put("{{ SPACE_TITLE }}", space.getName());
                orgUnderParams.put("{{ SPACE_LABEL }}", space.getName());
                orgUnderParams.put("{{ SPACE_ICON }}", space.getIcon());
                orgUnderParams.put("{{ SPACE_PARENT }}", path[0]);
                orgUnderParams.put("{{ SPACE_TARGET }}", path[1]);

                InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                String xmlParam = FileUtil.replaceReturnString(inputStream, orgUnderParams);
                iWikiSpaceWebHomeService.createSpace2WebHome(path[0], path[1], xmlParam, Stage.USERNAME);
            });
        });
    }

    public void updateWikiProjectHomePage() {
        List<WikiSpaceE> projectWikiSpaceEList = wikiSpaceRepository.getWikiSpaceByType(
                WikiSpaceResourceType.PROJECT.getResourceType());
        projectWikiSpaceEList.forEach(p -> {
            String[] path = p.getPath().split("/");
            Map<String, String> projectParams = new HashMap<>();
            projectParams.put("{{ SPACE_TITLE }}", p.getName());
            projectParams.put("{{ SPACE_LABEL }}", p.getName());
            projectParams.put("{{ SPACE_ICON }}", p.getIcon());
            projectParams.put("{{ SPACE_PARENT }}", path[0]);
            projectParams.put("{{ SPACE_TARGET }}", path[1]);

            InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
            String xmlParam = FileUtil.replaceReturnString(inputStream, projectParams);
            iWikiSpaceWebHomeService.createSpace2WebHome(path[0], path[1], xmlParam, Stage.USERNAME);

            List<WikiSpaceE> projectUnderlist = wikiSpaceRepository.getWikiSpaceList(p.getResourceId(),
                    WikiSpaceResourceType.PROJECT_S.getResourceType());
            projectUnderlist.forEach(space -> {
                String[] projectPath = space.getPath().split("/");
                Map<String, String> projectUnderParams = new HashMap<>();
                projectUnderParams.put("{{ SPACE_TITLE }}", space.getName());
                projectUnderParams.put("{{ SPACE_LABEL }}", space.getName());
                projectUnderParams.put("{{ SPACE_ICON }}", space.getIcon());
                projectUnderParams.put("{{ SPACE_ROOT }}", projectPath[0]);
                projectUnderParams.put("{{ SPACE_PARENT }}", projectPath[1]);
                projectUnderParams.put("{{ SPACE_TARGET }}", projectPath[2]);

                InputStream is = this.getClass().getResourceAsStream("/xml/webhome2.xml");
                String xml = FileUtil.replaceReturnString(is, projectUnderParams);
                iWikiSpaceWebHomeService.createSpace3WebHome(path[0], path[1], path[2], xml, Stage.USERNAME);
            });
        });
    }

    public void setOrganization(OrganizationE organizationE) {
        logger.info("sync organization : " + organizationE.getName());
        //创建组织
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(organizationE.getName());
        wikiSpaceDTO.setIcon(ORG_ICON);
        wikiSpaceService.create(wikiSpaceDTO, organizationE.getId(), Stage.USERNAME,
                WikiSpaceResourceType.ORGANIZATION.getResourceType());

        String adminGroupName = "O-" + organizationE.getCode() + Stage.ADMIN_GROUP;
        String userGroupName = "O-" + organizationE.getCode() + Stage.USER_GROUP;

        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupDTO.setOrganizationCode(organizationE.getCode());
        wikiGroupDTO.setOrganizationName(organizationE.getName());
        wikiGroupService.create(wikiGroupDTO, Stage.USERNAME, true, true);
        setWikiOrgGroupUser(organizationE, adminGroupName);

        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO, Stage.USERNAME, false, true);

        if (organizationE.getProjectCount() > 0) {
            setProject(organizationE);
        }

        if (!organizationE.getEnabled()) {
            wikiGroupService.disableOrganizationGroup(organizationE.getId(), Stage.USERNAME);
        }
    }

    public void setProject(OrganizationE organizationE) {
        List<ProjectE> projectEList = new ArrayList<>();
        Page<ProjectE> projectEPage = iamRepository.pageByProject(organizationE.getId(), 0, 400);
        int projectPage = projectEPage.getTotalPages();
        projectEList.addAll(projectEPage.getContent());
        if (projectPage > 1) {
            for (int i = 1; i < projectPage; i++) {
                Page<ProjectE> list = iamRepository.pageByProject(organizationE.getId(), i, 400);
                projectEList.addAll(list.getContent());
            }
        }

        projectEList.forEach(projectE -> {
            List<WikiSpaceE> wikiSpaceES = wikiSpaceRepository.getWikiSpaceList(
                    projectE.getId(), WikiSpaceResourceType.PROJECT.getResourceType());
            if (wikiSpaceES == null || wikiSpaceES.isEmpty()) {
                logger.info("sync project : " + projectE.getName());
                WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
                wikiSpaceDTO.setName(organizationE.getName() + "/" + projectE.getName());
                wikiSpaceDTO.setIcon(PROJECT_ICON);
                wikiSpaceService.create(wikiSpaceDTO, projectE.getId(), Stage.USERNAME,
                        WikiSpaceResourceType.PROJECT.getResourceType());

                WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
                String adminGroupName = "P-" + organizationE.getCode() + "-" + projectE.getCode() + Stage.ADMIN_GROUP;
                String userGroupName = "P-" + organizationE.getCode() + "-" + projectE.getCode() + Stage.USER_GROUP;
                wikiGroupDTO.setGroupName(adminGroupName);
                wikiGroupDTO.setProjectCode(projectE.getCode());
                wikiGroupDTO.setProjectName(projectE.getName());
                wikiGroupDTO.setOrganizationName(organizationE.getName());
                wikiGroupDTO.setOrganizationCode(organizationE.getCode());

                //创建组并分配权限
                wikiGroupService.create(wikiGroupDTO, Stage.USERNAME, true, false);
                //管理员给组分配成员
                setWikiProjectGroupUser(projectE, adminGroupName, Stage.ADMIN_GROUP);


                wikiGroupDTO.setGroupName(userGroupName);
                //创建组并分配权限
                wikiGroupService.create(wikiGroupDTO, Stage.USERNAME, false, false);
                //普通用户给组分配成员
                setWikiProjectGroupUser(projectE, userGroupName, Stage.USER_GROUP);

                if (!projectE.getEnabled()) {
                    wikiGroupService.disableProjectGroup(projectE.getId(), Stage.USERNAME);
                }
            }
        });
    }

    public void setWikiProjectGroupUser(ProjectE projectE, String groupName, String group) {
        Page<RoleE> rolePage = null;
        if (group.equals(Stage.ADMIN_GROUP)) {
            rolePage = iamRepository.roleList(InitRoleCode.PROJECT_OWNER);
        } else if (group.equals(Stage.USER_GROUP)) {
            rolePage = iamRepository.roleList(InitRoleCode.PROJECT_MEMBER);
        }

        if (rolePage != null && !rolePage.getContent().isEmpty()) {
            Page<UserE> userEPage = iamRepository.pagingQueryUsersByRoleIdOnProjectLevel(
                    rolePage.get(0).getId(),
                    projectE.getId(),
                    0,
                    400);
            if (userEPage != null && !userEPage.isEmpty()) {
                List<UserE> userEList = new ArrayList<>();
                userEList.addAll(userEPage.getContent());
                int userPage = userEPage.getTotalPages();
                if (userPage > 1) {
                    for (int i = 1; i < userPage; i++) {
                        Page<UserE> list = iamRepository.pagingQueryUsersByRoleIdOnProjectLevel(
                                rolePage.get(0).getId(),
                                projectE.getId(),
                                i,
                                400);
                        userEList.addAll(list.getContent());
                    }
                }

                for (UserE user : userEList) {
                    wikiGroupService.setUserToGroup(groupName, user.getId(), Stage.USERNAME);
                }
            }
        }
    }

    public void setWikiOrgGroupUser(OrganizationE organizationE, String groupName) {
        Page<RoleE> rolePage = iamRepository.roleList(InitRoleCode.ORGANIZATION_ADMINISTRATOR);

        if (rolePage != null && !rolePage.getContent().isEmpty()) {
            Page<UserE> userEPage = iamRepository.pagingQueryUsersByRoleIdOnOrganizationLevel(
                    rolePage.get(0).getId(),
                    organizationE.getId(),
                    0,
                    400);
            if (userEPage != null && !userEPage.isEmpty()) {
                List<UserE> userEList = new ArrayList<>();
                userEList.addAll(userEPage.getContent());
                int userPage = userEPage.getTotalPages();
                if (userPage > 1) {
                    for (int i = 1; i < userPage; i++) {
                        Page<UserE> list = iamRepository.pagingQueryUsersByRoleIdOnOrganizationLevel(
                                rolePage.get(0).getId(),
                                organizationE.getId(),
                                i,
                                400);
                        userEList.addAll(list.getContent());
                    }
                }

                for (UserE user : userEList) {
                    wikiGroupService.setUserToGroup(groupName, user.getId(), Stage.USERNAME);
                }
            }
        }
    }
}

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
import io.choerodon.core.exception.CommonException;
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
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/18.
 */
@Service
public class WikiScanningServiceImpl implements WikiScanningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiScanningServiceImpl.class);

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
                    if (SpaceStatus.SUCCESS.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus()) && organizationE.getProjectCount() > 0) {
                        LOGGER.info("the organization has synchronized, synchronized projects");
                        setProject(organizationE);
                    } else if (SpaceStatus.OPERATIING.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())
                            || SpaceStatus.FAILED.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())) {
                        LOGGER.info("start sync organization again");
                        setOrganization(organizationE, false, true);
                    }
                } else {
                    LOGGER.info("start sync organization");
                    setOrganization(organizationE, true, true);
                }
            }
        });
    }

    @Override
    @Async("org-pro-sync")
    public void syncOrgAndProject(Long orgId) {
        OrganizationE organizationE = iamRepository.queryOrganizationById(orgId);
        if (organizationE != null) {
            LOGGER.info("sync organization,orgId: {} and organization: {} ", orgId, organizationE.toString());
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    organizationE.getId(), WikiSpaceResourceType.ORGANIZATION.getResourceType());
            if (wikiSpaceEList != null && !wikiSpaceEList.isEmpty()) {
                if (SpaceStatus.SUCCESS.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus()) && organizationE.getProjectCount() > 0) {
                    LOGGER.info("the organization has synchronized, synchronized projects");
                    setProject(organizationE);
                } else if (SpaceStatus.OPERATIING.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())
                        || SpaceStatus.FAILED.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())) {
                    LOGGER.info("start sync organization again");
                    setOrganization(organizationE, false, true);
                }
            } else {
                setOrganization(organizationE, true, true);
            }
        } else {
            throw new CommonException("error.organization.not.exist");
        }
    }

    @Override
    public void syncOrg(Long organizationId) {
        OrganizationE organizationE = iamRepository.queryOrganizationById(organizationId);
        if (organizationE != null) {
            LOGGER.info("only sync organization space,orgId: {} and organization: {} ", organizationId, organizationE.toString());
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    organizationE.getId(), WikiSpaceResourceType.ORGANIZATION.getResourceType());
            if (wikiSpaceEList != null && !wikiSpaceEList.isEmpty() &&
                    SpaceStatus.FAILED.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())) {
                LOGGER.info("only sync organization,start...");
                setOrganization(organizationE, false, false);
            } else {
                throw new CommonException("error.conditions.sync.organization");
            }
        } else {
            throw new CommonException("error.organization.not.exist");
        }
    }

    @Override
    public void syncProject(Long projectId) {
        ProjectE projectE = iamRepository.queryIamProject(projectId);
        if (projectE != null) {
            LOGGER.info("only sync project,projectId: {} and project: {} ", projectId, projectE.toString());
            List<WikiSpaceE> projectSpaceList = wikiSpaceRepository.getWikiSpaceList(
                    projectE.getId(), WikiSpaceResourceType.PROJECT.getResourceType());
            if (projectSpaceList != null && !projectSpaceList.isEmpty() &&
                    SpaceStatus.FAILED.getSpaceStatus().equals(projectSpaceList.get(0).getStatus())) {
                Long orgId = projectE.getOrganization().getId();
                List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                        orgId, WikiSpaceResourceType.ORGANIZATION.getResourceType());
                if (wikiSpaceEList != null && !wikiSpaceEList.isEmpty() &&
                        SpaceStatus.SUCCESS.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())) {
                    OrganizationE organization = iamRepository.queryOrganizationById(orgId);
                    setProject(organization);
                } else {
                    throw new CommonException("error.organization.space.not.success");
                }
            } else {
                throw new CommonException("error.conditions.sync.project");
            }
        } else {
            throw new CommonException("error.project.not.exist");
        }
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
            LOGGER.info("modify the home page of the organization space, {}", p);
            Map<String, String> params = new HashMap<>(16);
            params.put("{{ SPACE_ICON }}", p.getIcon());
            params.put("{{ SPACE_TITLE }}", p.getName());
            params.put("{{ SPACE_LABEL }}", p.getName());
            params.put("{{ SPACE_TARGET }}", p.getName().replace(".", "\\."));

            InputStream orgIs = this.getClass().getResourceAsStream("/xml/webhome.xml");
            String orgXmlParam = FileUtil.replaceReturnString(orgIs, params);
            iWikiSpaceWebHomeService.createSpace1WebHome(p.getId(),p.getPath(), orgXmlParam, BaseStage.USERNAME);

            List<WikiSpaceE> orgUnderList = wikiSpaceRepository.getWikiSpaceList(p.getResourceId(),
                    WikiSpaceResourceType.ORGANIZATION_S.getResourceType());
            orgUnderList.forEach(space -> {
                LOGGER.info("modify the home page of the space under the organization, {}", space);
                String[] path = space.getPath().split("/");
                Map<String, String> orgUnderParams = new HashMap<>(16);
                orgUnderParams.put("{{ SPACE_TITLE }}", space.getName());
                orgUnderParams.put("{{ SPACE_LABEL }}", space.getName());
                orgUnderParams.put("{{ SPACE_ICON }}", space.getIcon());
                orgUnderParams.put("{{ SPACE_PARENT }}", path[0].replace(".", "\\."));
                orgUnderParams.put("{{ SPACE_TARGET }}", path[1].replace(".", "\\."));

                InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                String xmlParam = FileUtil.replaceReturnString(inputStream, orgUnderParams);
                iWikiSpaceWebHomeService.createSpace2WebHome(p.getId(),path[0], path[1], xmlParam, BaseStage.USERNAME);
            });
        });
    }

    public void updateWikiProjectHomePage() {
        List<WikiSpaceE> projectWikiSpaceEList = wikiSpaceRepository.getWikiSpaceByType(
                WikiSpaceResourceType.PROJECT.getResourceType());
        projectWikiSpaceEList.forEach(p -> {
            LOGGER.info("modify the home page of the project space, {}", p);
            String[] projectPath = p.getPath().split("/");
            Map<String, String> projectParams = new HashMap<>(16);
            projectParams.put("{{ SPACE_TITLE }}", p.getName());
            projectParams.put("{{ SPACE_LABEL }}", p.getName());
            projectParams.put("{{ SPACE_ICON }}", p.getIcon());
            projectParams.put("{{ SPACE_PARENT }}", projectPath[0].replace(".", "\\."));
            projectParams.put("{{ SPACE_TARGET }}", projectPath[1].replace(".", "\\."));

            InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
            String xmlParam = FileUtil.replaceReturnString(inputStream, projectParams);
            iWikiSpaceWebHomeService.createSpace2WebHome(p.getId(),projectPath[0], projectPath[1], xmlParam, BaseStage.USERNAME);

            List<WikiSpaceE> projectUnderlist = wikiSpaceRepository.getWikiSpaceList(p.getResourceId(),
                    WikiSpaceResourceType.PROJECT_S.getResourceType());
            projectUnderlist.forEach(space -> {
                LOGGER.info("modify the home page of the space under the project, {}", space);
                String[] projectUnderPath = space.getPath().split("/");
                Map<String, String> projectUnderParams = new HashMap<>(16);
                projectUnderParams.put("{{ SPACE_TITLE }}", space.getName());
                projectUnderParams.put("{{ SPACE_LABEL }}", space.getName());
                projectUnderParams.put("{{ SPACE_ICON }}", space.getIcon());
                projectUnderParams.put("{{ SPACE_ROOT }}", projectUnderPath[0].replace(".", "\\."));
                projectUnderParams.put("{{ SPACE_PARENT }}", projectUnderPath[1].replace(".", "\\."));
                projectUnderParams.put("{{ SPACE_TARGET }}", projectUnderPath[2].replace(".", "\\."));

                InputStream is = this.getClass().getResourceAsStream("/xml/webhome2.xml");
                String xml = FileUtil.replaceReturnString(is, projectUnderParams);
                iWikiSpaceWebHomeService.createSpace3WebHome(p.getId(),projectUnderPath[0], projectUnderPath[1], projectUnderPath[2], xml, BaseStage.USERNAME);
            });
        });
    }

    public void setOrganization(OrganizationE organizationE, Boolean flag, Boolean onlyOrganization) {
        LOGGER.info("sync organization: {} ", organizationE.getName());
        //创建组织
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(organizationE.getName());
        wikiSpaceDTO.setIcon(ORG_ICON);
        wikiSpaceService.create(wikiSpaceDTO, organizationE.getId(), BaseStage.USERNAME,
                WikiSpaceResourceType.ORGANIZATION.getResourceType(), flag);

        String adminGroupName = BaseStage.O + organizationE.getCode() + BaseStage.ADMIN_GROUP;
        String userGroupName = BaseStage.O + organizationE.getCode() + BaseStage.USER_GROUP;

        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupDTO.setOrganizationCode(organizationE.getCode());
        wikiGroupDTO.setOrganizationName(organizationE.getName());
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, true, true);
        setWikiOrgGroupUser(organizationE, adminGroupName);

        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, false, true);

        if (onlyOrganization && organizationE.getProjectCount() > 0) {
            setProject(organizationE);
        }

        if (!organizationE.getEnabled()) {
            wikiGroupService.disableOrganizationGroup(organizationE.getId(), BaseStage.USERNAME);
        }
    }

    public void setProject(OrganizationE organizationE) {
        LOGGER.info("start sync project, organizationE: {}", organizationE.toString());
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
                LOGGER.info("the first sync project");
                createWikiProjectSpace(organizationE, projectE, true);
            } else if (wikiSpaceES != null && !wikiSpaceES.isEmpty()
                    && (SpaceStatus.OPERATIING.getSpaceStatus().equals(wikiSpaceES.get(0).getStatus())
                    || SpaceStatus.FAILED.getSpaceStatus().equals(wikiSpaceES.get(0).getStatus()))) {
                LOGGER.info("sync project again");
                createWikiProjectSpace(organizationE, projectE, false);
            }
        });
    }

    public void createWikiProjectSpace(OrganizationE organizationE, ProjectE projectE, Boolean flag) {
        LOGGER.info("sync project: {}", projectE.getName());
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(organizationE.getName() + "/" + projectE.getName());
        wikiSpaceDTO.setIcon(PROJECT_ICON);
        wikiSpaceService.create(wikiSpaceDTO, projectE.getId(), BaseStage.USERNAME,
                WikiSpaceResourceType.PROJECT.getResourceType(), flag);

        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        String adminGroupName = BaseStage.P + organizationE.getCode() + BaseStage.LINE + projectE.getCode() + BaseStage.ADMIN_GROUP;
        String userGroupName = BaseStage.P + organizationE.getCode() + BaseStage.LINE + projectE.getCode() + BaseStage.USER_GROUP;
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupDTO.setProjectCode(projectE.getCode());
        wikiGroupDTO.setProjectName(projectE.getName());
        wikiGroupDTO.setOrganizationName(organizationE.getName());
        wikiGroupDTO.setOrganizationCode(organizationE.getCode());

        //创建组并分配权限
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, true, false);
        //管理员给组分配成员
        setWikiProjectGroupUser(projectE, adminGroupName, BaseStage.ADMIN_GROUP);


        wikiGroupDTO.setGroupName(userGroupName);
        //创建组并分配权限
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, false, false);
        //普通用户给组分配成员
        setWikiProjectGroupUser(projectE, userGroupName, BaseStage.USER_GROUP);

        if (!projectE.getEnabled()) {
            wikiGroupService.disableProjectGroup(projectE.getId(), BaseStage.USERNAME);
        }
    }

    public void setWikiProjectGroupUser(ProjectE projectE, String groupName, String group) {
        Page<RoleE> rolePage = null;
        if (group.equals(BaseStage.ADMIN_GROUP)) {
            rolePage = iamRepository.roleList(InitRoleCode.PROJECT_OWNER);
        } else if (group.equals(BaseStage.USER_GROUP)) {
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
                    wikiGroupService.setUserToGroup(groupName, user.getId(), BaseStage.USERNAME);
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
                    wikiGroupService.setUserToGroup(groupName, user.getId(), BaseStage.USERNAME);
                }
            }
        }
    }
}

package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.app.service.WikiScanningService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.application.entity.iam.LabelE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.RoleE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.common.enums.WikiRoleType;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;
import io.choerodon.wiki.infra.dataobject.iam.RoleDO;
import io.choerodon.wiki.infra.dataobject.iam.UserWithRoleDO;

/**
 * Created by Zenger on 2018/7/18.
 */
@Service
public class WikiScanningServiceImpl implements WikiScanningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiScanningServiceImpl.class);

    private IamRepository iamRepository;
    private WikiSpaceRepository wikiSpaceRepository;
    private WikiSpaceService wikiSpaceService;
    private WikiGroupService wikiGroupService;
    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService;
    private IWikiGroupService iWikiGroupService;
    private IWikiUserService iWikiUserService;

    public WikiScanningServiceImpl(IamRepository iamRepository,
                                   WikiSpaceRepository wikiSpaceRepository,
                                   WikiSpaceService wikiSpaceService,
                                   WikiGroupService wikiGroupService,
                                   IWikiSpaceWebHomeService iWikiSpaceWebHomeService,
                                   IWikiGroupService iWikiGroupService,
                                   IWikiUserService iWikiUserService) {
        this.iamRepository = iamRepository;
        this.wikiSpaceRepository = wikiSpaceRepository;
        this.wikiSpaceService = wikiSpaceService;
        this.wikiGroupService = wikiGroupService;
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
        this.iWikiGroupService = iWikiGroupService;
        this.iWikiUserService = iWikiUserService;
    }

    @Override
    @Async("org-pro-sync")
    public void scanning() {
        List<OrganizationE> organizationEList = iamRepository.pageByOrganization(0, 0);

        for (OrganizationE organizationE : organizationEList) {
            try {
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
            } catch (Exception e) {
                LOGGER.error(String.valueOf(e));
            }
        }
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
                    organizationE.setName(wikiSpaceEList.get(0).getPath().substring(2).replace(".", "\\."));
                    setProject(organizationE);
                } else if (SpaceStatus.OPERATIING.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())
                        || SpaceStatus.FAILED.getSpaceStatus().equals(wikiSpaceEList.get(0).getStatus())) {
                    LOGGER.info("start sync organization again");
                    organizationE.setName(wikiSpaceEList.get(0).getPath().substring(2).replace(".", "\\."));
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
                organizationE.setName(wikiSpaceEList.get(0).getPath().substring(2).replace(".", "\\."));
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
                    organization.setName(wikiSpaceEList.get(0).getPath().substring(2).replace(".", "\\."));
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
    public void updateWikiPage() {
        iWikiSpaceWebHomeService.updateWikiSpaceResource();
    }

    @Override
    @Async("org-pro-sync")
    public void syncOrganizationUserGroup() {
        List<OrganizationE> organizationEList = iamRepository.pageByOrganization(0, 0);
        for (OrganizationE organizationE : organizationEList) {
            if (organizationE.getProjectCount() > 0) {
                getProjectInfo(organizationE);
            }
        }
    }

    @Override
    @Async("org-pro-sync")
    public void syncXWikiAdminGroup() {
        List<UserWithRoleDO> userWithRoleDOList = iamRepository.pagingQueryUsersWithSiteLevelRoles(0, 0);

        for (UserWithRoleDO ur : userWithRoleDOList) {
            List<RoleDO> roles = ur.getRoles();
            for (RoleDO role : roles) {
                if (role.getCode().equals(BaseStage.SITE_ADMINISTRATOR)) {
                    if (!iWikiUserService.checkDocExsist(BaseStage.USERNAME, ur.getLoginName())) {
                        WikiUserE wikiUserE = new WikiUserE();
                        wikiUserE.setLastName(ur.getRealName());
                        wikiUserE.setFirstName(ur.getLoginName());
                        wikiUserE.setEmail(ur.getEmail());
                        wikiUserE.setPhone(ur.getPhone());
                        String xmlParam = getUserXml(wikiUserE);
                        if (!iWikiUserService.createUser(ur.getLoginName(), xmlParam, BaseStage.USERNAME)) {
                            throw new CommonException("error.wiki.user.create");
                        }
                    }
                    List<Integer> list = wikiGroupService.getGroupsObjectNumber(BaseStage.XWIKI_ADMIN_GROUP, BaseStage.USERNAME, ur.getLoginName());
                    if (list == null || list.isEmpty()) {
                        iWikiGroupService.createGroupUsers(BaseStage.XWIKI_ADMIN_GROUP, ur.getLoginName(), BaseStage.USERNAME);
                    }
                    break;
                }
            }
        }
    }

    @Override
    @Async("org-pro-sync")
    public void updateGrpupUsers() {
        //更新wiki默认组XWikiAdminGroup
        updateWikiGroupUsers(BaseStage.XWIKI_ADMIN_GROUP);

        //更新wiki默认组XWikiAllGroup
        updateWikiGroupUsers(BaseStage.XWIKI_ALL_GROUP);

        //更新组织创建的组
        List<WikiSpaceE> orgWikiSpaceEList = wikiSpaceRepository.getWikiSpaceByType(
                WikiSpaceResourceType.ORGANIZATION.getResourceType());
        for (WikiSpaceE o : orgWikiSpaceEList) {
            OrganizationE organizationE = iamRepository.queryOrganizationById(o.getResourceId());
            String adminGroupName = BaseStage.O + organizationE.getCode() + BaseStage.ADMIN_GROUP;
            String userGroupName = BaseStage.O + organizationE.getCode() + BaseStage.USER_GROUP;
            updateWikiGroupUsers(adminGroupName);
            updateWikiGroupUsers(userGroupName);
        }

        //更新项目创建的组
        List<WikiSpaceE> projectWikiSpaceEList = wikiSpaceRepository.getWikiSpaceByType(
                WikiSpaceResourceType.PROJECT.getResourceType());
        for (WikiSpaceE p : projectWikiSpaceEList) {
            ProjectE projectE = iamRepository.queryIamProject(p.getResourceId());
            if (projectE != null) {
                Long orgId = projectE.getOrganization().getId();
                OrganizationE organization = iamRepository.queryOrganizationById(orgId);
                String adminGroupName = BaseStage.P + organization.getCode() + BaseStage.LINE + projectE.getCode() + BaseStage.ADMIN_GROUP;
                String userGroupName = BaseStage.P + organization.getCode() + BaseStage.LINE + projectE.getCode() + BaseStage.USER_GROUP;
                updateWikiGroupUsers(adminGroupName);
                updateWikiGroupUsers(userGroupName);
            }
        }
    }

    private String getUserXml(WikiUserE wikiUserE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/user.xml");
        Map<String, String> params = new HashMap<>(16);
        params.put("{{ LAST_NAME }}", wikiUserE.getLastName());
        params.put("{{ FIRST_NAME }}", wikiUserE.getFirstName());
        params.put("{{ USER_EMAIL }}", wikiUserE.getEmail());
        params.put("{{ PHONE }}", wikiUserE.getPhone());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private void updateWikiGroupUsers(String groupName) {
        try {
            List<String> list = wikiGroupService.getGroupsUsers(groupName, BaseStage.USERNAME);
            for (String s : list) {
                iWikiGroupService.createGroupUsers(groupName,
                        s,
                        BaseStage.USERNAME);
            }
        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
        }
    }

    public void setOrganization(OrganizationE organizationE, Boolean flag, Boolean onlyOrganization) {
        LOGGER.info("sync organization: {} ", organizationE.getName());
        //创建组织
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(organizationE.getName());
        wikiSpaceDTO.setIcon(BaseStage.ORG_ICON);
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
        List<ProjectE> projectEList = iamRepository.pageByProject(organizationE.getId(), 0, 0);

        for (ProjectE projectE : projectEList) {
            try {
                List<WikiSpaceE> wikiSpaceES = wikiSpaceRepository.getWikiSpaceList(
                        projectE.getId(), WikiSpaceResourceType.PROJECT.getResourceType());
                if (wikiSpaceES == null || wikiSpaceES.isEmpty()) {
                    LOGGER.info("the first sync project");
                    createWikiProjectSpace(organizationE, projectE, true);
                } else if (!wikiSpaceES.isEmpty()
                        && (SpaceStatus.OPERATIING.getSpaceStatus().equals(wikiSpaceES.get(0).getStatus())
                        || SpaceStatus.FAILED.getSpaceStatus().equals(wikiSpaceES.get(0).getStatus()))) {
                    LOGGER.info("sync project again");
                    String[] path = wikiSpaceES.get(0).getPath().split("/");
                    projectE.setName(path[1].substring(2).replace(".", "\\."));
                    createWikiProjectSpace(organizationE, projectE, false);
                }
            } catch (Exception e) {
                LOGGER.error(String.valueOf(e));
            }
        }
    }

    public void getProjectInfo(OrganizationE organizationE) {
        LOGGER.info("get project information, organizationE: {}", organizationE.toString());
        List<ProjectE> projectEList = iamRepository.pageByProject(organizationE.getId(), 0, 0);

        for (ProjectE projectE : projectEList) {
            try {
                List<UserWithRoleDO> userWithRoleDOPage = iamRepository.pagingQueryUsersWithProjectLevelRoles(projectE.getId());
                userWithRoleDOPage.stream().forEach(u -> {
                    u.getRoles().stream().forEach(r -> {
                        RoleE roleE = iamRepository.queryWithPermissionsAndLabels(r.getId());
                        if (roleE.getLabels() != null) {
                            List<LabelE> labelEList = roleE.getLabels();
                            for (LabelE label : labelEList) {
                                if (label.getName().equals(WikiRoleType.PROJECT_WIKI_USER.getResourceType())
                                        || label.getName().equals(WikiRoleType.PROJECT_WIKI_ADMIN.getResourceType())) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(BaseStage.O).append(organizationE.getCode()).append(BaseStage.USER_GROUP);
                                    List<Integer> list = wikiGroupService.getGroupsObjectNumber(stringBuilder.toString(), BaseStage.USERNAME, u.getLoginName());
                                    if (list == null || list.isEmpty()) {
                                        iWikiGroupService.createGroupUsers(stringBuilder.toString(), u.getLoginName(), BaseStage.USERNAME);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                });
            } catch (Exception e) {
                LOGGER.error(String.valueOf(e));
            }
        }
    }

    public void createWikiProjectSpace(OrganizationE organizationE, ProjectE projectE, Boolean flag) {
        LOGGER.info("sync project: {}", projectE.getName());
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(organizationE.getName() + "/" + projectE.getName());
        wikiSpaceDTO.setIcon(BaseStage.PROJECT_ICON);
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
        List<RoleE> rolePage = null;
        if (group.equals(BaseStage.ADMIN_GROUP)) {
            rolePage = iamRepository.roleList(InitRoleCode.PROJECT_OWNER);
        } else if (group.equals(BaseStage.USER_GROUP)) {
            rolePage = iamRepository.roleList(InitRoleCode.PROJECT_MEMBER);
        }

        if (rolePage != null && !rolePage.isEmpty()) {
            List<UserE> userEList = iamRepository.pagingQueryUsersByRoleIdOnProjectLevel(
                    rolePage.get(0).getId(),
                    projectE.getId(),
                    0,
                    0);
            if (userEList != null && !userEList.isEmpty()) {
                for (UserE user : userEList) {
                    wikiGroupService.setUserToGroup(groupName, user.getId(), BaseStage.USERNAME);
                }
            }
        }
    }

    public void setWikiOrgGroupUser(OrganizationE organizationE, String groupName) {
        List<RoleE> rolePage = iamRepository.roleList(InitRoleCode.ORGANIZATION_ADMINISTRATOR);

        if (rolePage != null && !rolePage.isEmpty()) {
            List<UserE> userEList = iamRepository.pagingQueryUsersByRoleIdOnOrganizationLevel(
                    rolePage.get(0).getId(),
                    organizationE.getId(),
                    0,
                    0);
            if (userEList != null && !userEList.isEmpty()) {
                for (UserE user : userEList) {
                    wikiGroupService.setUserToGroup(groupName, user.getId(), BaseStage.USERNAME);
                }
            }
        }
    }
}

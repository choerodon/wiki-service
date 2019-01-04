package io.choerodon.wiki.api.eventhandler;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.api.dto.*;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.app.service.WikiLogoService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.event.OrganizationEventPayload;
import io.choerodon.wiki.domain.application.event.ProjectEvent;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/5.
 */
@Component
public class WikiEventHandler {

    private static final String ORG_ICON = "domain";
    private static final String PROJECT_ICON = "project";
    public static final String PROJECT_UPDATE = "iam-update-project";
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiEventHandler.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Gson gson = new Gson();

    private WikiSpaceService wikiSpaceService;
    private WikiGroupService wikiGroupService;
    private WikiLogoService wikiLogoService;
    private IamRepository iamRepository;
    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService;

    public WikiEventHandler(WikiSpaceService wikiSpaceService,
                            WikiGroupService wikiGroupService,
                            WikiLogoService wikiLogoService,
                            IamRepository iamRepository,
                            IWikiSpaceWebHomeService iWikiSpaceWebHomeService) {
        this.wikiSpaceService = wikiSpaceService;
        this.wikiGroupService = wikiGroupService;
        this.wikiLogoService = wikiLogoService;
        this.iamRepository = iamRepository;
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
    }

    private void loggerInfo(Object o) {
        LOGGER.info("request data: {}", o);
    }


    /**
     * wiki注册组织监听
     */
    @SagaTask(code = "wikiRegisterOrganization",
            description = "wiki注册组织监听",
            sagaCode = "org-register",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleRegisterOrganizationCreateEvent(String data) {
        RegisterOrganizationDTO registerOrganizationDTO = gson.fromJson(data, RegisterOrganizationDTO.class);
        loggerInfo(registerOrganizationDTO);

        createOrganization(registerOrganizationDTO.getOrganizationId(),
                registerOrganizationDTO.getOrganizationCode(),
                registerOrganizationDTO.getOrganizationName(),
                registerOrganizationDTO.getUserId());
        return data;
    }

    /**
     * wiki服务的创建组织监听
     */
    @SagaTask(code = "wikiCreateOrganization",
            description = "wiki服务的创建组织监听",
            sagaCode = "org-create-organization",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleOrganizationCreateEvent(String data) throws IOException {
        loggerInfo(data);
        OrganizationEventPayload organizationEventPayload = objectMapper.readValue(data, OrganizationEventPayload.class);
        createOrganization(organizationEventPayload.getId(),
                organizationEventPayload.getCode(),
                organizationEventPayload.getName(),
                organizationEventPayload.getUserId());

        return data;
    }

    /**
     * wiki服务的创建项目监听
     */
    @SagaTask(code = "wikiCreateProject",
            description = "wiki服务的创建项目监听",
            sagaCode = "iam-create-project",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleProjectCreateEvent(String data) throws IOException {
        loggerInfo(data);
        ProjectEvent projectEvent = objectMapper.readValue(data, ProjectEvent.class);
        createProject(projectEvent.getOrganizationCode(),
                projectEvent.getProjectCode(),
                projectEvent.getOrganizationName(),
                projectEvent.getProjectName(),
                projectEvent.getProjectId(),
                projectEvent.getUserId());

        return data;
    }


    /**
     * wiki服务的角色分配监听
     */
    @SagaTask(code = "wikiUpdateMemberRole",
            description = "wiki服务的角色分配监听",
            sagaCode = "iam-update-memberRole",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public String handleCreateGroupMemberEvent(String data) {
        loggerInfo(data);
        List<GroupMemberDTO> groupMemberDTOList = gson.fromJson(data,
                new TypeToken<List<GroupMemberDTO>>() {
                }.getType());
        wikiGroupService.createWikiGroupUsers(groupMemberDTOList, BaseStage.USERNAME);

        return data;
    }

    /**
     * wiki服务的去除角色监听
     */
    @SagaTask(code = "wikiDeleteMemberRole",
            description = "wiki服务的去除角色监听",
            sagaCode = "iam-delete-memberRole",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public String handledeleteMemberRoleEvent(String data) {
        loggerInfo(data);
        List<GroupMemberDTO> groupMemberDTOList = gson.fromJson(data,
                new TypeToken<List<GroupMemberDTO>>() {
                }.getType());
        wikiGroupService.deleteWikiGroupUsers(groupMemberDTOList, BaseStage.USERNAME);
        return data;
    }

    /**
     * wiki服务的用户创建监听
     */
    @SagaTask(code = "wikiCreateUser",
            description = "wiki服务的用户创建监听",
            sagaCode = "iam-create-user",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public String handleCreateUserEvent(String data) {
        loggerInfo(data);
        List<UserDTO> userDTOList = gson.fromJson(data,
                new TypeToken<List<UserDTO>>() {
                }.getType());
        wikiGroupService.createWikiUserToGroup(userDTOList, BaseStage.USERNAME);
        return data;
    }

    /**
     * wiki服务的组织禁用监听
     */
    @SagaTask(code = "wikiDisableOrganization",
            description = "wiki服务的组织禁用监听",
            sagaCode = "iam-disable-organization",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleOrganizationDisableEvent(String data) throws IOException {
        loggerInfo(data);
        OrganizationDTO organizationDTO = objectMapper.readValue(data, OrganizationDTO.class);
        wikiGroupService.disableOrganizationGroup(organizationDTO.getOrganizationId(), BaseStage.USERNAME);
        return data;
    }

    /**
     * wiki服务的项目禁用监听
     */
    @SagaTask(code = "wikiDisableProject",
            description = "wiki服务的项目禁用监听",
            sagaCode = "iam-disable-project",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleProjectDisableEvent(String data) throws IOException {
        loggerInfo(data);
        ProjectDTO projectDTO = objectMapper.readValue(data, ProjectDTO.class);
        wikiGroupService.disableProjectGroup(projectDTO.getProjectId(), BaseStage.USERNAME);
        return data;
    }

    /**
     * wiki服务的组织启用监听
     */
    @SagaTask(code = "wikiEnableOrganization",
            description = "wiki服务的组织启用监听",
            sagaCode = "iam-enable-organization",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleOrganizationEnableEvent(String data) throws IOException {
        loggerInfo(data);
        OrganizationDTO organizationDTO = objectMapper.readValue(data, OrganizationDTO.class);
        OrganizationE organization = iamRepository.queryOrganizationById(organizationDTO.getOrganizationId());
        if (!iWikiSpaceWebHomeService.checkOrgSpaceExsist(BaseStage.O + organization.getName(), BaseStage.USERNAME)) {
            createOrganization(organization.getId(),
                    organization.getCode(),
                    organization.getName(),
                    organization.getUserId());
        } else {
            wikiGroupService.enableOrganizationGroup(organization, BaseStage.USERNAME);
        }
        return data;
    }

    // 项目修改名称同步
    @SagaTask(code = "wikiProjectUpdate",
            description = "项目修改名称同步",
            sagaCode = PROJECT_UPDATE,
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public void dealProjectUpdateSync(String data) throws IOException {
        loggerInfo(data);
        ProjectEvent projectEvent = objectMapper.readValue(data, ProjectEvent.class);
        wikiSpaceService.updateAndSyncProject(projectEvent);
    }

    /**
     * wiki服务的项目启用监听
     */
    @SagaTask(code = "wikiEnableOrganization",
            description = "wiki服务的项目启用监听",
            sagaCode = "iam-enable-project",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleProjectEnableEvent(String data) throws IOException {
        loggerInfo(data);
        ProjectDTO projectDTO = objectMapper.readValue(data, ProjectDTO.class);
        ProjectE projectE = iamRepository.queryIamProject(projectDTO.getProjectId());
        if (projectE != null) {
            OrganizationE organization = iamRepository.queryOrganizationById(projectE.getOrganization().getId());
            if (!iWikiSpaceWebHomeService.checkProjectSpaceExsist(BaseStage.O + organization.getName(),
                    BaseStage.P + projectE.getName(), BaseStage.USERNAME)) {
                createProject(organization.getCode(),
                        projectE.getCode(),
                        organization.getName(),
                        projectE.getName(),
                        projectE.getId(),
                        organization.getUserId());
            } else {
                wikiGroupService.enableProjectGroup(organization, projectE, BaseStage.USERNAME);
            }
        } else {
            throw new CommonException("error.query.project");
        }

        return data;
    }

    /**
     * wiki服务的Logo修改监听
     */
    @SagaTask(code = "wikiUpdateLogo",
            description = "wiki服务的Logo修改监听",
            sagaCode = "iam-update-system-setting",
            maxRetryCount = 3,
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 10)
    public String handleLogoUpdateEvent(String data) throws IOException {
        loggerInfo(data);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WikiLogoDTO wikiLogoDTO = objectMapper.readValue(data, WikiLogoDTO.class);
        wikiLogoService.updateLogo(wikiLogoDTO, BaseStage.USERNAME);
        return data;
    }

    private void createOrganization(Long orgId, String orgCode, String orgName, Long userId) {
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(orgName);
        wikiSpaceDTO.setIcon(ORG_ICON);
        wikiSpaceService.create(wikiSpaceDTO, orgId, BaseStage.USERNAME,
                WikiSpaceResourceType.ORGANIZATION.getResourceType(), true);

        String adminGroupName = BaseStage.O + orgCode + BaseStage.ADMIN_GROUP;
        String userGroupName = BaseStage.O + orgCode + BaseStage.USER_GROUP;

        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupDTO.setOrganizationCode(orgCode);
        wikiGroupDTO.setOrganizationName(orgName);
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, true, true);

        wikiGroupService.setUserToGroup(adminGroupName, userId, BaseStage.USERNAME);

        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, false, true);
    }

    private void createProject(String orgCode, String projectCode, String orgName, String projectName, Long projectId, Long userId) {
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(orgName + "/" + projectName);
        wikiSpaceDTO.setIcon(PROJECT_ICON);
        wikiSpaceService.create(wikiSpaceDTO, projectId, BaseStage.USERNAME,
                WikiSpaceResourceType.PROJECT.getResourceType(), true);
        //创建组
        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        String adminGroupName = BaseStage.P + orgCode + BaseStage.LINE + projectCode + BaseStage.ADMIN_GROUP;
        String userGroupName = BaseStage.P + orgCode + BaseStage.LINE + projectCode + BaseStage.USER_GROUP;
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupDTO.setProjectCode(projectCode);
        wikiGroupDTO.setProjectName(projectName);
        wikiGroupDTO.setOrganizationName(orgName);
        wikiGroupDTO.setOrganizationCode(orgCode);
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, true, false);
        wikiGroupService.setUserToGroup(adminGroupName, userId, BaseStage.USERNAME);
        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO, BaseStage.USERNAME, false, false);
    }
}

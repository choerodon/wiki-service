package io.choerodon.wiki.api.eventhandler;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.wiki.api.dto.*;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.event.OrganizationEventPayload;
import io.choerodon.wiki.domain.application.event.ProjectEvent;
import io.choerodon.wiki.infra.common.Stage;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/5.
 */
@Component
public class WikiEventHandler {

    private static final String ORG_ICON = "domain";
    private static final String PROJECT_ICON = "project";
    private static final String USERNAME = "admin";
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiEventHandler.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    private WikiSpaceService wikiSpaceService;
    private WikiGroupService wikiGroupService;

    public WikiEventHandler(WikiSpaceService wikiSpaceService, WikiGroupService wikiGroupService) {
        this.wikiSpaceService = wikiSpaceService;
        this.wikiGroupService = wikiGroupService;
    }

    private void loggerInfo(Object o) {
        LOGGER.info("data: {}", o);
    }

    /**
     * 创建组织事件
     */
//    @EventListener(topic = ORG_SERVICE, businessType = "createOrganizationToDevops")
    @SagaTask(code = "wikiCreateOrganization",
            description = "wiki服务的创建组织监听",
            sagaCode = "iam-create-organization",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public String handleOrganizationCreateEvent(String data) throws IOException {
        OrganizationEventPayload organizationEventPayload = objectMapper.readValue(data, OrganizationEventPayload.class);
        loggerInfo(organizationEventPayload);
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(organizationEventPayload.getName());
        wikiSpaceDTO.setIcon(ORG_ICON);
        wikiSpaceService.create(wikiSpaceDTO, organizationEventPayload.getId(), USERNAME,
                WikiSpaceResourceType.ORGANIZATION.getResourceType());

        String adminGroupName = "O-" + organizationEventPayload.getCode() + Stage.ADMIN_GROUP;
        String userGroupName = "O-" + organizationEventPayload.getCode() + Stage.USER_GROUP;

        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupDTO.setOrganizationCode(organizationEventPayload.getCode());
        wikiGroupDTO.setOrganizationName(organizationEventPayload.getName());
        wikiGroupService.create(wikiGroupDTO, USERNAME, true, true);

        wikiGroupService.setUserToGroup(adminGroupName, organizationEventPayload.getUserId(), USERNAME);

        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO, USERNAME, false, true);

        return data;
    }

    /**
     * 创建项目事件
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "createProject")
    @SagaTask(code = "wikiCreateProject",
            description = "wiki服务的创建项目监听",
            sagaCode = "iam-create-project",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public String handleProjectCreateEvent(String data) throws IOException {
        ProjectEvent projectEvent = objectMapper.readValue(data, ProjectEvent.class);
//        ProjectEvent projectEvent = payload.getData();
        loggerInfo(projectEvent);
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(projectEvent.getOrganizationName() + "/" + projectEvent.getProjectName());
        wikiSpaceDTO.setIcon(PROJECT_ICON);
        wikiSpaceService.create(wikiSpaceDTO, projectEvent.getProjectId(), USERNAME,
                WikiSpaceResourceType.PROJECT.getResourceType());
        //创建组
        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        String adminGroupName = "P-" + projectEvent.getOrganizationCode() + "-" + projectEvent.getProjectCode() + Stage.ADMIN_GROUP;
        String userGroupName = "P-" + projectEvent.getOrganizationCode() + "-" + projectEvent.getProjectCode() + Stage.USER_GROUP;
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupDTO.setProjectCode(projectEvent.getProjectCode());
        wikiGroupDTO.setProjectName(projectEvent.getProjectName());
        wikiGroupDTO.setOrganizationName(projectEvent.getOrganizationName());
        wikiGroupDTO.setOrganizationCode(projectEvent.getOrganizationCode());
        wikiGroupService.create(wikiGroupDTO, USERNAME, true, false);
        wikiGroupService.setUserToGroup(adminGroupName, projectEvent.getUserId(), USERNAME);
        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO, USERNAME, false, false);

        return data;
    }


    /**
     * 角色分配
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "updateMemberRole")
    @SagaTask(code = "wikiUpdateMemberRole",
            description = "wiki服务的角色分配监听",
            sagaCode = "iam-update-memberRole",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 2)
    public String handleCreateGroupMemberEvent(String data) throws IOException {
        List<GroupMemberDTO> groupMemberDTOList = objectMapper.readValue(data, List.class);
        loggerInfo(groupMemberDTOList);
        wikiGroupService.createWikiGroupUsers(groupMemberDTOList, USERNAME);

        return data;
    }

    /**
     * 角色同步事件,去除角色
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "deleteMemberRole")
    @SagaTask(code = "wikiDeleteMemberRole",
            description = "wiki服务的去除角色监听",
            sagaCode = "iam-delete-memberRole",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 2)
    public String handledeleteMemberRoleEvent(String data) throws IOException {
        List<GroupMemberDTO> groupMemberDTOList = objectMapper.readValue(data, List.class);
        loggerInfo(groupMemberDTOList);
        wikiGroupService.deleteWikiGroupUsers(groupMemberDTOList, USERNAME);
        return data;
    }

    /**
     * 用户创建
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "createUser")
    @SagaTask(code = "wikiCreateUser",
            description = "wiki服务的用户创建监听",
            sagaCode = "iam-create-user",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 3)
    public String handleCreateUserEvent(String data) throws IOException {
        UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
        wikiGroupService.createWikiUserToGroup(userDTO, USERNAME);
        return data;
    }

    /**
     * 组织禁用
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "disableOrganization")
    @SagaTask(code = "wikiDisableOrganization",
            description = "wiki服务的组织禁用监听",
            sagaCode = "iam-disable-organization",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 4)
    public String handleOrganizationDisableEvent(String data) throws IOException {
        OrganizationDTO organizationDTO = objectMapper.readValue(data, OrganizationDTO.class);
        wikiGroupService.disableOrganizationGroup(organizationDTO.getOrganizationId(), USERNAME);
        return data;
    }

    /**
     * 项目禁用
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "disableProject")
    @SagaTask(code = "wikiDisableProject",
            description = "wiki服务的项目禁用监听",
            sagaCode = "iam-disable-project",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 4)
    public String handleProjectDisableEvent(String data) throws IOException {
        ProjectDTO projectDTO = objectMapper.readValue(data, ProjectDTO.class);
        wikiGroupService.disableProjectGroup(projectDTO.getProjectId(), USERNAME);
        return data;
    }

    /**
     * 组织启用
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "enableOrganization")
    @SagaTask(code = "wikiEnableOrganization",
            description = "wiki服务的组织启用监听",
            sagaCode = "iam-enable-organization",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 4)
    public String handleOrganizationEnableEvent(String data) throws IOException {
        OrganizationDTO organizationDTO = objectMapper.readValue(data, OrganizationDTO.class);
        wikiGroupService.enableOrganizationGroup(organizationDTO.getOrganizationId(), USERNAME);
        return data;
    }

    /**
     * 项目启用
     */
//    @EventListener(topic = IAM_SERVICE, businessType = "enableProject")
    @SagaTask(code = "wikiEnableOrganization",
            description = "wiki服务的项目启用监听",
            sagaCode = "iam-enable-project",
            concurrentLimitNum = 2,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 4)
    public String handleProjectEnableEvent(String data) throws IOException {
        ProjectDTO projectDTO = objectMapper.readValue(data, ProjectDTO.class);
        wikiGroupService.enableProjectGroup(projectDTO.getProjectId(), USERNAME);
        return data;
    }
}

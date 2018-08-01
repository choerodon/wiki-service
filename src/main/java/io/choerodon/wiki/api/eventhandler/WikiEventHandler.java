package io.choerodon.wiki.api.eventhandler;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.core.event.EventPayload;
import io.choerodon.core.saga.SagaDefinition;
import io.choerodon.core.saga.SagaTask;
import io.choerodon.event.consumer.annotation.EventListener;
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

    private static final String IAM_SERVICE = "iam-service";
    private static final String ORG_SERVICE = "organization-service";
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
    @EventListener(topic = ORG_SERVICE, businessType = "createOrganizationToDevops")
    public void handleOrganizationCreateEvent(EventPayload<OrganizationEventPayload> payload) {
        OrganizationEventPayload organizationEventPayload = payload.getData();
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
    public void handleProjectCreateEvent(String data) throws IOException {
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
    }


    /**
     * 角色同步事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "updateMemberRole")
    public void handleCreateGroupMemberEvent(EventPayload<List<GroupMemberDTO>> payload) {
        List<GroupMemberDTO> groupMemberDTOList = payload.getData();
        loggerInfo(groupMemberDTOList);
        wikiGroupService.createWikiGroupUsers(groupMemberDTOList, USERNAME);
    }

    /**
     * 角色同步事件,去除角色
     */
    @EventListener(topic = IAM_SERVICE, businessType = "deleteMemberRole")
    public void handledeleteMemberRoleEvent(EventPayload<List<GroupMemberDTO>> payload) {
        List<GroupMemberDTO> groupMemberDTOList = payload.getData();
        loggerInfo(groupMemberDTOList);
        wikiGroupService.deleteWikiGroupUsers(groupMemberDTOList, USERNAME);
    }

    /**
     * 用户创建事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "createUser")
    public void handleCreateUserEvent(EventPayload<UserDTO> payload) {
        UserDTO userDTO = payload.getData();
        wikiGroupService.createWikiUserToGroup(userDTO, USERNAME);
    }

    /**
     * 组织禁用事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "disableOrganization")
    public void handleOrganizationDisableEvent(EventPayload<OrganizationDTO> payload) {
        OrganizationDTO organizationDTO = payload.getData();
        wikiGroupService.disableOrganizationGroup(organizationDTO.getOrganizationId(), USERNAME);
    }

    /**
     * 项目禁用事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "disableProject")
    public void handleProjectDisableEvent(EventPayload<ProjectDTO> payload) {
        ProjectDTO projectDTO = payload.getData();
        wikiGroupService.disableProjectGroup(projectDTO.getProjectId(), USERNAME);
    }

    /**
     * 组织启用事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "enableOrganization")
    public void handleOrganizationEnableEvent(EventPayload<OrganizationDTO> payload) {
        OrganizationDTO organizationDTO = payload.getData();
        wikiGroupService.enableOrganizationGroup(organizationDTO.getOrganizationId(), USERNAME);
    }

    /**
     * 项目启用事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "enableProject")
    public void handleProjectEnableEvent(EventPayload<ProjectDTO> payload) {
        ProjectDTO projectDTO = payload.getData();
        wikiGroupService.enableProjectGroup(projectDTO.getProjectId(), USERNAME);
    }
}

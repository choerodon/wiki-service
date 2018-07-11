package io.choerodon.wiki.api.eventhandler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.wiki.api.dto.GitlabGroupMemberDTO;
import io.choerodon.wiki.api.dto.GitlabUserDTO;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.event.OrganizationEventPayload;
import io.choerodon.wiki.domain.application.event.ProjectEvent;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/5.
 */
@Component
public class WikiEventHandler {

    private static final String IAM_SERVICE = "iam-service";
    private static final String ORG_SERVICE = "organization-service";
    private static final String ORG_ICON = "chart-organisation";
    private static final String PROJECT_ICON = "branch";
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiEventHandler.class);

    private WikiSpaceService wikiSpaceService;

    WikiGroupService wikiGroupService;

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
        wikiSpaceService.create(wikiSpaceDTO, organizationEventPayload.getId(),
                WikiSpaceResourceType.ORGANIZATION.getResourceType());

        //创建组
        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        String adminGroupName = "O-"+organizationEventPayload.getCode()+"AdminGroup";
        String userGroupName = "O-"+organizationEventPayload.getCode()+"UserGroup";
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupService.create(wikiGroupDTO);
        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO);
    }

    /**
     * 创建项目事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "createProject")
    public void handleProjectCreateEvent(EventPayload<ProjectEvent> payload) {
        ProjectEvent projectEvent = payload.getData();
        loggerInfo(projectEvent);
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
        wikiSpaceDTO.setName(projectEvent.getOrganizationName() + "/" + projectEvent.getProjectName());
        wikiSpaceDTO.setIcon(PROJECT_ICON);
        wikiSpaceService.create(wikiSpaceDTO, projectEvent.getProjectId(),
                WikiSpaceResourceType.PROJECT.getResourceType());
        //创建组
        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
        String adminGroupName = "P-"+projectEvent.getProjectCode()+"AdminGroup";
        String userGroupName = "P-"+projectEvent.getProjectCode()+"UserGroup";
        wikiGroupDTO.setGroupName(adminGroupName);
        wikiGroupService.create(wikiGroupDTO);
        wikiGroupDTO.setGroupName(userGroupName);
        wikiGroupService.create(wikiGroupDTO);
    }


    /**
     * 角色同步事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "updateMemberRole")
    public void handleGitlabGroupMemberEvent(EventPayload<List<GitlabGroupMemberDTO>> payload) {
        List<GitlabGroupMemberDTO> gitlabGroupMemberDTOList = payload.getData();
        loggerInfo(gitlabGroupMemberDTOList);

        wikiGroupService.createWikiGroupUsers(gitlabGroupMemberDTOList);
    }

    /**
     * 角色同步事件,去除角色
     */
    @EventListener(topic = IAM_SERVICE, businessType = "deleteMemberRole")
    public void handledeleteMemberRoleEvent(EventPayload<List<GitlabGroupMemberDTO>> payload) {
        List<GitlabGroupMemberDTO> gitlabGroupMemberDTOList = payload.getData();
        loggerInfo(gitlabGroupMemberDTOList);
        //TODO
        wikiGroupService.deleteWikiGroupUsers(gitlabGroupMemberDTOList);
    }

    /**
     * 用户创建事件
     */
    @EventListener(topic = IAM_SERVICE, businessType = "createUser")
    public void handleCreateUserEvent(EventPayload<GitlabUserDTO> payload) {
        GitlabUserDTO gitlabUserDTO = payload.getData();
        wikiGroupService.createWikiUserToGroup(gitlabUserDTO);
    }

    /**
     * 组织禁用事件
     */
/*    @EventListener(topic = IAM_SERVICE, businessType = "")
    public void handleOrganizationDisableEvent(EventPayload payload) {

    }*/

    /**
     * 项目禁用事件
     */
/*    @EventListener(topic = IAM_SERVICE, businessType = "")
    public void handleProjectDisableEvent(EventPayload payload) {

    }*/
}

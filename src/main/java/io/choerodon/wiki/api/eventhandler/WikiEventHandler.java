package io.choerodon.wiki.api.eventhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
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

    public WikiEventHandler(WikiSpaceService wikiSpaceService) {
        this.wikiSpaceService = wikiSpaceService;
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
        wikiSpaceDTO.setDescribe(organizationEventPayload.getName());
        wikiSpaceService.create(wikiSpaceDTO, organizationEventPayload.getId(),
                WikiSpaceResourceType.ORGANIZATION.getResourceType());
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
        wikiSpaceDTO.setDescribe(projectEvent.getProjectName());
        wikiSpaceService.create(wikiSpaceDTO, projectEvent.getProjectId(),
                WikiSpaceResourceType.PROJECT.getResourceType());
    }
}

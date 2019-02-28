package io.choerodon.wiki.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.api.dto.MenuDTO;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;
import io.choerodon.wiki.domain.application.event.OrganizationEventPayload;
import io.choerodon.wiki.domain.application.event.ProjectEvent;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface WikiSpaceService {

    void create(WikiSpaceDTO wikiSpaceDTO, Long resourceId, String username, String type, Boolean flag);

    Page<WikiSpaceListTreeDTO> listTreeWikiSpaceByPage(Long resourceId, String type,
                                                       PageRequest pageRequest, String searchParam);

    List<WikiSpaceResponseDTO> underOrganization(Long organizationId, String type);

    List<WikiSpaceResponseDTO> underProject(Long projectId, String type);

    WikiSpaceResponseDTO query(Long id);

    WikiSpaceResponseDTO update(Long id, WikiSpaceDTO wikiSpaceDTO, String username);

    Boolean checkName(Long resourceId, String name, String type);

    void syncOrg(Long id);

    void syncProject(Long id);

    void delete(Long resourceId, Long id);

    void updateAndSyncProject(ProjectEvent projectEvent);

    void updateAndSyncOrganization(OrganizationEventPayload organizationEventPayload);

    List<WikiSpaceResponseDTO> getWikiSpaceList(Long resourceId, String resourceType);

    void createDemo(Long id,String username);

    String queryWikiMenus(Long projectId, MenuDTO menuDTO);
}

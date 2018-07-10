package io.choerodon.wiki.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface WikiSpaceService {

    void create(WikiSpaceDTO wikiSpaceDTO, Long resourceId, String type);

    Page<WikiSpaceResponseDTO> listWikiSpaceByPage(Long resourceId, String type,
                                                   PageRequest pageRequest, String searchParam);

    WikiSpaceResponseDTO query(Long id);

    WikiSpaceResponseDTO update(Long id,WikiSpaceDTO wikiSpaceDTO,String type);

    Boolean checkName(Long projectId,String name,String type);
}

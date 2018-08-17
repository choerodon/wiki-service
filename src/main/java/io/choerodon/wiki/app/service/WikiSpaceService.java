package io.choerodon.wiki.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface WikiSpaceService {

    void create(WikiSpaceDTO wikiSpaceDTO, Long resourceId,String username, String type);

    Page<WikiSpaceListTreeDTO> listTreeWikiSpaceByPage(Long resourceId, String type,
                                                       PageRequest pageRequest, String searchParam);

    WikiSpaceResponseDTO query(Long id);

    WikiSpaceResponseDTO update(Long id,WikiSpaceDTO wikiSpaceDTO,String username);

    Boolean checkName(Long resourceId,String name,String type);

    void delete(Long resourceId,Long id);
}

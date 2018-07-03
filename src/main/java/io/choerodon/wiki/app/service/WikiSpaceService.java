package io.choerodon.wiki.app.service;

import io.choerodon.wiki.api.dto.WikiSpaceDTO;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface WikiSpaceService {

    void create(WikiSpaceDTO wikiSpaceDTO, Long organizationId,String type);
}

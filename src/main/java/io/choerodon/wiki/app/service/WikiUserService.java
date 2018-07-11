package io.choerodon.wiki.app.service;

import io.choerodon.wiki.api.dto.WikiUserDTO;

/**
 * Created by Ernst on 2018/7/4.
 */
public interface WikiUserService {

    Boolean create(WikiUserDTO wikiSpaceDTO);

    Boolean checkUserExsist(String userName);

    Boolean deletePage(String pageName, String username);
}

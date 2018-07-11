package io.choerodon.wiki.domain.service;

import io.choerodon.wiki.domain.application.entity.WikiUserE;

/**
 * Created by Ernst on 2018/7/4.
 */
public interface IWikiUserService {

    Boolean createUser(WikiUserE wikiUserE, String param1, String xmlParam);

    Boolean checkDocExsist(String username,String param1);

    Boolean deletePage(String pageName, String username);
}

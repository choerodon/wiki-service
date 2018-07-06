package io.choerodon.wiki.domain.service;

import io.choerodon.wiki.domain.application.entity.WikiUserE;

/**
 * Created by Ernsy on t018/7/4.
 */
public interface IWikiUserService {

    Boolean createUser(WikiUserE wikiUserE, String param1, String xmlParam);

    Boolean checkUserExsist(String userName);

    Boolean deletePage(String pageName);
}

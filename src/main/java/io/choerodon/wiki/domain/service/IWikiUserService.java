package io.choerodon.wiki.domain.service;

import java.util.List;

import io.choerodon.wiki.domain.application.entity.WikiUserE;

/**
 * Created by Ernst on 2018/7/4.
 */
public interface IWikiUserService {

    Boolean createUser(String param1, String xmlParam, String username);

    Boolean checkDocExsist(String username, String param1);

    Boolean deletePage(String pageName, String username);

    Boolean createWikiUserToGroup(List<WikiUserE> wikiUserEList, String username);
}

package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/11.
 */
public interface IWikiClassService {

    String getPageClassResource(String pageName, String username);

    void deletePageClass(String username, String name, int objectNumber);
}

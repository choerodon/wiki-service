package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/12.
 */
public interface IWikiCreatePageService {

    int createPage1Code(String param1, String name, String xmlParam, String username);

    int createPage2Code(String param1, String param2, String name, String xmlParam, String username);
}

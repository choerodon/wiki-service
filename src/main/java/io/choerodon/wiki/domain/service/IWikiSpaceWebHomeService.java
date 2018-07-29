package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/3.
 */
public interface IWikiSpaceWebHomeService {

    int createSpace1WebHome(String param1, String xmlParam, String username);

    int createSpace2WebHome(String param1, String param2, String xmlParam, String username);

    int createSpace3WebHome(String param1, String param2, String param3, String xmlParam, String username);

    int deletePage(String spaces,String page,String username);

    int deletePage1(String org,String project,String page,String username);
}

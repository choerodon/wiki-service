package io.choerodon.wiki.domain.service;

import java.util.Map;

/**
 * Created by Zenger on 2018/7/3.
 */
public interface IWikiSpaceWebHomeService {

    int createSpace1WebHome(Long spaceId, String param1, String xmlParam, String username);

    int createSpace2WebHome(Long spaceId, String param1, String param2, String xmlParam, String username);

    int createSpace3WebHome(Long spaceId, String param1, String param2, String param3, String xmlParam, String username);

    int deletePage(Long spaceId, String param1, String page, String username);

    int deletePage1(Long spaceId, String param1, String param2, String page, String username);

    int deletePage2(Long spaceId, String param1, String param2, String param3, String page, String username);

    Boolean checkOrgSpaceExsist(String space, String username);

    Boolean checkProjectSpaceExsist(String orgSpace, String projectSpace, String username);

    String getPageMenuUnderProject(String menuIdStr, String username);
}

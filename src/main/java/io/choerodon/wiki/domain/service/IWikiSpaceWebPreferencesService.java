package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceWebPreferencesService {

    int createSpace1WebPreferences(Long id, String param1, String xmlParam,String username);

    int createSpace2WebPreferences(Long id, String param1, String param2, String xmlParam,String username);

    int createSpace3WebPreferences(Long id, String param1, String param2, String param3, String xmlParam,String username);
}

package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceWebPreferencesService {

    int createSpace1WebPreferences(String param1, String xmlParam);

    int createSpace2WebPreferences(String param1, String param2, String xmlParam);

    int createSpace3WebPreferences(String param1, String param2, String param3, String xmlParam);
}

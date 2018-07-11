package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceCodeTranslationsService {

    int createSpace1CodeTranslations(String param1, String xmlParam,String username);

    int createSpace2CodeTranslations(String param1, String param2, String xmlParam,String username);

    int createSpace3CodeTranslations(String param1, String param2, String param3, String xmlParam,String username);
}

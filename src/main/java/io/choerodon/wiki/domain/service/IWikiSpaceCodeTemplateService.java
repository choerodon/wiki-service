package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceCodeTemplateService {

    int createSpace1CodeTemplate(String param1, String xmlParam,String username);

    int createSpace2CodeTemplate(String param1, String param2, String xmlParam,String username);

    int createSpace3CodeTemplate(String param1, String param2, String param3, String xmlParam,String username);
}

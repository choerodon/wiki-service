package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceCodeTemplateProvideService {

    int createSpace1CodeTemplateProvide(String param1, String xmlParam,String username);

    int createSpace2CodeTemplateProvide(String param1, String param2, String xmlParam,String username);

    int createSpace3CodeTemplateProvide(String param1, String param2, String param3, String xmlParam,String username);
}

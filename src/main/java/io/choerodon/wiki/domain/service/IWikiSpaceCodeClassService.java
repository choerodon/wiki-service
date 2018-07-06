package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceCodeClassService {

    int createSpace1CodeClass(String param1, String xmlParam);

    int createSpace2CodeClass(String param1, String param2, String xmlParam);

    int createSpace3CodeClass(String param1, String param2, String param3, String xmlParam);
}

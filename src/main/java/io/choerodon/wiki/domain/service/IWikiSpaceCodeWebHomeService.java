package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceCodeWebHomeService {

    int createSpace1CodeWebHome(String param1, String xmlParam,String username);

    int createSpace2CodeWebHome(String param1, String param2, String xmlParam,String username);

    int createSpace3CodeWebHome(String param1, String param2, String param3, String xmlParam,String username);
}

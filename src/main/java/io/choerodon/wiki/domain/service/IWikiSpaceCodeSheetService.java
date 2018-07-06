package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface IWikiSpaceCodeSheetService {

    int createSpace1CodeSheet(String param1, String xmlParam);

    int createSpace2CodeSheet(String param1, String param2, String xmlParam);

    int createSpace3CodeSheet(String param1, String param2, String param3, String xmlParam);
}

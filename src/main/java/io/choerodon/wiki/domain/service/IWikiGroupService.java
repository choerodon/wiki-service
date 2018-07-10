package io.choerodon.wiki.domain.service;

/**
 * Created by Ernst on t018/7/6.
 */
public interface IWikiGroupService {

    Boolean createGroup(String groupName, String xmlParam);

    Boolean createGroupUsers(String groupName, String userName);

}

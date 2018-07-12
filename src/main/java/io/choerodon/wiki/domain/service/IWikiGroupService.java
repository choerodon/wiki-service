package io.choerodon.wiki.domain.service;

/**
 * Created by Ernst on t018/7/6.
 */
public interface IWikiGroupService {

    Boolean createGroup(String groupName,String username);

    Boolean createGroupUsers(String groupName, String loginName,String username);

    Boolean disableOrgGroupView(String groupName, String organizationName,String username);

    Boolean disableProjectGroupView(String projectName, String projectCode, String organizationName,String username);
}

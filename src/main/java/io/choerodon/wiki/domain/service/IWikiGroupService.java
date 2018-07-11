package io.choerodon.wiki.domain.service;

/**
 * Created by Ernst on t018/7/6.
 */
public interface IWikiGroupService {

    Boolean createGroup(String groupName);

    Boolean createGroupUsers(String groupName, String userName);

    Boolean disableOrgGroupView(String groupName, String organizationName);

    Boolean disableProjectGroupView(String projectName, String projectCode, String organizationName);
}

package io.choerodon.wiki.domain.service;

import java.util.List;

/**
 * Created by Ernst on t018/7/6.
 */
public interface IWikiGroupService {

    Boolean createGroup(String groupName,String username);

    Boolean createGroupUsers(String groupName, String loginName,String username);

    Boolean disableOrgGroupView(String groupName, String organizationName,String username);

    Boolean disableProjectGroupView(String projectName, String projectCode, String organizationName,String username);

    Boolean addRightsToOrg(String organizationCode, String organizationName, List<String> rights, Boolean isAdmin, String username);

    Boolean addRightsToProject(String projectName, String projectCode, String organizationName,List<String> rights, Boolean isAdmin, String username);
}

package io.choerodon.wiki.app.service;

import java.util.List;

import io.choerodon.wiki.api.dto.GroupMemberDTO;
import io.choerodon.wiki.api.dto.UserDTO;
import io.choerodon.wiki.api.dto.WikiGroupDTO;

/**
 * Created by Ernst on 2018/7/6.
 */
public interface WikiGroupService {

    Boolean create(WikiGroupDTO wikiGroupDTO, String username, Boolean isAdmin, Boolean isOrg);

    void createWikiGroupUsers(List<GroupMemberDTO> groupMemberDTOList, String username);

    void deleteWikiGroupUsers(List<GroupMemberDTO> groupMemberDTOList, String username);

    void createWikiUserToGroup(List<UserDTO> userDTOList, String username);

    void disableOrganizationGroup(Long orgId, String username);

    void enableOrganizationGroup(Long orgId, String username);

    void disableProjectGroup(Long projectId, String username);

    void enableProjectGroup(Long projectId, String username);

    void setUserToGroup(String groupName, Long userId, String username);
}

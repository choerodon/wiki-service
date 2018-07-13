package io.choerodon.wiki.app.service;

import java.util.List;

import io.choerodon.wiki.api.dto.GitlabGroupMemberDTO;
import io.choerodon.wiki.api.dto.GitlabUserDTO;
import io.choerodon.wiki.api.dto.WikiGroupDTO;

/**
 * Created by Ernst on 2018/7/6.
 */
public interface WikiGroupService {

    Boolean create(WikiGroupDTO wikiGroupDTO, String username, Boolean isAdmin, Boolean isOrg);

    void createWikiGroupUsers(List<GitlabGroupMemberDTO> gitlabGroupMemberList, String username);

    void deleteWikiGroupUsers(List<GitlabGroupMemberDTO> gitlabGroupMemberList);

    void createWikiUserToGroup(GitlabUserDTO gitlabUserDTO, String username);

    void disableOrganizationGroup(Long orgId, String username);

    void disableProjectGroup(Long projectId, String username);

    void setUserToGroup(String groupName, Long userId, String username);
}

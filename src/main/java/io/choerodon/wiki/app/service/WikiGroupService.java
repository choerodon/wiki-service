package io.choerodon.wiki.app.service;

import java.util.List;

import io.choerodon.wiki.api.dto.GitlabGroupMemberDTO;
import io.choerodon.wiki.api.dto.GitlabUserDTO;
import io.choerodon.wiki.api.dto.WikiGroupDTO;

/**
 * Created by Ernst on 2018/7/6.
 */
public interface WikiGroupService {

    Boolean create(WikiGroupDTO wikiGroupDTO, String username);

    void createWikiGroupUsers(List<GitlabGroupMemberDTO> gitlabGroupMemberList);

    void deleteWikiGroupUsers(List<GitlabGroupMemberDTO> gitlabGroupMemberList);

    void createWikiUserToGroup(GitlabUserDTO gitlabUserDTO);

    void disableOrganizationGroup(Long orgId, String username);

    void disableProjectGroup(Long projectId, String username);
}

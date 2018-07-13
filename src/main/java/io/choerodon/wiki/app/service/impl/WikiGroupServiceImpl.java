package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.wiki.api.dto.GitlabGroupMemberDTO;
import io.choerodon.wiki.api.dto.GitlabUserDTO;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.OrganizationSpaceType;

/**
 * Created by Ernst on 2018/7/4.
 */
@Service
public class WikiGroupServiceImpl implements WikiGroupService {

    private static final String SITE = "site";

    private IWikiGroupService iWikiGroupService;
    private IWikiUserService iWikiUserService;
    private IamRepository iamRepository;

    public WikiGroupServiceImpl(IWikiGroupService iWikiGroupService,
                                IWikiUserService iWikiUserService,
                                IamRepository iamRepository) {
        this.iWikiGroupService = iWikiGroupService;
        this.iWikiUserService = iWikiUserService;
        this.iamRepository = iamRepository;
    }

    @Override
    public Boolean create(WikiGroupDTO wikiGroupDTO, String username, Boolean isAdmin, Boolean isOrg) {
        Boolean flag = iWikiUserService.checkDocExsist(username, wikiGroupDTO.getGroupName());
        if (!flag) {
            iWikiGroupService.createGroup(wikiGroupDTO.getGroupName(), username);
            String[] adminRights = {"login", "view", "edit", "delete", "creator", "register", "comment", "script", "admin", "createwiki", "programming"};
            List<String> adminRightsList = Arrays.asList(adminRights);
            String[] userRights = {"login", "view", "creator", "comment", "script", "programming"};
            List<String> userRightsList = Arrays.asList(userRights);
            if (isAdmin) {
                if (isOrg) {
                    //给组织组分配admin权限
                    iWikiGroupService.addRightsToOrg(wikiGroupDTO.getOrganizationCode(),wikiGroupDTO.getOrganizationName(),adminRightsList,username);
                } else {
                    //给项目组分配admin权限
                    iWikiGroupService.addRightsToProject(wikiGroupDTO.getProjectName(),wikiGroupDTO.getProjectCode(),wikiGroupDTO.getOrganizationName(),adminRightsList,username);
                }
            } else {
                if (isOrg) {
                    //给组织组分配user权限
                    iWikiGroupService.addRightsToOrg(wikiGroupDTO.getOrganizationCode(),wikiGroupDTO.getOrganizationName(),userRightsList,username);
                } else {
                    //给项目组分配user权限
                    iWikiGroupService.addRightsToProject(wikiGroupDTO.getProjectName(),wikiGroupDTO.getProjectCode(),wikiGroupDTO.getOrganizationName(),userRightsList,username);
                }

            }
        }
        return false;
    }

    @Override
    public void createWikiGroupUsers(List<GitlabGroupMemberDTO> gitlabGroupMemberList, String username) {
        gitlabGroupMemberList.stream()
                .filter(gitlabGroupMemberDTO -> !gitlabGroupMemberDTO.getResourceType().equals(SITE))
                .forEach(gitlabGroupMemberDTO -> {
                    //将用户分配到组
                    String groupName = getGroupName(gitlabGroupMemberDTO);

                    //通过groupName给组添加成员
                    if (!StringUtils.isEmpty(groupName)) {
                        //根据用户名查询用户信息
                        UserE user = iamRepository.queryByLoginName(gitlabGroupMemberDTO.getUsername());
                        WikiUserE wikiUserE = new WikiUserE();
                        wikiUserE.setLastName(user.getLoginName());
                        wikiUserE.setFirstName(user.getLoginName());
                        wikiUserE.setEmail(user.getEmail());
                        String xmlParam = getUserXml(wikiUserE);
                        if (!iWikiUserService.checkDocExsist(username, user.getLoginName())) {
                            iWikiUserService.createUser(wikiUserE, user.getLoginName(), xmlParam, username);
                        }

                        iWikiGroupService.createGroupUsers(groupName, user.getLoginName(),username);
                    }
                });
    }

    @Override
    public void deleteWikiGroupUsers(List<GitlabGroupMemberDTO> gitlabGroupMemberList) {
        gitlabGroupMemberList.stream()
                .filter(gitlabGroupMemberDTO -> !gitlabGroupMemberDTO.getResourceType().equals(SITE))
                .forEach(gitlabGroupMemberDTO -> {
                    String groupName = getGroupName(gitlabGroupMemberDTO);

                    if (!StringUtils.isEmpty(groupName)) {

                    }
                });
    }

    @Override
    public void createWikiUserToGroup(GitlabUserDTO gitlabUserDTO, String username) {
        String loginName = gitlabUserDTO.getUsername();
        UserE user = iamRepository.queryByLoginName(loginName);
        if (user != null) {
            Long orgId = user.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            String orgCode = organization.getCode();
            String groupName = "O-" + orgCode + "UserGroup";

            //如果用户不存在则新建
            Boolean flag = iWikiUserService.checkDocExsist(loginName, loginName);
            if (!flag) {
                WikiUserE wikiUserE = new WikiUserE();
                wikiUserE.setLastName(loginName);
                wikiUserE.setFirstName(loginName);
                wikiUserE.setEmail(gitlabUserDTO.getEmail());

                String xmlParam = getUserXml(wikiUserE);
                iWikiUserService.createUser(wikiUserE, loginName, xmlParam,username);
            }

            //通过groupName给组添加成员
            iWikiGroupService.createGroupUsers(groupName, loginName,username);
        }
    }

    @Override
    public void disableOrganizationGroup(Long orgId, String username) {
        OrganizationE organization = iamRepository.queryOrganizationById(orgId);
        if (organization != null) {
            iWikiGroupService.disableOrgGroupView(organization.getCode(), organization.getName(), username);
        } else {
            throw new CommonException("error.query.organization");
        }

    }

    @Override
    public void disableProjectGroup(Long projectId, String username) {
        ProjectE projectE = iamRepository.queryIamProject(projectId);
        if (projectE != null) {
            Long orgId = projectE.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            iWikiGroupService.disableProjectGroupView(projectE.getName(), projectE.getCode(), organization.getName(), username);
        } else {
            throw new CommonException("error.query.project");
        }
    }

    @Override
    public void setUserToGroup(String groupName, Long userId, String username) {
        UserE userE = iamRepository.queryUserById(userId);
        if (userE != null && userE.getLoginName() != null) {
            String loginName = userE.getLoginName();
            iWikiGroupService.createGroupUsers(groupName, loginName, username);
        } else {
            throw new CommonException("error.query.user");
        }
    }

    private String getGroupName(GitlabGroupMemberDTO gitlabGroupMemberDTO) {
        List<String> roleLabels = gitlabGroupMemberDTO.getRoleLabels();
        if (roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_ADMIN.getResourceType()) || roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_ADMIN.getResourceType())) {
            return getGroupNameBuffer(gitlabGroupMemberDTO).append("AdminGroup").toString();
        } else if (roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_USER.getResourceType()) || roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_USER.getResourceType())) {
            return getGroupNameBuffer(gitlabGroupMemberDTO).append("UserGroup").toString();
        } else {
            return "";
        }
    }

    private StringBuffer getGroupNameBuffer(GitlabGroupMemberDTO gitlabGroupMemberDTO) {
        Long resourceId = gitlabGroupMemberDTO.getResourceId();
        String resourceType = gitlabGroupMemberDTO.getResourceType();
        StringBuffer groupName = new StringBuffer();
        if (ResourceLevel.ORGANIZATION.value().equals(resourceType)) {
            groupName.append("O-");
            //通过组织id获取组织code
            OrganizationE organization = iamRepository.queryOrganizationById(resourceId);
            groupName.append(organization.getCode());

        } else if (ResourceLevel.PROJECT.value().equals(resourceType)) {
            groupName.append("P-");
            //通过项目id找到项目code
            ProjectE projectE = iamRepository.queryIamProject(resourceId);
            groupName.append(projectE.getCode());
        }

        return groupName;
    }

    private String getUserXml(WikiUserE wikiUserE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/user.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ LAST_NAME }}", wikiUserE.getLastName());
        params.put("{{ FIRST_NAME }}", wikiUserE.getFirstName());
        params.put("{{ USER_EMAIL }}", wikiUserE.getEmail());
        return FileUtil.replaceReturnString(inputStream, params);
    }
}

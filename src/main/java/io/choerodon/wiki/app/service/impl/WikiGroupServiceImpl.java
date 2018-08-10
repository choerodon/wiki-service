package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.wiki.api.dto.GroupMemberDTO;
import io.choerodon.wiki.api.dto.UserDTO;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.service.IWikiClassService;
import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.Stage;
import io.choerodon.wiki.infra.common.enums.OrganizationSpaceType;

/**
 * Created by Ernst on 2018/7/4.
 */
@Service
public class WikiGroupServiceImpl implements WikiGroupService {

    private static final String SITE = "site";
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiGroupServiceImpl.class);

    private IWikiGroupService iWikiGroupService;
    private IWikiUserService iWikiUserService;
    private IamRepository iamRepository;
    private IWikiClassService iWikiClassService;

    public WikiGroupServiceImpl(IWikiGroupService iWikiGroupService,
                                IWikiUserService iWikiUserService,
                                IamRepository iamRepository,
                                IWikiClassService iWikiClassService) {
        this.iWikiGroupService = iWikiGroupService;
        this.iWikiUserService = iWikiUserService;
        this.iamRepository = iamRepository;
        this.iWikiClassService = iWikiClassService;
    }

    @Override
    public Boolean create(WikiGroupDTO wikiGroupDTO, String username, Boolean isAdmin, Boolean isOrg) {
        if (!checkDocExsist(username, wikiGroupDTO.getGroupName())) {
            iWikiGroupService.createGroup(wikiGroupDTO.getGroupName(), username);

            Calendar ca = Calendar.getInstance();
            long old = ca.getTimeInMillis();

            try {
                Thread.sleep(1500);
                while (!checkDocExsist(username, wikiGroupDTO.getGroupName())) {
                    Thread.sleep(1500);
                    if (ca.getTimeInMillis() - old > 4500) {
                        return false;
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }

            String[] adminRights = {"login", "view", "edit", "delete", "creator", "register", "comment", "script", "admin", "createwiki", "programming"};
            List<String> adminRightsList = Arrays.asList(adminRights);
            String[] userRights = {"login", "view", "edit", "creator", "comment"};
            List<String> userRightsList = Arrays.asList(userRights);
            if (isAdmin) {
                if (isOrg) {
                    //给组织组分配admin权限
                    iWikiGroupService.addRightsToOrg(wikiGroupDTO, adminRightsList, isAdmin, username);
                } else {
                    //给项目组分配admin权限
                    iWikiGroupService.addRightsToProject(wikiGroupDTO, adminRightsList, isAdmin, username);
                }
            } else {
                if (isOrg) {
                    //给组织组分配user权限
                    iWikiGroupService.addRightsToOrg(wikiGroupDTO, userRightsList, isAdmin, username);
                } else {
                    //给项目组分配user权限
                    iWikiGroupService.addRightsToProject(wikiGroupDTO, userRightsList, isAdmin, username);
                }

            }
        }

        return true;
    }

    @Override
    public void createWikiGroupUsers(List<GroupMemberDTO> groupMemberDTOList, String username) {
        groupMemberDTOList.stream()
                .filter(groupMember -> !groupMember.getResourceType().equals(SITE))
                .forEach(groupMember -> {
                    //将用户分配到组
                    String groupName = getGroupName(groupMember, username);

                    //通过groupName给组添加成员
                    if (!StringUtils.isEmpty(groupName)) {
                        //根据用户名查询用户信息
                        UserE user = iamRepository.queryByLoginName(groupMember.getUsername());
                        WikiUserE wikiUserE = new WikiUserE();
                        wikiUserE.setLastName(user.getRealName());
                        wikiUserE.setFirstName(user.getLoginName());
                        wikiUserE.setEmail(user.getEmail());
                        wikiUserE.setPhone(user.getPhone());
                        String xmlParam = getUserXml(wikiUserE);
                        if (!checkDocExsist(username, user.getLoginName())) {
                            iWikiUserService.createUser(wikiUserE, user.getLoginName(), xmlParam, username);
                        }

                        iWikiGroupService.createGroupUsers(groupName, user.getLoginName(), username);
                    }
                });
    }

    @Override
    public void deleteWikiGroupUsers(List<GroupMemberDTO> groupMemberDTOList, String username) {
        groupMemberDTOList.stream()
                .filter(groupMember -> !groupMember.getResourceType().equals(SITE))
                .forEach(groupMember -> {
                    List<String> roleLabels = groupMember.getRoleLabels();
                    if (roleLabels == null || roleLabels.isEmpty()) {
                        String adminGroupName = getGroupNameBuffer(groupMember, username, Stage.ADMIN_GROUP).append(Stage.ADMIN_GROUP).toString();
                        String userGroupName = getGroupNameBuffer(groupMember, username, Stage.USER_GROUP).append(Stage.USER_GROUP).toString();
                        deletePageClass(adminGroupName, username, groupMember.getUsername());
                        deletePageClass(userGroupName, username, groupMember.getUsername());
                    } else {
                        deleteGroupMember(roleLabels, groupMember, username);
                    }
                });
    }

    @Override
    public void createWikiUserToGroup(UserDTO userDTO, String username) {
        String loginName = userDTO.getUsername();
        UserE user = iamRepository.queryByLoginName(loginName);
        if (user != null) {
            Long orgId = user.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            String orgCode = organization.getCode();
            String groupName = "O-" + orgCode + Stage.USER_GROUP;

            //如果用户不存在则新建
            Boolean flag = checkDocExsist(username, loginName);
            if (!flag) {
                WikiUserE wikiUserE = new WikiUserE();
                wikiUserE.setFirstName(user.getLoginName());
                wikiUserE.setLastName(user.getRealName());
                wikiUserE.setPhone(user.getPhone());
                wikiUserE.setEmail(user.getEmail());

                String xmlParam = getUserXml(wikiUserE);
                iWikiUserService.createUser(wikiUserE, loginName, xmlParam, username);
            }

            //通过groupName给组添加成员
            iWikiGroupService.createGroupUsers(groupName, loginName, username);
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
    public void enableOrganizationGroup(Long orgId, String username) {
        OrganizationE organization = iamRepository.queryOrganizationById(orgId);
        if (organization != null) {
            List<Integer> list = getGlobalRightsObjectNumber("O-" + organization.getName(), null, username);
            for (Integer i : list) {
                //删除角色
                iWikiClassService.deletePageClass(username, "O-" + organization.getName(), Stage.WEBPREFERENCES, Stage.XWIKIGLOBALRIGHTS, i);
            }
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
            iWikiGroupService.disableProjectGroupView(projectE.getName(), projectE.getCode(), organization.getName(), organization.getCode(), username);
        } else {
            throw new CommonException("error.query.project");
        }
    }

    @Override
    public void enableProjectGroup(Long projectId, String username) {
        ProjectE projectE = iamRepository.queryIamProject(projectId);
        if (projectE != null) {
            Long orgId = projectE.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            if (organization != null) {
                List<Integer> list = getGlobalRightsObjectNumber("O-" + organization.getName(),
                        "P-" + projectE.getName(), username);
                for (Integer i : list) {
                    //删除角色
                    iWikiClassService.deleteProjectPageClass(username, "O-" + organization.getName(), "P-" + projectE.getName(), Stage.WEBPREFERENCES, Stage.XWIKIGLOBALRIGHTS, i);
                }
            }
        } else {
            throw new CommonException("error.query.project");
        }
    }

    @Override
    public void setUserToGroup(String groupName, Long userId, String username) {
        UserE userE = iamRepository.queryUserById(userId);
        LOGGER.info("setUserToGroup: " + "groupName: " + groupName + ",user: " + userE.getLoginName());
        if (userE.getLoginName() != null) {
            String loginName = userE.getLoginName();
            Boolean isUserExist = checkDocExsist(loginName, loginName);
            if (!isUserExist) {
                WikiUserE wikiUserE = new WikiUserE();
                wikiUserE.setLastName(userE.getRealName());
                wikiUserE.setFirstName(loginName);
                wikiUserE.setEmail(userE.getEmail());
                wikiUserE.setPhone(userE.getPhone());

                iWikiUserService.createUser(wikiUserE, loginName, getUserXml(wikiUserE), username);
            }
            iWikiGroupService.createGroupUsers(groupName, loginName, username);
        } else {
            throw new CommonException("error.query.user");
        }
    }

    private String getGroupName(GroupMemberDTO groupMemberDTO, String username) {
        List<String> roleLabels = groupMemberDTO.getRoleLabels();
        if (roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_ADMIN.getResourceType()) || roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_ADMIN.getResourceType())) {
            return getGroupNameBuffer(groupMemberDTO, username, Stage.ADMIN_GROUP).append(Stage.ADMIN_GROUP).toString();
        } else if (roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_USER.getResourceType()) || roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_USER.getResourceType())) {
            return getGroupNameBuffer(groupMemberDTO, username, Stage.USER_GROUP).append(Stage.USER_GROUP).toString();
        } else {
            return "";
        }
    }

    private StringBuilder getGroupNameBuffer(GroupMemberDTO groupMemberDTO, String username, String type) {
        Long resourceId = groupMemberDTO.getResourceId();
        String resourceType = groupMemberDTO.getResourceType();
        StringBuilder groupName = new StringBuilder();
        if (ResourceLevel.ORGANIZATION.value().equals(resourceType)) {
            groupName.append("O-");
            //通过组织id获取组织code
            OrganizationE organization = iamRepository.queryOrganizationById(resourceId);
            groupName.append(organization.getCode());

        } else if (ResourceLevel.PROJECT.value().equals(resourceType)) {
            groupName.append("P-");
            //通过项目id找到项目code
            ProjectE projectE = iamRepository.queryIamProject(resourceId);
            OrganizationE organizationE = iamRepository.queryOrganizationById(projectE.getOrganization().getId());
            if (iWikiUserService.checkDocExsist(username, "P-" + organizationE.getCode() + "-" + projectE.getCode() + type)) {
                groupName.append(organizationE.getCode() + "-" + projectE.getCode());
            } else if (iWikiUserService.checkDocExsist(username, "P-" + projectE.getCode() + type)) {
                groupName.append(projectE.getCode());
            }
        }

        return groupName;
    }

    private String getUserXml(WikiUserE wikiUserE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/user.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ LAST_NAME }}", wikiUserE.getLastName());
        params.put("{{ FIRST_NAME }}", wikiUserE.getFirstName());
        params.put("{{ USER_EMAIL }}", wikiUserE.getEmail());
        params.put("{{ PHONE }}", wikiUserE.getPhone());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private List<Integer> getGroupsObjectNumber(String groupName, String username, String loginName) {
        List<Integer> list = new ArrayList<>();
        try {
            String page = iWikiClassService.getPageClassResource(Stage.SPACE, groupName, Stage.XWIKIGROUPS, username);
            if (!StringUtils.isEmpty(page)) {
                Document doc = DocumentHelper.parseText(page);
                Element rootElt = doc.getRootElement();
                Iterator iter = rootElt.elementIterator("objectSummary");
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String pageName = recordEle.elementTextTrim("pageName");
                    if (groupName.equals(pageName)) {
                        String headline = recordEle.elementTextTrim("headline");
                        if (!StringUtils.isEmpty(headline) && loginName.equals(headline.split("\\.")[1])) {
                            list.add(Integer.valueOf(recordEle.elementTextTrim("number")));
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage());
        }

        return list;
    }

    private List<Integer> getGlobalRightsObjectNumber(String org, String project, String username) {
        List<Integer> list = new ArrayList<>();
        try {
            String page;
            if (project == null) {
                page = iWikiClassService.getPageClassResource(org, Stage.WEBPREFERENCES, Stage.XWIKIGLOBALRIGHTS, username);
            } else {
                page = iWikiClassService.getProjectPageClassResource(org, project, Stage.WEBPREFERENCES, Stage.XWIKIGLOBALRIGHTS, username);
            }
            if (!StringUtils.isEmpty(page)) {
                Document doc = DocumentHelper.parseText(page);
                Element rootElt = doc.getRootElement();
                Iterator iter = rootElt.elementIterator("objectSummary");
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String className = recordEle.elementTextTrim("className");
                    if (Stage.XWIKIGLOBALRIGHTS.equals(className)) {
                        String headline = recordEle.elementTextTrim("headline");
                        if (!StringUtils.isEmpty(headline) && Integer.valueOf(headline) == 0) {
                            list.add(Integer.valueOf(recordEle.elementTextTrim("number")));
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage());
        }

        return list;
    }

    private void deletePageClass(String pageName, String username, String deleteUsername) {
        if (!StringUtils.isEmpty(pageName)) {
            List<Integer> list = getGroupsObjectNumber(pageName, username, deleteUsername);
            for (Integer i : list) {
                //删除角色
                iWikiClassService.deletePageClass(username, Stage.SPACE, pageName, Stage.XWIKIGROUPS, i);
            }
        }
    }

    public void deleteGroupMember(List<String> roleLabels, GroupMemberDTO groupMember, String username) {
        if (!roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_ADMIN.getResourceType()) && ResourceLevel.PROJECT.value().equals(groupMember.getResourceType())) {
            String adminGroupName = getGroupNameBuffer(groupMember, username, Stage.ADMIN_GROUP).append(Stage.ADMIN_GROUP).toString();
            deletePageClass(adminGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_ADMIN.getResourceType()) && ResourceLevel.ORGANIZATION.value().equals(groupMember.getResourceType())) {
            String adminGroupName = getGroupNameBuffer(groupMember, username, Stage.ADMIN_GROUP).append(Stage.ADMIN_GROUP).toString();
            deletePageClass(adminGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_USER.getResourceType()) && ResourceLevel.PROJECT.value().equals(groupMember.getResourceType())) {
            String userGroupName = getGroupNameBuffer(groupMember, username, Stage.USER_GROUP).append(Stage.USER_GROUP).toString();
            deletePageClass(userGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_USER.getResourceType()) && ResourceLevel.ORGANIZATION.value().equals(groupMember.getResourceType())) {
            String userGroupName = getGroupNameBuffer(groupMember, username, Stage.USER_GROUP).append(Stage.USER_GROUP).toString();
            deletePageClass(userGroupName, username, groupMember.getUsername());
        }
    }

    public Boolean checkDocExsist(String username, String groupName) {
        return iWikiUserService.checkDocExsist(username, groupName);
    }
}

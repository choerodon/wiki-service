package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.FileUtil;
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
        try {
            if (!checkDocExsist(username, wikiGroupDTO.getGroupName())) {
                iWikiGroupService.createGroup(wikiGroupDTO.getGroupName(), username);

                Calendar ca = Calendar.getInstance();
                long old = ca.getTimeInMillis();

                Thread.sleep(1500);
                while (!checkDocExsist(username, wikiGroupDTO.getGroupName())) {
                    Thread.sleep(1500);
                    if (ca.getTimeInMillis() - old > 4500) {
                        return false;
                    }
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommonException("error.interrupted.exception", e);
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
                            iWikiUserService.createUser(user.getLoginName(), xmlParam, username);
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
                        String adminGroupName = getGroupNameBuffer(groupMember, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString();
                        String userGroupName = getGroupNameBuffer(groupMember, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString();
                        deletePageClass(adminGroupName, username, groupMember.getUsername());
                        deletePageClass(userGroupName, username, groupMember.getUsername());
                    } else {
                        deleteGroupMember(roleLabels, groupMember, username);
                    }
                });
    }

    @Override
    public void createWikiUserToGroup(List<UserDTO> userDTOList, String username) {
        userDTOList.stream()
                .forEach(userDTO -> {
                    String loginName = userDTO.getUsername();
                    UserE user = iamRepository.queryByLoginName(loginName);
                    if (user != null) {
                        Long orgId = user.getOrganization().getId();
                        OrganizationE organization = iamRepository.queryOrganizationById(orgId);
                        String orgCode = organization.getCode();
                        String groupName = BaseStage.O + orgCode + BaseStage.USER_GROUP;

                        //如果用户不存在则新建
                        Boolean flag = checkDocExsist(username, loginName);
                        if (!flag) {
                            WikiUserE wikiUserE = new WikiUserE();
                            wikiUserE.setFirstName(user.getLoginName());
                            wikiUserE.setLastName(user.getRealName());
                            wikiUserE.setPhone(user.getPhone());
                            wikiUserE.setEmail(user.getEmail());

                            String xmlParam = getUserXml(wikiUserE);
                            iWikiUserService.createUser(loginName, xmlParam, username);
                        }

                        //通过groupName给组添加成员
                        iWikiGroupService.createGroupUsers(groupName, loginName, username);
                    }
                });
    }

    @Override
    public void disableOrganizationGroup(Long orgId, String username) {
        OrganizationE organization = iamRepository.queryOrganizationById(orgId);
        if (organization != null) {
            LOGGER.info("disable organization group,orgId: {} and organization: {} ", orgId, organization.toString());
            iWikiGroupService.disableOrgGroupView(organization.getCode(), organization.getName(), username);
        } else {
            throw new CommonException("error.query.organization");
        }
    }

    @Override
    public void enableOrganizationGroup(Long orgId, String username) {
        OrganizationE organization = iamRepository.queryOrganizationById(orgId);
        if (organization != null) {
            LOGGER.info("enable organization group,orgId: {} and organization: {} ", orgId, organization.toString());
            List<Integer> list = getGlobalRightsObjectNumber(BaseStage.O + organization.getName(), null, username);
            for (Integer i : list) {
                //删除角色
                iWikiClassService.deletePageClass(username, BaseStage.O + organization.getName(), BaseStage.WEBPREFERENCES, BaseStage.XWIKIGLOBALRIGHTS, i);
            }
        } else {
            throw new CommonException("error.query.organization");
        }
    }

    @Override
    public void disableProjectGroup(Long projectId, String username) {
        ProjectE projectE = iamRepository.queryIamProject(projectId);
        if (projectE != null) {
            LOGGER.info("disable project group,projectId: {} and project: {} ", projectId, projectE.toString());
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
            LOGGER.info("enable project group,projectId: {} and project: {} ", projectId, projectE.toString());
            Long orgId = projectE.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            if (organization != null) {
                List<Integer> list = getGlobalRightsObjectNumber(BaseStage.O + organization.getName(),
                        BaseStage.P + projectE.getName(), username);
                for (Integer i : list) {
                    //删除角色
                    iWikiClassService.deleteProjectPageClass(username, BaseStage.O + organization.getName(), BaseStage.P + projectE.getName(), BaseStage.WEBPREFERENCES, BaseStage.XWIKIGLOBALRIGHTS, i);
                }
            }
        } else {
            throw new CommonException("error.query.project");
        }
    }

    @Override
    public void setUserToGroup(String groupName, Long userId, String username) {
        UserE userE = iamRepository.queryUserById(userId);
        LOGGER.info("set user to group,groupName:{} and user: {} ", groupName, userE.getLoginName());
        if (userE.getLoginName() != null) {
            String loginName = userE.getLoginName();
            Boolean isUserExist = checkDocExsist(username, loginName);
            if (!isUserExist) {
                WikiUserE wikiUserE = new WikiUserE();
                wikiUserE.setLastName(userE.getRealName());
                wikiUserE.setFirstName(loginName);
                wikiUserE.setEmail(userE.getEmail());
                wikiUserE.setPhone(userE.getPhone());

                iWikiUserService.createUser(loginName, getUserXml(wikiUserE), username);
            }
            iWikiGroupService.createGroupUsers(groupName, loginName, username);
        } else {
            throw new CommonException("error.query.user");
        }
    }

    @Override
    public List<String> getGroupsUsers(String groupName, String username) {
        List<String> list = new ArrayList<>();
        List<String> alluser = new ArrayList<>();
        try {
            String page = iWikiClassService.getPageClassResource(BaseStage.SPACE, groupName, BaseStage.XWIKIGROUPS, username);
            if (!StringUtils.isEmpty(page)) {
                Document doc = DocumentHelper.parseText(page);
                Element rootElt = doc.getRootElement();
                Iterator iter = rootElt.elementIterator("objectSummary");
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String pageName = recordEle.elementTextTrim("pageName");
                    if (groupName.equals(pageName)) {
                        String headline = recordEle.elementTextTrim("headline");
                        if (!StringUtils.isEmpty(headline)) {
                            String user = headline.substring(6);
                            alluser.add(user);
                            if (user.contains(".") && !user.contains("\\.")) {
                                list.add(user);
                            }
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            throw new CommonException("error.document.get", e);
        }

        //获取组中重复数据
        List<String> repeat = new ArrayList<>();
        Map<String, Long> collect = alluser.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        for (Map.Entry<String, Long> entry : collect.entrySet()) {
            if (entry.getValue() >= 2) {
                repeat.add(entry.getKey());
            }
        }

        //删除组中重复数据
        for (String rep : repeat) {
            List<Integer> repeatList = getGroupsObjectNumber(groupName, username, rep);
            //删除组中多余用户，只保留一个
            int flag = 0;
            for (Integer i : repeatList) {
                if (flag == repeatList.size() - 1) {
                    break;
                }
                //删除角色
                iWikiClassService.deletePageClass(username, BaseStage.SPACE, groupName, BaseStage.XWIKIGROUPS, i);
                flag++;
            }
        }

        //删除组中带点用户
        for (String li : list) {
            List<Integer> repeatList = getGroupsObjectNumber(groupName, username, li);
            for (Integer i : repeatList) {
                iWikiClassService.deletePageClass(username, BaseStage.SPACE, groupName, BaseStage.XWIKIGROUPS, i);
            }
        }

        return !list.isEmpty() ? list.stream().distinct().collect(Collectors.toList()) : list;
    }

    private String getGroupName(GroupMemberDTO groupMemberDTO, String username) {
        List<String> roleLabels = groupMemberDTO.getRoleLabels();
        if (roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_ADMIN.getResourceType()) || roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_ADMIN.getResourceType())) {
            return getGroupNameBuffer(groupMemberDTO, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString();
        } else if (roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_USER.getResourceType()) || roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_USER.getResourceType())) {
            return getGroupNameBuffer(groupMemberDTO, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString();
        } else {
            return "";
        }
    }

    private StringBuilder getGroupNameBuffer(GroupMemberDTO groupMemberDTO, String username, String type) {
        Long resourceId = groupMemberDTO.getResourceId();
        String resourceType = groupMemberDTO.getResourceType();
        StringBuilder groupName = new StringBuilder();
        if (ResourceLevel.ORGANIZATION.value().equals(resourceType)) {
            groupName.append(BaseStage.O);
            //通过组织id获取组织code
            OrganizationE organization = iamRepository.queryOrganizationById(resourceId);
            groupName.append(organization.getCode());

        } else if (ResourceLevel.PROJECT.value().equals(resourceType)) {
            groupName.append(BaseStage.P);
            //通过项目id找到项目code
            ProjectE projectE = iamRepository.queryIamProject(resourceId);
            OrganizationE organizationE = iamRepository.queryOrganizationById(projectE.getOrganization().getId());
            if (iWikiUserService.checkDocExsist(username, BaseStage.P + organizationE.getCode() + BaseStage.LINE + projectE.getCode() + type)) {
                groupName.append(organizationE.getCode() + BaseStage.LINE + projectE.getCode());
            } else if (iWikiUserService.checkDocExsist(username, BaseStage.P + projectE.getCode() + type)) {
                groupName.append(projectE.getCode());
            }
        }

        return groupName;
    }

    private String getUserXml(WikiUserE wikiUserE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/user.xml");
        Map<String, String> params = new HashMap<>(16);
        params.put("{{ LAST_NAME }}", wikiUserE.getLastName());
        params.put("{{ FIRST_NAME }}", wikiUserE.getFirstName());
        params.put("{{ USER_EMAIL }}", wikiUserE.getEmail());
        params.put("{{ PHONE }}", wikiUserE.getPhone());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private List<Integer> getGroupsObjectNumber(String groupName, String username, String loginName) {
        List<Integer> list = new ArrayList<>();
        try {
            String page = iWikiClassService.getPageClassResource(BaseStage.SPACE, groupName, BaseStage.XWIKIGROUPS, username);
            if (!StringUtils.isEmpty(page)) {
                Document doc = DocumentHelper.parseText(page);
                Element rootElt = doc.getRootElement();
                Iterator iter = rootElt.elementIterator("objectSummary");
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String pageName = recordEle.elementTextTrim("pageName");
                    if (groupName.equals(pageName)) {
                        String headline = recordEle.elementTextTrim("headline");
                        if (!StringUtils.isEmpty(headline) && loginName.equals(headline.substring(6))) {
                            list.add(Integer.valueOf(recordEle.elementTextTrim("number")));
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            throw new CommonException("error.document.get", e);
        }

        return list;
    }

    private List<Integer> getGlobalRightsObjectNumber(String org, String project, String username) {
        List<Integer> list = new ArrayList<>();
        try {
            String page;
            if (project == null) {
                page = iWikiClassService.getPageClassResource(org, BaseStage.WEBPREFERENCES, BaseStage.XWIKIGLOBALRIGHTS, username);
            } else {
                page = iWikiClassService.getProjectPageClassResource(org, project, BaseStage.WEBPREFERENCES, BaseStage.XWIKIGLOBALRIGHTS, username);
            }
            if (!StringUtils.isEmpty(page)) {
                Document doc = DocumentHelper.parseText(page);
                Element rootElt = doc.getRootElement();
                Iterator iter = rootElt.elementIterator("objectSummary");
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String className = recordEle.elementTextTrim("className");
                    if (BaseStage.XWIKIGLOBALRIGHTS.equals(className)) {
                        String headline = recordEle.elementTextTrim("headline");
                        if (!StringUtils.isEmpty(headline) && Integer.valueOf(headline) == 0) {
                            list.add(Integer.valueOf(recordEle.elementTextTrim("number")));
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            throw new CommonException("error.document.get", e);
        }

        return list;
    }

    private void deletePageClass(String pageName, String username, String deleteUsername) {
        if (!StringUtils.isEmpty(pageName)) {
            List<Integer> list = getGroupsObjectNumber(pageName, username, deleteUsername);
            for (Integer i : list) {
                //删除角色
                iWikiClassService.deletePageClass(username, BaseStage.SPACE, pageName, BaseStage.XWIKIGROUPS, i);
            }
        }
    }

    public void deleteGroupMember(List<String> roleLabels, GroupMemberDTO groupMember, String username) {
        if (!roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_ADMIN.getResourceType()) && ResourceLevel.PROJECT.value().equals(groupMember.getResourceType())) {
            String adminGroupName = getGroupNameBuffer(groupMember, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString();
            deletePageClass(adminGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_ADMIN.getResourceType()) && ResourceLevel.ORGANIZATION.value().equals(groupMember.getResourceType())) {
            String adminGroupName = getGroupNameBuffer(groupMember, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString();
            deletePageClass(adminGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(OrganizationSpaceType.PROJECT_WIKI_USER.getResourceType()) && ResourceLevel.PROJECT.value().equals(groupMember.getResourceType())) {
            String userGroupName = getGroupNameBuffer(groupMember, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString();
            deletePageClass(userGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(OrganizationSpaceType.ORGANIZATION_WIKI_USER.getResourceType()) && ResourceLevel.ORGANIZATION.value().equals(groupMember.getResourceType())) {
            String userGroupName = getGroupNameBuffer(groupMember, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString();
            deletePageClass(userGroupName, username, groupMember.getUsername());
        }
    }

    public Boolean checkDocExsist(String username, String groupName) {
        return iWikiUserService.checkDocExsist(username, groupName);
    }
}

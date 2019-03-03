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
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiClassService;
import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.common.enums.WikiRoleType;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Ernst on 2018/7/4.
 */
@Service
public class WikiGroupServiceImpl implements WikiGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiGroupServiceImpl.class);

    private IWikiGroupService iWikiGroupService;
    private IWikiUserService iWikiUserService;
    private IamRepository iamRepository;
    private IWikiClassService iWikiClassService;
    private WikiSpaceRepository wikiSpaceRepository;

    public WikiGroupServiceImpl(IWikiGroupService iWikiGroupService,
                                IWikiUserService iWikiUserService,
                                IamRepository iamRepository,
                                IWikiClassService iWikiClassService,
                                WikiSpaceRepository wikiSpaceRepository) {
        this.iWikiGroupService = iWikiGroupService;
        this.iWikiUserService = iWikiUserService;
        this.iamRepository = iamRepository;
        this.iWikiClassService = iWikiClassService;
        this.wikiSpaceRepository = wikiSpaceRepository;
    }

    @Override
    public Boolean create(WikiGroupDTO wikiGroupDTO, String username, Boolean isAdmin, Boolean isOrg) {
        Boolean assign = false;
        if (!checkDocExsist(username, wikiGroupDTO.getGroupName())) {
            if (iWikiGroupService.createGroup(wikiGroupDTO.getGroupName(), username)) {
                String[] adminRights = {"login", "view", "edit", "delete", "creator", "register", "comment", "script", "admin", "createwiki", "programming"};
                List<String> adminRightsList = Arrays.asList(adminRights);
                String[] userRights = {"login", "view", "edit", "creator", "comment"};
                List<String> userRightsList = Arrays.asList(userRights);
                if (isAdmin) {
                    if (isOrg) {
                        //给组织组分配admin权限
                        assign = iWikiGroupService.addRightsToOrg(wikiGroupDTO, adminRightsList, isAdmin, username);
                    } else {
                        //给项目组分配admin权限
                        assign = iWikiGroupService.addRightsToProject(wikiGroupDTO, adminRightsList, isAdmin, username);
                    }
                } else {
                    if (isOrg) {
                        //给组织组分配user权限
                        assign = iWikiGroupService.addRightsToOrg(wikiGroupDTO, userRightsList, isAdmin, username);
                    } else {
                        //给项目组分配user权限
                        assign = iWikiGroupService.addRightsToProject(wikiGroupDTO, userRightsList, isAdmin, username);
                    }
                }
            }
        }

        if (assign) {
            return true;
        } else {
            throw new CommonException("permission assignment failed");
        }
    }

    @Override
    public void createWikiGroupUsers(List<GroupMemberDTO> groupMemberDTOList, String username) {
        groupMemberDTOList.stream()
                .filter(groupMember -> groupMember.getRoleLabels() != null)
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
                            if (!iWikiUserService.createUser(user.getLoginName(), xmlParam, username)) {
                                throw new CommonException("error.wiki.user.create");
                            }
                        }

                        List<Integer> list = getGroupsObjectNumber(groupName, username, user.getLoginName());
                        if (list == null || list.isEmpty()) {
                            iWikiGroupService.createGroupUsers(groupName, user.getLoginName(), username);
                        }

                        if (ResourceLevel.PROJECT.value().equals(groupMember.getResourceType())
                                && (groupMember.getRoleLabels().contains(WikiRoleType.PROJECT_WIKI_USER.getResourceType())
                                || groupMember.getRoleLabels().contains(WikiRoleType.PROJECT_WIKI_ADMIN.getResourceType()))) {
                            ProjectE projectE = iamRepository.queryIamProject(groupMember.getResourceId());
                            LOGGER.info("projectE : {}", projectE.toString());
                            if (projectE.getOrganization().getId() != null) {
                                OrganizationE organizationE = iamRepository.queryOrganizationById(projectE.getOrganization().getId());
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(BaseStage.O).append(organizationE.getCode()).append(BaseStage.USER_GROUP);
                                List<Integer> list1 = getGroupsObjectNumber(stringBuilder.toString(), username, user.getLoginName());
                                if (list1 == null || list1.isEmpty()) {
                                    iWikiGroupService.createGroupUsers(stringBuilder.toString(), user.getLoginName(), username);
                                }
                            }
                        }
                    } else {
                        throw new CommonException("group name is null");
                    }
                });
    }

    @Override
    public void deleteWikiGroupUsers(List<GroupMemberDTO> groupMemberDTOList, String username) {
        groupMemberDTOList.stream()
                .forEach(groupMember -> {
                    List<String> roleLabels = groupMember.getRoleLabels();
                    if (groupMember.getResourceType().equals(ResourceLevel.SITE.value())) {
                        if (roleLabels == null || (roleLabels != null && !roleLabels.contains(WikiRoleType.SITE_ADMIN.getResourceType()))) {
                            deletePageClass(BaseStage.XWIKI_ADMIN_GROUP, username, groupMember.getUsername());
                        }
                    } else {
                        if (roleLabels == null || roleLabels.isEmpty()) {
                            String adminGroupName = getGroupNameBuffer(groupMember, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString();
                            String userGroupName = getGroupNameBuffer(groupMember, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString();
                            deletePageClass(adminGroupName, username, groupMember.getUsername());
                            deletePageClass(userGroupName, username, groupMember.getUsername());
                        } else {
                            deleteGroupMember(roleLabels, groupMember, username);
                        }
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
                        LOGGER.info("user:{} exist ? {}", loginName, flag);
                        if (!flag) {
                            WikiUserE wikiUserE = new WikiUserE();
                            wikiUserE.setFirstName(user.getLoginName());
                            wikiUserE.setLastName(user.getRealName());
                            wikiUserE.setPhone(user.getPhone());
                            wikiUserE.setEmail(user.getEmail());

                            String xmlParam = getUserXml(wikiUserE);
                            if (!iWikiUserService.createUser(loginName, xmlParam, username)) {
                                throw new CommonException("error.wiki.user.create");
                            }
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
            List<WikiSpaceE> wikiSpaceList = wikiSpaceRepository.getWikiSpaceList(organization.getId(), WikiSpaceResourceType.ORGANIZATION.getResourceType());
            if (wikiSpaceList != null && !wikiSpaceList.isEmpty() && wikiSpaceList.get(0).getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
                iWikiGroupService.disableOrgGroupView(organization.getCode(), wikiSpaceList.get(0).getPath().substring(2), username);
            }
        } else {
            throw new CommonException("error.query.organization");
        }
    }

    @Override
    public void enableOrganizationGroup(OrganizationE organization, String username) {
        if (organization != null) {
            LOGGER.info("enable organization group,orgId: {} and organization: {} ", organization.getId(), organization.toString());
            List<Integer> list = getGlobalRightsObjectNumber(BaseStage.O + organization.getName(), null, username);
            for (Integer i : list) {
                //删除角色
                iWikiClassService.deletePageClass(username, BaseStage.O + organization.getName(), BaseStage.WEBPREFERENCES, BaseStage.XWIKIGLOBALRIGHTS, i);
            }
        } else {
            throw new CommonException("error.get.organization.infor");
        }
    }

    @Override
    public void disableProjectGroup(Long projectId, String username) {
        ProjectE projectE = iamRepository.queryIamProject(projectId);
        if (projectE != null) {
            LOGGER.info("disable project group,projectId: {} and project: {} ", projectId, projectE.toString());
            Long orgId = projectE.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            List<WikiSpaceE> wikiSpaceList = wikiSpaceRepository.getWikiSpaceList(projectE.getId(), WikiSpaceResourceType.PROJECT.getResourceType());
            if (wikiSpaceList != null && !wikiSpaceList.isEmpty() && wikiSpaceList.get(0).getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
                String[] param = wikiSpaceList.get(0).getPath().split("/");
                iWikiGroupService.disableProjectGroupView(param[1].substring(2), projectE.getCode(), param[0].substring(2), organization.getCode(), username);
            }
        } else {
            throw new CommonException("error.query.project");
        }
    }

    @Override
    public void enableProjectGroup(OrganizationE organization, ProjectE projectE, String username) {
        LOGGER.info("enable project group,projectId: {} and project: {} ", projectE.getId(), projectE.toString());
        if (organization != null) {
            List<Integer> list = getGlobalRightsObjectNumber(BaseStage.O + organization.getName(),
                    BaseStage.P + projectE.getName(), username);
            for (Integer i : list) {
                //删除角色
                iWikiClassService.deleteProjectPageClass(username, BaseStage.O + organization.getName(), BaseStage.P + projectE.getName(), BaseStage.WEBPREFERENCES, BaseStage.XWIKIGLOBALRIGHTS, i);
            }
        }
    }

    @Override
    public void setUserToGroup(String groupName, Long userId, String username) {
        Long[] ids = new Long[1];
        ids[0] = userId;
        UserE userE = iamRepository.queryUserByIds(ids, false);
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

                if (!iWikiUserService.createUser(loginName, getUserXml(wikiUserE), username)) {
                    throw new CommonException("error.wiki.user.create");
                }
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
        if (roleLabels.contains(WikiRoleType.PROJECT_WIKI_ADMIN.getResourceType()) || roleLabels.contains(WikiRoleType.ORGANIZATION_WIKI_ADMIN.getResourceType())) {
            return !StringUtils.isEmpty(getGroupNameBuffer(groupMemberDTO, username, BaseStage.ADMIN_GROUP).toString()) ? getGroupNameBuffer(groupMemberDTO, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString() : "";
        } else if (roleLabels.contains(WikiRoleType.PROJECT_WIKI_USER.getResourceType()) || roleLabels.contains(WikiRoleType.ORGANIZATION_WIKI_USER.getResourceType())) {
            return !StringUtils.isEmpty(getGroupNameBuffer(groupMemberDTO, username, BaseStage.USER_GROUP).toString()) ? getGroupNameBuffer(groupMemberDTO, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString() : "";
        } else if (ResourceLevel.SITE.value().equals(groupMemberDTO.getResourceType()) && roleLabels.contains(WikiRoleType.SITE_ADMIN.getResourceType())) {
            return BaseStage.XWIKI_ADMIN_GROUP;
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
            if (organization == null) {
                return new StringBuilder();
            }
            groupName.append(organization.getCode());

        } else if (ResourceLevel.PROJECT.value().equals(resourceType)) {
            groupName.append(BaseStage.P);
            //通过项目id找到项目code
            ProjectE projectE = iamRepository.queryIamProject(resourceId);
            if (projectE.getOrganization().getId() == null) {
                return new StringBuilder();
            }
            OrganizationE organizationE = iamRepository.queryOrganizationById(projectE.getOrganization().getId());
            if (iWikiUserService.checkDocExsist(username, BaseStage.P + organizationE.getCode() + BaseStage.LINE + projectE.getCode() + type)) {
                groupName.append(organizationE.getCode() + BaseStage.LINE + projectE.getCode());
            } else if (iWikiUserService.checkDocExsist(username, BaseStage.P + projectE.getCode() + type)) {
                groupName.append(projectE.getCode());
            } else {
                return new StringBuilder();
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

    @Override
    public List<Integer> getGroupsObjectNumber(String groupName, String username, String loginName) {
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
                        LOGGER.info("loginName: {} get headline: {}", loginName, headline);
                        if (!StringUtils.isEmpty(headline) && headline.startsWith(BaseStage.XWiki) && loginName.equals(headline.substring(6))) {
                            list.add(Integer.valueOf(recordEle.elementTextTrim("number")));
                        } else if (!StringUtils.isEmpty(headline) && !headline.startsWith(BaseStage.XWiki)) {
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
        if (!roleLabels.contains(WikiRoleType.PROJECT_WIKI_ADMIN.getResourceType()) && ResourceLevel.PROJECT.value().equals(groupMember.getResourceType())) {
            String adminGroupName = getGroupNameBuffer(groupMember, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString();
            deletePageClass(adminGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(WikiRoleType.ORGANIZATION_WIKI_ADMIN.getResourceType()) && ResourceLevel.ORGANIZATION.value().equals(groupMember.getResourceType())) {
            String adminGroupName = getGroupNameBuffer(groupMember, username, BaseStage.ADMIN_GROUP).append(BaseStage.ADMIN_GROUP).toString();
            deletePageClass(adminGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(WikiRoleType.PROJECT_WIKI_USER.getResourceType()) && ResourceLevel.PROJECT.value().equals(groupMember.getResourceType())) {
            String userGroupName = getGroupNameBuffer(groupMember, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString();
            deletePageClass(userGroupName, username, groupMember.getUsername());
        }
        if (!roleLabels.contains(WikiRoleType.ORGANIZATION_WIKI_USER.getResourceType()) && ResourceLevel.ORGANIZATION.value().equals(groupMember.getResourceType())) {
            String userGroupName = getGroupNameBuffer(groupMember, username, BaseStage.USER_GROUP).append(BaseStage.USER_GROUP).toString();
            deletePageClass(userGroupName, username, groupMember.getUsername());
        }
    }

    public Boolean checkDocExsist(String username, String groupName) {
        return iWikiUserService.checkDocExsist(username, groupName);
    }
}

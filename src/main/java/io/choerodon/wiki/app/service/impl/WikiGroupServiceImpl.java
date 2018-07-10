package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.wiki.api.dto.GitlabGroupMemberDTO;
import io.choerodon.wiki.api.dto.GitlabUserDTO;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.valueobject.Organization;
import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.OrganizationSpaceType;

/**
 * Created by Ernst on 2018/7/4.
 */
@Service
public class WikiGroupServiceImpl implements WikiGroupService {

    private IWikiGroupService iWikiGroupService;

    private IWikiUserService iWikiUserService;

    private IamRepository iamRepository;


    public WikiGroupServiceImpl(IWikiGroupService iWikiGroupService,IWikiUserService iWikiUserService, IamRepository iamRepository) {
        this.iWikiGroupService = iWikiGroupService;
        this.iWikiUserService = iWikiUserService;
        this.iamRepository = iamRepository;
    }

    @Override
    public Boolean create(WikiGroupDTO wikiGroupDTO) {
        Boolean flag = iWikiUserService.checkDocExsist(wikiGroupDTO.getGroupName());
        if(!flag){
            return iWikiGroupService.createGroup(wikiGroupDTO.getGroupName(),getGroupXml());
        }
        return false;
    }

    @Override
    public void createWikiGroupUsers(List<GitlabGroupMemberDTO> gitlabGroupMemberList) {

        //创建用户
        for(GitlabGroupMemberDTO gitlabGroupMemberDTO:gitlabGroupMemberList){
            //根据用户名查询用户信息
            String userName = gitlabGroupMemberDTO.getUsername();
            UserE user = iamRepository.queryByLoginName(userName);
            WikiUserE wikiUserE = new WikiUserE();
            wikiUserE.setLastName(user.getRealName());
            wikiUserE.setEmail(user.getEmail());
            String xmlParam = getUserXml(wikiUserE);
            if(!iWikiUserService.checkDocExsist(user.getLoginName())){
                iWikiUserService.createUser(wikiUserE,user.getLoginName(),xmlParam);
            }

            //将用户分配到组
            List<String> roleLabels = gitlabGroupMemberDTO.getRoleLabels();
            Long resourceId = gitlabGroupMemberDTO.getResourceId();
            String resourceType = gitlabGroupMemberDTO.getResourceType();
            StringBuffer groupName = new StringBuffer();
            if(ResourceLevel.ORGANIZATION.equals(resourceType)){
                groupName.append("O-");
                //通过组织id获取组织code
                Organization organization = iamRepository.queryOrganizationById(resourceId);
                groupName.append(organization.getCode());

            }else if(ResourceLevel.PROJECT.equals(OrganizationSpaceType.PROJECT_OWNER)){
                groupName.append("P-");
                //通过项目id找到项目code
                ProjectE projectE = iamRepository.queryIamProject(resourceId);
                groupName.append(projectE.getCode());
            }

            if(roleLabels.contains(OrganizationSpaceType.ORGANIZATION_OWNER) || roleLabels.contains(OrganizationSpaceType.PROJECT_OWNER)){
                groupName.append("AdminGroup");
            }else {
                groupName.append("UserGroup");
            }

            //通过groupName给组添加成员
            iWikiGroupService.createGroupUsers(groupName.toString(),userName);
        }


    }

    @Override
    public void createWikiUserToGroup(GitlabUserDTO gitlabUserDTO) {
        String loginName = gitlabUserDTO.getUsername();
        UserE user = iamRepository.queryByLoginName(loginName);
        Long orgId = user.getOrganizationId();
        Organization organization = iamRepository.queryOrganizationById(orgId);
        String orgCode = organization.getCode();
        String groupName = "O-"+orgCode+"UserGroup";
        //通过groupName给组添加成员
        iWikiGroupService.createGroupUsers(groupName,loginName);
    }

    private String getGroupXml() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/group.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getUserXml(WikiUserE wikiUserE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/user.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ FIRST_NAME }}", wikiUserE.getFirstName());
        params.put("{{ LAST_NAME }}", wikiUserE.getLastName());
        params.put("{{ USER_EMAIL }}", wikiUserE.getEmail());
        return FileUtil.replaceReturnString(inputStream, params);
    }
}

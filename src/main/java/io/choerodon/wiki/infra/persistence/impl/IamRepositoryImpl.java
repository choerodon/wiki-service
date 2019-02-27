package io.choerodon.wiki.infra.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.api.eventhandler.WikiEventHandler;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.RoleE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.valueobject.RoleAssignmentSearch;
import io.choerodon.wiki.infra.dataobject.iam.*;
import io.choerodon.wiki.infra.feign.IamServiceClient;

/**
 * Created by Zenger on 2018/6/29.
 */
@Component
public class IamRepositoryImpl implements IamRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(IamRepositoryImpl.class);

    private IamServiceClient iamServiceClient;

    public IamRepositoryImpl(IamServiceClient iamServiceClient) {
        this.iamServiceClient = iamServiceClient;
    }


    @Override
    public UserE queryByLoginName(String userName) {
        ResponseEntity<UserDO> responseEntity = iamServiceClient.queryByLoginName(userName);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return ConvertHelper.convert(responseEntity.getBody(), UserE.class);
        } else {
            throw new CommonException("error.user.get");
        }
    }

    @Override
    public OrganizationE queryOrganizationById(Long organizationId) {
        ResponseEntity<OrganizationDO> organization = iamServiceClient.queryOrganizationById(organizationId);
        if (organization.getStatusCode().is2xxSuccessful()) {
            return ConvertHelper.convert(organization.getBody(), OrganizationE.class);
        } else {
            throw new CommonException("error.organization.get");
        }
    }

    @Override
    public ProjectE queryIamProject(Long projectId) {
        ResponseEntity<ProjectDO> projectDO = iamServiceClient.queryIamProject(projectId);
        if (!projectDO.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.project.get");
        }
        return ConvertHelper.convert(projectDO.getBody(), ProjectE.class);
    }

    @Override
    public UserE queryUserById(Long userId) {
        LOGGER.info("query user by userId:{}",userId);
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        ResponseEntity<List<UserDO>> responseEntity = iamServiceClient.queryUsersByIds(userIds);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.user.get");
        }
        List<UserDO> list = responseEntity.getBody();
        LOGGER.info("query user info",list.toString());
        if (list != null && list.size() == 1) {
            return ConvertHelper.convert(list.get(0), UserE.class);
        } else {
            throw new CommonException("error.user.query");
        }
    }

    @Override
    public Page<OrganizationE> pageByOrganization(int page, int size) {
        ResponseEntity<Page<OrganizationDO>> responseEntity = iamServiceClient.pageByOrganization(page, size);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.organization.get");
        }
        Page<OrganizationDO> organizationDOPage = responseEntity.getBody();
        if (organizationDOPage != null && !organizationDOPage.isEmpty()) {
            return ConvertPageHelper.convertPage(organizationDOPage, OrganizationE.class);
        } else {
            throw new CommonException("error.organization.get");
        }
    }

    @Override
    public Page<ProjectE> pageByProject(Long organizationId, int page, int size) {
        ResponseEntity<Page<ProjectDO>> responseEntity = iamServiceClient.pageByProject(organizationId, page, size);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.project.get");
        }
        Page<ProjectDO> projectDOPage = responseEntity.getBody();
        if (projectDOPage != null && !projectDOPage.isEmpty()) {
            return ConvertPageHelper.convertPage(projectDOPage, ProjectE.class);
        } else {
            throw new CommonException("error.project.get");
        }
    }

    @Override
    public Page<RoleE> roleList(String code) {
        RoleSearchDO roleSearchDO = new RoleSearchDO();
        roleSearchDO.setCode(code);
        ResponseEntity<Page<RoleDO>> responseEntity = iamServiceClient.roleList(roleSearchDO);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.role.get");
        }
        Page<RoleDO> roleDOPage = responseEntity.getBody();
        if (roleDOPage != null && !roleDOPage.isEmpty()) {
            return ConvertPageHelper.convertPage(roleDOPage, RoleE.class);
        } else {
            throw new CommonException("error.role.get");
        }
    }

    @Override
    public Page<UserE> pagingQueryUsersByRoleIdOnProjectLevel(Long roleId, Long projectId, int page, int size) {
        ResponseEntity<Page<UserDO>> responseEntity =
                iamServiceClient.pagingQueryUsersByRoleIdOnProjectLevel(roleId, projectId, page, size, new RoleAssignmentSearch());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.organization.get");
        }
        Page<UserDO> userDOPage = responseEntity.getBody();
        return ConvertPageHelper.convertPage(userDOPage, UserE.class);
    }

    @Override
    public Page<UserE> pagingQueryUsersByRoleIdOnOrganizationLevel(Long roleId, Long organizationId, int page, int size) {
        ResponseEntity<Page<UserDO>> responseEntity =
                iamServiceClient.pagingQueryUsersByRoleIdOnOrganizationLevel(roleId, organizationId, page, size,new RoleAssignmentSearch());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.organization.get");
        }
        Page<UserDO> userDOPage = responseEntity.getBody();
        return ConvertPageHelper.convertPage(userDOPage, UserE.class);
    }

    @Override
    public Page<UserWithRoleDO> pagingQueryUsersWithProjectLevelRoles(Long projectId) {
        ResponseEntity<Page<UserWithRoleDO>> responseEntity = iamServiceClient.pagingQueryUsersWithProjectLevelRoles(projectId,new RoleAssignmentSearch(),false);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.project.user.and.role.get");
        }
        return responseEntity.getBody();
    }

    @Override
    public Page<UserWithRoleDO> pagingQueryUsersWithSiteLevelRoles(int page, int size) {
        ResponseEntity<Page<UserWithRoleDO>> responseEntity = iamServiceClient.pagingQueryUsersWithSiteLevelRoles(page, size, new RoleAssignmentSearch());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.site.user.and.role.get");
        }
        return responseEntity.getBody();
    }

    @Override
    public RoleE queryWithPermissionsAndLabels(Long id) {
        ResponseEntity<RoleDO> responseEntity = iamServiceClient.queryWithPermissionsAndLabels(id);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.role.get");
        }
        return ConvertHelper.convert(responseEntity.getBody(), RoleE.class);
    }
}

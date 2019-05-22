package io.choerodon.wiki.infra.persistence.impl;

import java.util.List;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
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
    public UserE queryUserById(Long organizationId, Long id) {
        ResponseEntity<UserDO> userDOResponseEntity = iamServiceClient.query(organizationId, id);
        LOGGER.info("query user info:{}", userDOResponseEntity.getBody().toString());
        return ConvertHelper.convert(userDOResponseEntity.getBody(), UserE.class);
    }

    @Override
    public UserE queryUserByIds(Long[] ids, Boolean flag) {
        ResponseEntity<List<UserDO>> responseEntity = iamServiceClient.listUsersByIds(ids, flag);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.user.get");
        }
        List<UserDO> list = responseEntity.getBody();
        LOGGER.info("query user info:{}", list.toString());
        if (list != null && list.size() == 1) {
            return ConvertHelper.convert(list.get(0), UserE.class);
        } else {
            throw new CommonException("error.user.query");
        }
    }

    @Override
    public List<OrganizationE> pageByOrganization(int page, int size) {
        ResponseEntity<PageInfo<OrganizationDO>> responseEntity = iamServiceClient.pageByOrganization(page, size);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.organization.get");
        }
        return ConvertHelper.convertList(responseEntity.getBody().getList(), OrganizationE.class);
    }

    @Override
    public List<ProjectE> pageByProject(Long organizationId, int page, int size) {
        ResponseEntity<PageInfo<ProjectDO>> responseEntity = iamServiceClient.pageByProject(organizationId, page, size);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.project.get");
        }
        return ConvertHelper.convertList(responseEntity.getBody().getList(), ProjectE.class);
    }

    @Override
    public List<RoleE> roleList(String code) {
        RoleSearchDO roleSearchDO = new RoleSearchDO();
        roleSearchDO.setCode(code);
        ResponseEntity<PageInfo<RoleDO>> responseEntity = iamServiceClient.roleList(roleSearchDO);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.role.get");
        }
        return ConvertHelper.convertList(responseEntity.getBody().getList(), RoleE.class);
    }

    @Override
    public List<UserE> pagingQueryUsersByRoleIdOnProjectLevel(Long roleId, Long projectId, int page, int size) {
        ResponseEntity<PageInfo<UserDO>> responseEntity =
                iamServiceClient.pagingQueryUsersByRoleIdOnProjectLevel(roleId, projectId, page, size, new RoleAssignmentSearch());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.organization.get");
        }
        return ConvertHelper.convertList(responseEntity.getBody().getList(), UserE.class);
    }

    @Override
    public List<UserE> pagingQueryUsersByRoleIdOnOrganizationLevel(Long roleId, Long organizationId, int page, int size) {
        ResponseEntity<PageInfo<UserDO>> responseEntity =
                iamServiceClient.pagingQueryUsersByRoleIdOnOrganizationLevel(roleId, organizationId, page, size, new RoleAssignmentSearch());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.organization.get");
        }
        return ConvertHelper.convertList(responseEntity.getBody().getList(), UserE.class);
    }

    @Override
    public List<UserWithRoleDO> pagingQueryUsersWithProjectLevelRoles(Long projectId) {
        ResponseEntity<PageInfo<UserWithRoleDO>> responseEntity = iamServiceClient.pagingQueryUsersWithProjectLevelRoles(projectId, new RoleAssignmentSearch(), false);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.project.user.and.role.get");
        }
        return responseEntity.getBody().getList();
    }

    @Override
    public List<UserWithRoleDO> pagingQueryUsersWithSiteLevelRoles(int page, int size) {
        ResponseEntity<PageInfo<UserWithRoleDO>> responseEntity = iamServiceClient.pagingQueryUsersWithSiteLevelRoles(page, size, new RoleAssignmentSearch());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.site.user.and.role.get");
        }
        return responseEntity.getBody().getList();
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

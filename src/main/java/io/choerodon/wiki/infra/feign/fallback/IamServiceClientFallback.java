package io.choerodon.wiki.infra.feign.fallback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.wiki.domain.application.valueobject.RoleAssignmentSearch;
import io.choerodon.wiki.infra.dataobject.iam.*;
import io.choerodon.wiki.infra.feign.IamServiceClient;

/**
 * Created by Zenger on 2018/3/29.
 */
@Component
public class IamServiceClientFallback implements IamServiceClient {

    @Override
    public ResponseEntity<UserDO> queryByLoginName(String loginName) {
        throw new FeignException("error.user.get");
    }

    @Override
    public ResponseEntity<OrganizationDO> queryOrganizationById(Long organizationId) {
        throw new FeignException("error.organization.get");
    }

    @Override
    public ResponseEntity<ProjectDO> queryIamProject(Long projectId) {
        throw new FeignException("error.project.get");
    }

    @Override
    public ResponseEntity<List<UserDO>> queryUsersByIds(List<Long> ids) {
        throw new FeignException("error.user.get");
    }

    @Override
    public ResponseEntity<UserDO> query(Long organizationId, Long id) {
        throw new FeignException("error.user.get");
    }

    @Override
    public ResponseEntity<Page<OrganizationDO>> pageByOrganization(int page, int size) {
        throw new FeignException("error.organization.get");
    }

    @Override
    public ResponseEntity<OrganizationDO> queryOrgById(Long organizationId) {
        throw new FeignException("error.organization.get");
    }

    @Override
    public ResponseEntity<Page<ProjectDO>> pageByProject(Long organizationId, int page, int size) {
        throw new FeignException("error.project.get");
    }

    @Override
    public ResponseEntity<Page<RoleDO>> roleList(RoleSearchDO role) {
        throw new FeignException("error.role.get");
    }

    @Override
    public ResponseEntity<Page<UserDO>> pagingQueryUsersByRoleIdOnProjectLevel(Long roleId,
                                                                               Long sourceId,
                                                                               int page,
                                                                               int size,
                                                                               @RequestBody RoleAssignmentSearch roleAssignmentSearch) {
        throw new FeignException("error.user.get");
    }

    @Override
    public ResponseEntity<Page<UserDO>> pagingQueryUsersByRoleIdOnOrganizationLevel(Long roleId,
                                                                                    Long sourceId,
                                                                                    int page,
                                                                                    int size,
                                                                                    @RequestBody RoleAssignmentSearch roleAssignmentSearch) {
        throw new FeignException("error.user.get");
    }

    @Override
    public ResponseEntity<Page<UserWithRoleDO>> pagingQueryUsersWithProjectLevelRoles(Long sourceId,
                                                                                      RoleAssignmentSearch roleAssignmentSearchDTO,
                                                                                      boolean doPage) {
        throw new CommonException("error.project.user.and.role.get");
    }

    @Override
    public ResponseEntity<RoleDO> queryWithPermissionsAndLabels(Long id) {
        throw new CommonException("error.role.get");
    }

    @Override
    public ResponseEntity<Page<UserWithRoleDO>> pagingQueryUsersWithSiteLevelRoles(int page, int size, RoleAssignmentSearch roleAssignmentSearchDTO) {
        throw new CommonException("error.site.user.and.role.get");
    }
}

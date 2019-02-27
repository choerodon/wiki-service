package io.choerodon.wiki.domain.application.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.RoleE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.infra.dataobject.iam.UserWithRoleDO;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface IamRepository {

    UserE queryByLoginName(String userName);

    OrganizationE queryOrganizationById(Long organizationId);

    ProjectE queryIamProject(Long projectId);

    UserE queryUserByIds(Long[] ids,Boolean flag);

    UserE queryUserById(Long organizationId, Long id);

    Page<OrganizationE> pageByOrganization(int page, int size);

    Page<ProjectE> pageByProject(Long organizationId, int page, int size);

    Page<RoleE> roleList(String code);

    Page<UserE> pagingQueryUsersByRoleIdOnProjectLevel(Long roleId, Long projectId,int page, int size);

    Page<UserE> pagingQueryUsersByRoleIdOnOrganizationLevel(Long roleId, Long organizationId,int page, int size);

    Page<UserWithRoleDO> pagingQueryUsersWithProjectLevelRoles(Long projectId);

    Page<UserWithRoleDO> pagingQueryUsersWithSiteLevelRoles(int page, int size);

    RoleE queryWithPermissionsAndLabels(Long id);
}

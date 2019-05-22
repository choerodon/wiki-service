package io.choerodon.wiki.domain.application.repository;

import java.util.List;

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

    UserE queryUserByIds(Long[] ids, Boolean flag);

    UserE queryUserById(Long organizationId, Long id);

    List<OrganizationE> pageByOrganization(int page, int size);

    List<ProjectE> pageByProject(Long organizationId, int page, int size);

    List<RoleE> roleList(String code);

    List<UserE> pagingQueryUsersByRoleIdOnProjectLevel(Long roleId, Long projectId, int page, int size);

    List<UserE> pagingQueryUsersByRoleIdOnOrganizationLevel(Long roleId, Long organizationId, int page, int size);

    List<UserWithRoleDO> pagingQueryUsersWithProjectLevelRoles(Long projectId);

    List<UserWithRoleDO> pagingQueryUsersWithSiteLevelRoles(int page, int size);

    RoleE queryWithPermissionsAndLabels(Long id);
}

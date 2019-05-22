package io.choerodon.wiki.infra.feign;

import java.util.List;

import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.wiki.domain.application.valueobject.RoleAssignmentSearch;
import io.choerodon.wiki.infra.dataobject.iam.*;
import io.choerodon.wiki.infra.feign.fallback.IamServiceClientFallback;

/**
 * Created by Ernst on 2018/7/9.
 */

@FeignClient(value = "iam-service", fallback = IamServiceClientFallback.class)
public interface IamServiceClient {

    @GetMapping(value = "/v1/users")
    ResponseEntity<UserDO> queryByLoginName(@RequestParam("login_name") String loginName);

    @GetMapping("/v1/organizations/{organizationId}")
    ResponseEntity<OrganizationDO> queryOrganizationById(@PathVariable("organizationId") Long organizationId);

    @GetMapping(value = "/v1/projects/{projectId}")
    ResponseEntity<ProjectDO> queryIamProject(@PathVariable("projectId") Long projectId);

    @GetMapping(value = "/v1/organizations/{organization_id}/users/{id}")
    ResponseEntity<UserDO> query(@PathVariable("organization_id") Long organizationId,
                                 @PathVariable("id") Long id);

    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserDO>> listUsersByIds(@RequestBody Long[] ids,
                                                @RequestParam(name = "only_enabled") Boolean onlyEnabled);

    @GetMapping(value = "/v1/organizations")
    ResponseEntity<PageInfo<OrganizationDO>> pageByOrganization(@RequestParam("page") int page, @RequestParam("size") int size);

    @GetMapping(value = "/v1/organizations/{organization_id}")
    ResponseEntity<OrganizationDO> queryOrgById(@PathVariable(name = "organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/projects")
    ResponseEntity<PageInfo<ProjectDO>> pageByProject(@PathVariable(name = "organization_id") Long organizationId,
                                                      @RequestParam("page") int page, @RequestParam("size") int size);

    @PostMapping(value = "/v1/roles/search")
    ResponseEntity<PageInfo<RoleDO>> roleList(@RequestBody(required = false) RoleSearchDO role);

    @PostMapping(value = "/v1/projects/{project_id}/role_members/users")
    ResponseEntity<PageInfo<UserDO>> pagingQueryUsersByRoleIdOnProjectLevel(
            @RequestParam(name = "role_id") Long roleId,
            @PathVariable(name = "project_id") Long sourceId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestBody RoleAssignmentSearch roleAssignmentSearch);

    @PostMapping(value = "/v1/organizations/{organization_id}/role_members/users")
    ResponseEntity<PageInfo<UserDO>> pagingQueryUsersByRoleIdOnOrganizationLevel(
            @RequestParam(name = "role_id") Long roleId,
            @PathVariable(name = "organization_id") Long sourceId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestBody RoleAssignmentSearch roleAssignmentSearch);

    @PostMapping(value = "/v1/projects/{project_id}/role_members/users/roles")
    ResponseEntity<PageInfo<UserWithRoleDO>> pagingQueryUsersWithProjectLevelRoles(
            @PathVariable(name = "project_id") Long sourceId,
            @RequestBody RoleAssignmentSearch roleAssignmentSearchDTO,
            @RequestParam(name = "doPage") boolean doPage);

    @GetMapping(value = "/v1/roles/{id}")
    ResponseEntity<RoleDO> queryWithPermissionsAndLabels(@PathVariable(name = "id") Long id);

    @PostMapping(value = "/v1/site/role_members/users/roles")
    ResponseEntity<PageInfo<UserWithRoleDO>> pagingQueryUsersWithSiteLevelRoles(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestBody RoleAssignmentSearch roleAssignmentSearchDTO);
}

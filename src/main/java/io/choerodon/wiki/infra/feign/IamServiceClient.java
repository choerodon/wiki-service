package io.choerodon.wiki.infra.feign;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
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

    @GetMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserDO>> queryUsersByIds(@RequestBody List<Long> ids);

    @GetMapping(value = "/v1/organizations")
    ResponseEntity<Page<OrganizationDO>> pageByOrganization(@RequestParam("page") int page, @RequestParam("size") int size);

    @GetMapping(value = "/v1/organizations/{organization_id}")
    ResponseEntity<OrganizationDO> queryOrgById(@PathVariable(name = "organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/projects")
    ResponseEntity<Page<ProjectDO>> pageByProject(@PathVariable(name = "organization_id") Long organizationId,
                                                  @RequestParam("page") int page, @RequestParam("size") int size);

    @PostMapping(value = "/v1/roles/search")
    public ResponseEntity<Page<RoleDO>> roleList(@RequestBody(required = false) RoleSearchDO role);

    @PostMapping(value = "/v1/projects/{project_id}/role_members/users")
    public ResponseEntity<Page<UserDO>> pagingQueryUsersByRoleIdOnProjectLevel(
            @RequestParam(name = "role_id") Long roleId,
            @PathVariable(name = "project_id") Long sourceId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestBody RoleAssignmentSearch roleAssignmentSearchDTO);

    @PostMapping(value = "/v1/organizations/{organization_id}/role_members/users")
    public ResponseEntity<Page<UserDO>> pagingQueryUsersByRoleIdOnOrganizationLevel(
            @RequestParam(name = "role_id") Long roleId,
            @PathVariable(name = "organization_id") Long sourceId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestBody RoleAssignmentSearch roleAssignmentSearchDTO);
}

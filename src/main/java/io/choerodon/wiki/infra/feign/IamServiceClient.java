package io.choerodon.wiki.infra.feign;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.core.domain.Page;
import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO;
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO;
import io.choerodon.wiki.infra.dataobject.iam.UserDO;
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

    @GetMapping(value = "/v1/organizations/{organization_id}/projects")
    ResponseEntity<Page<ProjectDO>> pageByProject(@PathVariable(name = "organization_id") Long organizationId,
                                                  @RequestParam("page") int page, @RequestParam("size") int size);
}

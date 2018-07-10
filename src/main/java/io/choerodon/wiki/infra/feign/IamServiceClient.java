package io.choerodon.wiki.infra.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO;
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO;
import io.choerodon.wiki.infra.dataobject.iam.UserDO;

/**
 * Created by Ernst on 2018/7/9.
 */

@FeignClient(value = "iam-service")
public interface IamServiceClient {

    @GetMapping(value = "/v1/users")
    ResponseEntity<UserDO> queryByLoginName(@RequestParam("login_name") String loginName);

    @GetMapping("/v1/organizations/{organizationId}")
    ResponseEntity<OrganizationDO> queryOrganizationById(@PathVariable("organizationId") Long organizationId);

    @GetMapping(value = "/v1/projects/{projectId}")
    ResponseEntity<ProjectDO> queryIamProject(@PathVariable("projectId") Long projectId);

}

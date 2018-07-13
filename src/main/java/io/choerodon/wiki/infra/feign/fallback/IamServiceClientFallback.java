package io.choerodon.wiki.infra.feign.fallback;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO;
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO;
import io.choerodon.wiki.infra.dataobject.iam.UserDO;
import io.choerodon.wiki.infra.feign.IamServiceClient;

/**
 * Created by younger on 2018/3/29.
 */
@Component
public class IamServiceClientFallback implements IamServiceClient {

    @Override
    public ResponseEntity<UserDO> queryByLoginName(String loginName) {
        return new ResponseEntity("error.user.get", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<OrganizationDO> queryOrganizationById(Long organizationId) {
        return new ResponseEntity("error.organization.get", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProjectDO> queryIamProject(Long projectId) {
        return new ResponseEntity("error.project.get", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserDO>> queryUsersByIds(List<Long> ids) {
        return new ResponseEntity("error.user.get", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

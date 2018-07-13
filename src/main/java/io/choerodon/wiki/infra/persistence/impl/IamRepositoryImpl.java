package io.choerodon.wiki.infra.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO;
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO;
import io.choerodon.wiki.infra.dataobject.iam.UserDO;
import io.choerodon.wiki.infra.feign.IamServiceClient;

/**
 * Created by younger on 2018/3/29.
 */
@Component
public class IamRepositoryImpl implements IamRepository {

    private IamServiceClient iamServiceClient;

    public IamRepositoryImpl(IamServiceClient iamServiceClient) {
        this.iamServiceClient = iamServiceClient;
    }


    @Override
    public UserE queryByLoginName(String userName) {
        try {
            ResponseEntity<UserDO> responseEntity = iamServiceClient.queryByLoginName(userName);
            return ConvertHelper.convert(responseEntity.getBody(), UserE.class);
        } catch (Exception e) {
            return null;
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
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        ResponseEntity<List<UserDO>> responseEntity = iamServiceClient.queryUsersByIds(userIds);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.user.get");
        }
        List<UserDO> list = responseEntity.getBody();
        if(list!=null && list.size()==1){
            return ConvertHelper.convert(list.get(0), UserE.class);
        }else {
            throw new CommonException("error.user.query");
        }
    }

}

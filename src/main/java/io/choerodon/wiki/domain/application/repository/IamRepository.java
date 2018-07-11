package io.choerodon.wiki.domain.application.repository;

import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface IamRepository {

    UserE queryByLoginName(String userName);

    OrganizationE queryOrganizationById(Long organizationId);

    ProjectE queryIamProject(Long projectId);
}

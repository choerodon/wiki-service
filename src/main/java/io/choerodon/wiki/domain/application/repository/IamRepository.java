package io.choerodon.wiki.domain.application.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface IamRepository {

    UserE queryByLoginName(String userName);

    OrganizationE queryOrganizationById(Long organizationId);

    ProjectE queryIamProject(Long projectId);

    UserE queryUserById(Long userId);

    Page<OrganizationE> pageByOrganization(int page, int size);

    Page<ProjectE> pageByProject(Long organizationId,int page, int size);
}

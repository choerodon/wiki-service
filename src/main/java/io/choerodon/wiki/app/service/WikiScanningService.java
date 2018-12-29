package io.choerodon.wiki.app.service;

/**
 * Created by Zenger on 2018/7/18.
 */
public interface WikiScanningService {

    void syncOrgAndProject(Long orgId);

    void scanning();

    void updateWikiPage();

    void updateGrpupUsers();

    void syncOrg(Long organizationId);

    void syncProject(Long projectId);
}

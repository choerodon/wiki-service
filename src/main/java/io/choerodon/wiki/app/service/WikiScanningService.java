package io.choerodon.wiki.app.service;

/**
 * Created by Zenger on 2018/7/18.
 */
public interface WikiScanningService {

    void syncOrg(Long orgId);

    Boolean deleteSpaceById(Long id);

    void scanning();
}

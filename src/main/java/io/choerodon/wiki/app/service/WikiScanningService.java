package io.choerodon.wiki.app.service;

/**
 * Created by Zenger on 2018/7/18.
 */
public interface WikiScanningService {

    void syncOrg(Long orgId);

    void scanning();

    void updateWikiPage();
}

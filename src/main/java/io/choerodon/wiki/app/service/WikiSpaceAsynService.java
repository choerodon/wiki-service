package io.choerodon.wiki.app.service;

import io.choerodon.wiki.domain.application.entity.WikiSpaceE;

/**
 * Created by Zenger on 2018/7/5.
 */
public interface WikiSpaceAsynService {

    void createOrgSpace(String orgName, WikiSpaceE orgSpace, String username);

    void createOrgUnderSpace(String param1, String param2, WikiSpaceE orgUnderSpace,String username);

    void createProjectUnderSpace(String param1, String param2, String projectUnderName,
                                 WikiSpaceE projectUnderSpace,String username);
}

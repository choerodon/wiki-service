package io.choerodon.wiki.infra.common;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

public class GetUserNameUtil {

    private GetUserNameUtil() {
    }

    /**
     * 获取登录用户名
     *
     * @return username
     */
    public static String getUsername() {
        CustomUserDetails details = DetailsHelper.getUserDetails();
        return details.getUsername();
    }
}

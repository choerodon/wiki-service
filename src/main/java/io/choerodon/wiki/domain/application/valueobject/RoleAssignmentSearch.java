package io.choerodon.wiki.domain.application.valueobject;

/**
 * Created by Zenger on 2018/7/19.
 */
public class RoleAssignmentSearch {

    private String loginName;

    private String roleName;

    private String realName;

    private String[] param;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }
}

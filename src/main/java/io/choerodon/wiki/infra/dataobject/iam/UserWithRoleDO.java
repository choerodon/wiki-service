package io.choerodon.wiki.infra.dataobject.iam;

import java.util.List;

/**
 * Created by Zenger on 2018/11/12.
 */
public class UserWithRoleDO extends UserDO {

    private List<RoleDO> roles;

    public List<RoleDO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDO> roles) {
        this.roles = roles;
    }
}

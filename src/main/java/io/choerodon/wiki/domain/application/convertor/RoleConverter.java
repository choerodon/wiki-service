package io.choerodon.wiki.domain.application.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.domain.application.entity.iam.RoleE;
import io.choerodon.wiki.infra.dataobject.iam.RoleDO;

/**
 * Created by Zenger on 2018/7/19.
 */
@Component
public class RoleConverter implements ConvertorI<RoleE, RoleDO, Object> {

    @Override
    public RoleE doToEntity(RoleDO dataObject) {
        RoleE roleE = new RoleE();
        BeanUtils.copyProperties(dataObject, roleE);
        return roleE;
    }

    @Override
    public RoleDO entityToDo(RoleE entity) {
        RoleDO roleDO = new RoleDO();
        BeanUtils.copyProperties(entity, roleDO);
        return roleDO;
    }
}

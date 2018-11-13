package io.choerodon.wiki.domain.application.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.domain.application.entity.iam.LabelE;
import io.choerodon.wiki.domain.application.entity.iam.RoleE;
import io.choerodon.wiki.infra.dataobject.iam.LabelDO;
import io.choerodon.wiki.infra.dataobject.iam.RoleDO;

/**
 * Created by Zenger on 2018/7/19.
 */
@Component
public class LabelConverter implements ConvertorI<LabelE, LabelDO, Object> {

    @Override
    public LabelE doToEntity(LabelDO dataObject) {
        LabelE labelE = new LabelE();
        BeanUtils.copyProperties(dataObject, labelE);
        return labelE;
    }

    @Override
    public LabelDO entityToDo(LabelE entity) {
        LabelDO labelDO = new LabelDO();
        BeanUtils.copyProperties(entity, labelDO);
        return labelDO;
    }
}

package io.choerodon.wiki.domain.application.convertor;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO;

/**
 * @author ernst
 */
@Component
public class OrganizationConverter implements ConvertorI<OrganizationE, OrganizationDO, Object> {

    @Override
    public OrganizationE doToEntity(OrganizationDO dataObject) {
        return new OrganizationE(dataObject.getId(), dataObject.getName(), dataObject.getCode());
    }
}

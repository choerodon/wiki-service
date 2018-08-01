package io.choerodon.wiki.domain.application.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.api.dto.WikiUserDTO;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.entity.iam.UserE;
import io.choerodon.wiki.infra.dataobject.iam.UserDO;

/**
 * @author ernst
 * @data 2018/7/10
 */
@Component
public class UserConverter implements ConvertorI<UserE, UserDO, WikiUserDTO> {

    @Override
    public UserE doToEntity(UserDO dataObject) {
        UserE userE = new UserE();
        BeanUtils.copyProperties(dataObject, userE);
        userE.setOrganization(new OrganizationE(dataObject.getOrganizationId()));
        return userE;
    }

}

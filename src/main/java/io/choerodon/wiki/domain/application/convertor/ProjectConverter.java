package io.choerodon.wiki.domain.application.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO;

/**
 * @author ernst
 * @date 2018/3/22
 */
@Component
public class ProjectConverter implements ConvertorI<ProjectE, ProjectDO, Object> {


    @Override
    public ProjectE doToEntity(ProjectDO dataObject) {
        ProjectE projectE = new ProjectE();
        BeanUtils.copyProperties(dataObject, projectE);
        projectE.initOrganizationE(dataObject.getOrganizationId());
        return projectE;
    }
}
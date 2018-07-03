package io.choerodon.wiki.domain.application.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;

/**
 * Created by Zenger on 2018/7/2.
 */
@Component
public class WikiSpaceConvertor implements ConvertorI<WikiSpaceE, WikiSpaceDO, Object> {

    @Override
    public WikiSpaceE doToEntity(WikiSpaceDO dataObject) {
        WikiSpaceE wikiSpaceE = new WikiSpaceE();
        BeanUtils.copyProperties(dataObject,wikiSpaceE);
        return wikiSpaceE;
    }

    @Override
    public WikiSpaceDO entityToDo(WikiSpaceE entity) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        BeanUtils.copyProperties(entity,wikiSpaceDO);
        return wikiSpaceDO;
    }
}

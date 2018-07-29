package io.choerodon.wiki.domain.application.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;

/**
 * Created by Zenger on 2018/7/28.
 */
@Component
public class WikiSpaceListTreeConvertor implements ConvertorI<WikiSpaceE, Object, WikiSpaceListTreeDTO> {
    @Override
    public WikiSpaceListTreeDTO entityToDto(WikiSpaceE entity) {
        WikiSpaceListTreeDTO wikiSpaceListTreeDTO = new WikiSpaceListTreeDTO();
        BeanUtils.copyProperties(entity,wikiSpaceListTreeDTO);
        return wikiSpaceListTreeDTO;
    }
}

package io.choerodon.wiki.domain.application.convertor;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.wiki.api.dto.WikiLogoDTO;
import io.choerodon.wiki.domain.application.entity.WikiLogoE;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import org.springframework.stereotype.Component;

/**
 * create by xingyu 2018/10/29
 */
@Component
public class WikiLogoConvertor implements ConvertorI<WikiLogoE,Object,WikiLogoDTO> {
    @Override
    public WikiLogoE dtoToEntity(WikiLogoDTO dto) {
        WikiLogoE wikiLogoE = new WikiLogoE();
        wikiLogoE.setLogo(dto.getSystemLogo());
        wikiLogoE.setSimpleName(dto.getSystemName());
        wikiLogoE.setFavicon(dto.getFavicon());
        return wikiLogoE;
    }
}

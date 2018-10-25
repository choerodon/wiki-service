package io.choerodon.wiki.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.api.dto.WikiLogoDTO;
import io.choerodon.wiki.app.service.WikiLogoService;
import io.choerodon.wiki.domain.application.entity.WikiLogoE;
import io.choerodon.wiki.domain.service.IWikiLogoService;
import io.choerodon.wiki.infra.common.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class WikiLogoServiceImpl implements WikiLogoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiGroupServiceImpl.class);

    private IWikiLogoService iWikiLogoService;

    public WikiLogoServiceImpl(IWikiLogoService iWikiLogoService) {
        this.iWikiLogoService = iWikiLogoService;
    }

    @Override
    public void updateLogo(WikiLogoDTO wikiLogoDTO, String username) {
        LOGGER.info("update logo", wikiLogoDTO.getLogo());
        WikiLogoE wikiLogoE = new WikiLogoE();
        wikiLogoE.setLogo(wikiLogoDTO.getLogo());
        wikiLogoE.setSimpleName(wikiLogoDTO.getSimpleName());

        String xmlParam = getWikiLogoXml(wikiLogoE);
        iWikiLogoService.updateLogo(username, xmlParam);
    }

    private String getWikiLogoXml(WikiLogoE wikiLogoE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/wikiLogo.xml");
        Map<String, String> params = new HashMap<>(16);
        params.put("{{ LOGO }}", wikiLogoE.getLogo());
        params.put("{{ SIMPLE_NAME }}", wikiLogoE.getSimpleName());
        return FileUtil.replaceReturnString(inputStream, params);
    }
}

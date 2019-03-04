package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.wiki.api.dto.WikiLogoDTO;
import io.choerodon.wiki.app.service.WikiLogoService;
import io.choerodon.wiki.domain.application.entity.WikiLogoE;
import io.choerodon.wiki.domain.service.IWikiLogoService;
import io.choerodon.wiki.infra.common.FileUtil;

/**
 * Created by Zenger on 2018/11/18.
 */
@Service
public class WikiLogoServiceImpl implements WikiLogoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiGroupServiceImpl.class);

    private IWikiLogoService iWikiLogoService;

    public WikiLogoServiceImpl(IWikiLogoService iWikiLogoService) {
        this.iWikiLogoService = iWikiLogoService;
    }

    @Override
    public void updateLogo(WikiLogoDTO wikiLogoDTO, String username) {
        LOGGER.info("update logo");
        WikiLogoE wikiLogoE = ConvertHelper.convert(wikiLogoDTO, WikiLogoE.class);
        String xmlParam = getWikiLogoXml(wikiLogoE);
        iWikiLogoService.updateLogo(username, xmlParam);
    }

    private String getWikiLogoXml(WikiLogoE wikiLogoE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/wikiLogo.xml");
        Map<String, String> params = new HashMap<>(16);
        String logo = wikiLogoE.getLogo() == null ? "" : wikiLogoE.getLogo();
        String simpleName = wikiLogoE.getSimpleName() == null ? "" : wikiLogoE.getSimpleName();
        String facicon = wikiLogoE.getFavicon() == null ? "" : wikiLogoE.getFavicon();
        params.put("{{ LOGO }}", logo);
        params.put("{{ SIMPLE_NAME }}", simpleName);
        params.put("{{ FAVICON }}", facicon);
        return FileUtil.replaceReturnString(inputStream, params);
    }
}

package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.app.service.WikiSpaceAsynService;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.service.IWikiCreatePageService;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.domain.service.IWikiSpaceWebPreferencesService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;
import io.choerodon.wiki.infra.mapper.WikiSpaceMapper;

/**
 * Created by Zenger on 2018/7/5.
 */
@Component
public class WikiSpaceAsynServiceImpl implements WikiSpaceAsynService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiSpaceAsynServiceImpl.class);

    private static final String PAGE = "页面";
    private static final String TYPE = "project";
    private static final String USERNAME = "admin";

    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService;
    private IWikiSpaceWebPreferencesService iWikiSpaceWebPreferencesService;
    private IWikiCreatePageService iWikiCreatePageService;
    private WikiSpaceMapper wikiSpaceMapper;

    public WikiSpaceAsynServiceImpl(IWikiSpaceWebHomeService iWikiSpaceWebHomeService,
                                    IWikiSpaceWebPreferencesService iWikiSpaceWebPreferencesService,
                                    IWikiCreatePageService iWikiCreatePageService,
                                    WikiSpaceMapper wikiSpaceMapper) {
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
        this.iWikiSpaceWebPreferencesService = iWikiSpaceWebPreferencesService;
        this.iWikiCreatePageService = iWikiCreatePageService;
        this.wikiSpaceMapper = wikiSpaceMapper;
    }

    @Override
    public void createOrgSpace(String orgName, WikiSpaceE wikiSpaceE, String username) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace1WebHome(orgName, getWebHome1XmlStr(wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace1WebPreferences(orgName, getWebPreferencesXmlStr(wikiSpaceE), USERNAME);
        int pageCode = iWikiCreatePageService.createPage1Code(orgName, PAGE, getPageXmlStr(), username);
        LOGGER.info("webHomeCode:" + webHomeCode + "  webPreferencesCode:" + webPreferencesCode + "  pageCode: " + pageCode);
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    public void createProjectSpace(String param1, String param2, WikiSpaceE wikiSpaceE, String username, String type) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace2WebHome(param1, param2, getWebHome2XmlStr(param1, wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace2WebPreferences(param1, param2, getWebPreferencesXmlStr(wikiSpaceE), username);
        if (TYPE.equals(type)) {
            int pageCode = iWikiCreatePageService.createPage2Code(param1, param2, PAGE, getPageXmlStr(), username);
            LOGGER.info("pageCode:" + pageCode);
        }
        LOGGER.info("webHomeCode:" + webHomeCode + "  webPreferencesCode:" + webPreferencesCode);
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    @Async
    public void createOrgUnderSpace(String param1, String param2, WikiSpaceE wikiSpaceE, String username, String type) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace2WebHome(param1, param2, getWebHome2XmlStr(param1, wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace2WebPreferences(param1, param2, getWebPreferencesXmlStr(wikiSpaceE), username);
        if (TYPE.equals(type)) {
            int pageCode = iWikiCreatePageService.createPage2Code(param1, param2, PAGE, getPageXmlStr(), username);
            LOGGER.info("pageCode:" + pageCode);
        }
        LOGGER.info("webHomeCode:" + webHomeCode + "  webPreferencesCode:" + webPreferencesCode);
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    @Async
    public void createProjectUnderSpace(String param1, String param2, String projectUnderName, WikiSpaceE wikiSpaceE, String username) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace3WebHome(param1, param2, projectUnderName, getWebHome3XmlStr(param1, param2, wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace3WebPreferences(param1, param2, projectUnderName, getWebPreferencesXmlStr(wikiSpaceE), username);
        LOGGER.info("webHomeCode:" + webHomeCode + "  webPreferencesCode:" + webPreferencesCode);
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    void checkCodeSuccess(int webHomeCode, int webPreferencesCode, WikiSpaceE wikiSpaceE) {
        if ((webHomeCode == 201 || webHomeCode == 202) && (webPreferencesCode == 201 || webPreferencesCode == 202)) {
            WikiSpaceDO wikiSpaceDO = wikiSpaceMapper.selectByPrimaryKey(wikiSpaceE.getId());
            if (wikiSpaceDO != null) {
                wikiSpaceDO.setSynchro(true);
                if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDO) != 1) {
                    throw new CommonException("error.wikispace.update");
                }
            }
        }
    }

    private String getWebHome1XmlStr(WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
        params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
        params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName());
        params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getWebHome2XmlStr(String parent, WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
        params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
        params.put("{{ SPACE_PARENT }}", parent);
        params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName());
        params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getWebHome3XmlStr(String root, String parent, WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome2.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
        params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
        params.put("{{ SPACE_ROOT }}", root);
        params.put("{{ SPACE_PARENT }}", parent);
        params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName());
        params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getWebPreferencesXmlStr(WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webPreferences.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ SPACE_NAME }}", wikiSpaceE.getName());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getPageXmlStr() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/page.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

}

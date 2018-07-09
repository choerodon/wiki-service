package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.app.service.WikiSpaceAsynService;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.service.*;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;
import io.choerodon.wiki.infra.mapper.WikiSpaceMapper;

/**
 * Created by Zenger on 2018/7/5.
 */
@Component
@Async
public class WikiSpaceAsynServiceImpl implements WikiSpaceAsynService {

    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService;
    private IWikiSpaceWebPreferencesService iWikiSpaceWebPreferencesService;
    private IWikiSpaceCodeWebHomeService iWikiSpaceCodeWebHomeService;
    private IWikiSpaceCodeClassService iWikiSpaceCodeClassService;
    private IWikiSpaceCodeSheetService iWikiSpaceCodeSheetService;
    private IWikiSpaceCodeTemplateService iWikiSpaceCodeTemplateService;
    private IWikiSpaceCodeTemplateProvideService iWikiSpaceCodeTemplateProvideService;
    private IWikiSpaceCodeTranslationsService iWikiSpaceCodeTranslationsService;
    private WikiSpaceMapper wikiSpaceMapper;

    public WikiSpaceAsynServiceImpl(IWikiSpaceWebHomeService iWikiSpaceWebHomeService,
                                    IWikiSpaceWebPreferencesService iWikiSpaceWebPreferencesService,
                                    IWikiSpaceCodeWebHomeService iWikiSpaceCodeWebHomeService,
                                    IWikiSpaceCodeClassService iWikiSpaceCodeClassService,
                                    IWikiSpaceCodeSheetService iWikiSpaceCodeSheetService,
                                    IWikiSpaceCodeTemplateService iWikiSpaceCodeTemplateService,
                                    IWikiSpaceCodeTemplateProvideService iWikiSpaceCodeTemplateProvideService,
                                    IWikiSpaceCodeTranslationsService iWikiSpaceCodeTranslationsService,
                                    WikiSpaceMapper wikiSpaceMapper) {
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
        this.iWikiSpaceWebPreferencesService = iWikiSpaceWebPreferencesService;
        this.iWikiSpaceCodeWebHomeService = iWikiSpaceCodeWebHomeService;
        this.iWikiSpaceCodeClassService = iWikiSpaceCodeClassService;
        this.iWikiSpaceCodeSheetService = iWikiSpaceCodeSheetService;
        this.iWikiSpaceCodeTemplateService = iWikiSpaceCodeTemplateService;
        this.iWikiSpaceCodeTemplateProvideService = iWikiSpaceCodeTemplateProvideService;
        this.iWikiSpaceCodeTranslationsService = iWikiSpaceCodeTranslationsService;
        this.wikiSpaceMapper = wikiSpaceMapper;
    }

    @Override
    public void createOrgSpace(String orgName, WikiSpaceE wikiSpaceE) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace1WebHome(orgName, getWebHome1XmlStr(wikiSpaceE));
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace1WebPreferences(orgName, getWebPreferencesXmlStr(wikiSpaceE));
        int pageHomeCode = iWikiSpaceCodeWebHomeService.createSpace1CodeWebHome(orgName, getCodeWebHomeXmlStr());
        int classCode = iWikiSpaceCodeClassService.createSpace1CodeClass(orgName, getClassXmlStr());
        int sheetCode = iWikiSpaceCodeSheetService.createSpace1CodeSheet(orgName, getSheetXmlStr());
        int templateCode = iWikiSpaceCodeTemplateService.createSpace1CodeTemplate(orgName, getTemplateXmlStr());
        int templateProviderCode = iWikiSpaceCodeTemplateProvideService.createSpace1CodeTemplateProvide(orgName, getTemplateProvideXmlStr());
        int translationsCode = iWikiSpaceCodeTranslationsService.createSpace1CodeTranslations(orgName, getTranslationsXmlStr());
        checkCodeSuccess(webHomeCode, webPreferencesCode, pageHomeCode, classCode, sheetCode, templateCode, templateProviderCode, translationsCode, wikiSpaceE);
    }

    @Override
    public void createOrgUnderSpace(String param1, String param2, WikiSpaceE wikiSpaceE) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace2WebHome(param1, param2, getWebHome2XmlStr(param1, wikiSpaceE));
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace2WebPreferences(param1, param2, getWebPreferencesXmlStr(wikiSpaceE));
        int pageHomeCode = iWikiSpaceCodeWebHomeService.createSpace2CodeWebHome(param1, param2, getCodeWebHomeXmlStr());
        int classCode = iWikiSpaceCodeClassService.createSpace2CodeClass(param1, param2, getClassXmlStr());
        int sheetCode = iWikiSpaceCodeSheetService.createSpace2CodeSheet(param1, param2, getSheetXmlStr());
        int templateCode = iWikiSpaceCodeTemplateService.createSpace2CodeTemplate(param1, param2, getTemplateXmlStr());
        int templateProviderCode = iWikiSpaceCodeTemplateProvideService.createSpace2CodeTemplateProvide(param1, param2, getTemplateProvideXmlStr());
        int translationsCode = iWikiSpaceCodeTranslationsService.createSpace2CodeTranslations(param1, param2, getTranslationsXmlStr());
        checkCodeSuccess(webHomeCode, webPreferencesCode, pageHomeCode, classCode, sheetCode, templateCode, templateProviderCode, translationsCode, wikiSpaceE);
    }

    @Override
    public void createProjectUnderSpace(String param1, String param2, String projectUnderName, WikiSpaceE wikiSpaceE) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace3WebHome(param1, param2, projectUnderName, getWebHome3XmlStr(param1, param2, wikiSpaceE));
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace3WebPreferences(param1, param2, projectUnderName, getWebPreferencesXmlStr(wikiSpaceE));
        int pageHomeCode = iWikiSpaceCodeWebHomeService.createSpace3CodeWebHome(param1, param2, projectUnderName, getCodeWebHomeXmlStr());
        int classCode = iWikiSpaceCodeClassService.createSpace3CodeClass(param1, param2, projectUnderName, getClassXmlStr());
        int sheetCode = iWikiSpaceCodeSheetService.createSpace3CodeSheet(param1, param2, projectUnderName, getSheetXmlStr());
        int templateCode = iWikiSpaceCodeTemplateService.createSpace3CodeTemplate(param1, param2, projectUnderName, getTemplateXmlStr());
        int templateProviderCode = iWikiSpaceCodeTemplateProvideService.createSpace3CodeTemplateProvide(param1, param2, projectUnderName, getTemplateProvideXmlStr());
        int translationsCode = iWikiSpaceCodeTranslationsService.createSpace3CodeTranslations(param1, param2, projectUnderName, getTranslationsXmlStr());
        checkCodeSuccess(webHomeCode, webPreferencesCode, pageHomeCode, classCode, sheetCode, templateCode, templateProviderCode, translationsCode, wikiSpaceE);
    }

    void checkCodeSuccess(int webHomeCode, int webPreferencesCode,
                          int pageHomeCode, int classCode, int sheetCode,
                          int templateCode, int templateProviderCode,
                          int translationsCode, WikiSpaceE wikiSpaceE) {
        if (webHomeCode == 201 && webPreferencesCode == 201
                && pageHomeCode == 201 && classCode == 201
                && sheetCode == 201 && templateCode == 201
                && templateProviderCode == 201 && translationsCode == 201) {
            WikiSpaceDO wikiSpaceDO = wikiSpaceMapper.selectByPrimaryKey(wikiSpaceE.getId());
            wikiSpaceDO.setSynchro(true);
            if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDO) != 1) {
                throw new CommonException("error.wikispace.update");
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
        params.put("{{ DESCRIPTION }}", wikiSpaceE.getDescription());
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
        params.put("{{ DESCRIPTION }}", wikiSpaceE.getDescription());
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
        params.put("{{ DESCRIPTION }}", wikiSpaceE.getDescription());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getWebPreferencesXmlStr(WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webPreferences.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ SPACE_NAME }}", wikiSpaceE.getName());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getCodeWebHomeXmlStr() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/codeWebHome.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getClassXmlStr() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/spaceClass.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getSheetXmlStr() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/spaceSheet.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getTemplateXmlStr() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/spaceTemplate.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getTemplateProvideXmlStr() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/spaceTemplateProvider.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getTranslationsXmlStr() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/spaceTranslations.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }
}

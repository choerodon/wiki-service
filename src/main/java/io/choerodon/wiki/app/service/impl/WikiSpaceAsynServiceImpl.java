package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.app.service.WikiSpaceAsynService;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.domain.service.IWikiSpaceWebPreferencesService;
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
    private WikiSpaceMapper wikiSpaceMapper;

    public WikiSpaceAsynServiceImpl(IWikiSpaceWebHomeService iWikiSpaceWebHomeService,
                                    IWikiSpaceWebPreferencesService iWikiSpaceWebPreferencesService,
                                    WikiSpaceMapper wikiSpaceMapper) {
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
        this.iWikiSpaceWebPreferencesService = iWikiSpaceWebPreferencesService;
        this.wikiSpaceMapper = wikiSpaceMapper;
    }

    @Override
    public void createOrgSpace(String orgName, WikiSpaceE wikiSpaceE) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace1WebHome(orgName, getWebHome1XmlStr(wikiSpaceE));
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace1WebPreferences(orgName, getWebPreferencesXmlStr(wikiSpaceE));
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    public void createOrgUnderSpace(String param1, String param2, WikiSpaceE wikiSpaceE) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace2WebHome(param1, param2, getWebHome2XmlStr(param1, wikiSpaceE));
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace2WebPreferences(param1, param2, getWebPreferencesXmlStr(wikiSpaceE));
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    public void createProjectUnderSpace(String param1, String param2, String projectUnderName, WikiSpaceE wikiSpaceE) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace3WebHome(param1, param2, projectUnderName, getWebHome3XmlStr(param1, param2, wikiSpaceE));
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace3WebPreferences(param1, param2, projectUnderName, getWebPreferencesXmlStr(wikiSpaceE));
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    void checkCodeSuccess(int webHomeCode, int webPreferencesCode, WikiSpaceE wikiSpaceE) {
        if ((webHomeCode == 201 || webHomeCode == 202) && (webPreferencesCode == 201 || webPreferencesCode == 202)) {
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
}

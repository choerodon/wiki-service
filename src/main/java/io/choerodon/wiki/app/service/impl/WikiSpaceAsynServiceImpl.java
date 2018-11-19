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
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.domain.service.IWikiSpaceWebPreferencesService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;
import io.choerodon.wiki.infra.mapper.WikiSpaceMapper;

/**
 * Created by Zenger on 2018/7/5.
 */
@Component
public class WikiSpaceAsynServiceImpl implements WikiSpaceAsynService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiSpaceAsynServiceImpl.class);

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
    public void createOrgSpace(String orgName, WikiSpaceE wikiSpaceE, String username) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace1WebHome(wikiSpaceE.getId(), orgName, getWebHome1XmlStr(wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace1WebPreferences(wikiSpaceE.getId(), orgName, getWebPreferencesXmlStr(wikiSpaceE), BaseStage.USERNAME);
        LOGGER.info("create organization space,path: {}, webHomeCode:{}, webPreferencesCode:{}", orgName, webHomeCode, webPreferencesCode);
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    public void createProjectSpace(String param1, String param2, WikiSpaceE wikiSpaceE, String username) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace2WebHome(wikiSpaceE.getId(), param1, param2, getWebHome2XmlStr(param1, wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace2WebPreferences(wikiSpaceE.getId(), param1, param2, getWebPreferencesXmlStr(wikiSpaceE), username);
        LOGGER.info("create project space,path: {}/{}, webHomeCode:{}, webPreferencesCode:{}", param1, param2, webHomeCode, webPreferencesCode);
        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    @Async("org-pro-sync")
    public void createOrgUnderSpace(String param1, String param2, WikiSpaceE wikiSpaceE, String username) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace2WebHome(wikiSpaceE.getId(), param1, param2, getWebHome2XmlStr(param1, wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace2WebPreferences(wikiSpaceE.getId(), param1, param2, getWebPreferencesXmlStr(wikiSpaceE), username);
        LOGGER.info("create space under the organization,path: {}/{}, webHomeCode:{}, webPreferencesCode:{}", param1, param2, webHomeCode, webPreferencesCode);

        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    @Override
    @Async("org-pro-sync")
    public void createProjectUnderSpace(String param1, String param2, String projectUnderName, WikiSpaceE wikiSpaceE, String username) {
        int webHomeCode = iWikiSpaceWebHomeService.createSpace3WebHome(wikiSpaceE.getId(), param1, param2, projectUnderName, getWebHome3XmlStr(param1, param2, wikiSpaceE), username);
        int webPreferencesCode = iWikiSpaceWebPreferencesService.createSpace3WebPreferences(wikiSpaceE.getId(), param1, param2, projectUnderName, getWebPreferencesXmlStr(wikiSpaceE), username);
        LOGGER.info("create space under the project,path: {}/{}/{}, webHomeCode:{}, webPreferencesCode:{}", param1, param2, projectUnderName, webHomeCode, webPreferencesCode);

        checkCodeSuccess(webHomeCode, webPreferencesCode, wikiSpaceE);
    }

    public void checkCodeSuccess(int webHomeCode, int webPreferencesCode, WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = wikiSpaceMapper.selectByPrimaryKey(wikiSpaceE.getId());
        Boolean isSuccess = (webHomeCode == BaseStage.CREATED || webHomeCode == BaseStage.ACCEPTED)
                && (webPreferencesCode == BaseStage.CREATED || webPreferencesCode == BaseStage.ACCEPTED);
        if (isSuccess) {
            if (wikiSpaceDO != null) {
                wikiSpaceDO.setStatus(SpaceStatus.SUCCESS.getSpaceStatus());
                if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDO) != 1) {
                    throw new CommonException("error.wikispace.update");
                }
            }
        } else {
            if (wikiSpaceDO != null) {
                wikiSpaceDO.setStatus(SpaceStatus.FAILED.getSpaceStatus());
                if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDO) != 1) {
                    throw new CommonException("error.wikispace.update");
                }
            }
        }
    }

    private String getWebHome1XmlStr(WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome.xml");
        Map<String, String> params = new HashMap<>(16);
        params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
        params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
        params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName().replace(".", "\\."));
        params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getWebHome2XmlStr(String parent, WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
        Map<String, String> params = new HashMap<>(16);
        params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
        params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
        params.put("{{ SPACE_PARENT }}", parent.replace(".", "\\."));
        params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName().replace(".", "\\."));
        params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getWebHome3XmlStr(String root, String parent, WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome2.xml");
        Map<String, String> params = new HashMap<>(16);
        params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
        params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
        params.put("{{ SPACE_ROOT }}", root.replace(".", "\\."));
        params.put("{{ SPACE_PARENT }}", parent.replace(".", "\\."));
        params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName().replace(".", "\\."));
        params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private String getWebPreferencesXmlStr(WikiSpaceE wikiSpaceE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webPreferences.xml");
        Map<String, String> params = new HashMap<>(16);
        params.put("{{ SPACE_NAME }}", wikiSpaceE.getName());
        return FileUtil.replaceReturnString(inputStream, params);
    }
}

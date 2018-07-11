package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.choerodon.wiki.api.dto.WikiUserDTO;
import io.choerodon.wiki.app.service.WikiUserService;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.FileUtil;

/**
 * Created by Ernst on 2018/7/4.
 */
@Service
public class WikiUserServiceImpl implements WikiUserService {

    private IWikiUserService iWikiUserService;

    public WikiUserServiceImpl(IWikiUserService iWikiUserService) {
        this.iWikiUserService = iWikiUserService;
    }

    @Override
    public Boolean create(WikiUserDTO wikiUserDTO) {
        String path = "";

        WikiUserE wikiUserE = new WikiUserE();
        wikiUserE.setFirstName(wikiUserDTO.getFirstName());
        wikiUserE.setLastName(wikiUserDTO.getLastName());
        wikiUserE.setUserName(wikiUserDTO.getUserName());
        wikiUserE.setPassword(wikiUserDTO.getPassword());
        wikiUserE.setEmail(wikiUserDTO.getEmail());
        return iWikiUserService.createUser(wikiUserE, wikiUserDTO.getUserName(), getXml(wikiUserE));
    }

    @Override
    public Boolean checkUserExsist(String userName) {
        return iWikiUserService.checkDocExsist(userName,userName);
    }

    @Override
    public Boolean deletePage(String pageName, String username) {
        return iWikiUserService.deletePage(pageName, username);
    }

    private String getXml(WikiUserE wikiUserE) {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/user.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ FIRST_NAME }}", wikiUserE.getFirstName());
        params.put("{{ LAST_NAME }}", wikiUserE.getLastName());
        params.put("{{ USER_EMAIL }}", wikiUserE.getEmail());
        return FileUtil.replaceReturnString(inputStream, params);
    }


}

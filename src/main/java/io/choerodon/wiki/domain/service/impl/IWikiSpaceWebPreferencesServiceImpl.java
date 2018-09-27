package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiSpaceWebPreferencesService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Zenger on 2018/7/5.
 */
@Service
public class IWikiSpaceWebPreferencesServiceImpl implements IWikiSpaceWebPreferencesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IWikiSpaceWebPreferencesServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;
    private WikiSpaceRepository wikiSpaceRepository;

    public IWikiSpaceWebPreferencesServiceImpl(WikiClient wikiClient,
                                               WikiSpaceRepository wikiSpaceRepository) {
        this.wikiClient = wikiClient;
        this.wikiSpaceRepository = wikiSpaceRepository;
    }

    @Override
    public int createSpace1WebPreferences(Long id, String param1, String xmlParam, String username) {
        LOGGER.info("create webPreferences,path: {}", param1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("create webPreferences request xml: {}", xmlParam);
        }
        Response<ResponseBody> response;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(BaseStage.APPXML), xmlParam);
            response = wikiClient.createSpace1WebPreferences(username,
                    client, param1, requestBody).execute();
            LOGGER.info("create webPreferences code:{} ", response.code());
        } catch (IOException e) {
            this.updateWikiSpaceStatus(id);
            throw new CommonException("error.webPreferences.create", e);
        }

        return response.code();
    }

    @Override
    public int createSpace2WebPreferences(Long id, String param1, String param2, String xmlParam, String username) {
        LOGGER.info("create webPreferences,path: {}/{}", param1, param2);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("create webPreferences request xml: {}", xmlParam);
        }
        Response<ResponseBody> response;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(BaseStage.APPXML), xmlParam);
            response = wikiClient.createSpace2WebPreferences(username,
                    client, param1, param2, requestBody).execute();
            LOGGER.info("create webPreferences code:{} ", response.code());
        } catch (IOException e) {
            this.updateWikiSpaceStatus(id);
            throw new CommonException("error.webPreferences.create", e);
        }

        return response.code();
    }

    @Override
    public int createSpace3WebPreferences(Long id, String param1, String param2, String param3, String xmlParam, String username) {
        LOGGER.info("create webPreferences,path: {}/{}/{}", param1, param2, param3);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("create webPreferences request xml: {}", xmlParam);
        }
        Response<ResponseBody> response;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(BaseStage.APPXML), xmlParam);
            response = wikiClient.createSpace3WebPreferences(username,
                    client, param1, param2, param3, requestBody).execute();
            LOGGER.info("create webPreferences code:{} ", response.code());
        } catch (IOException e) {
            this.updateWikiSpaceStatus(id);
            throw new CommonException("error.webPreferences.create", e);
        }

        return response.code();
    }

    public void updateWikiSpaceStatus(Long id) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        if (wikiSpaceE != null) {
            wikiSpaceE.setStatus(SpaceStatus.FAILED.getSpaceStatus());
            wikiSpaceRepository.update(wikiSpaceE);
        }
    }
}

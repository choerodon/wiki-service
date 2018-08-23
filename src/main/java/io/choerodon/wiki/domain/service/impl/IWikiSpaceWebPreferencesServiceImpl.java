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
import io.choerodon.wiki.domain.service.IWikiSpaceWebPreferencesService;
import io.choerodon.wiki.infra.common.Stage;
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

    public IWikiSpaceWebPreferencesServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public int createSpace1WebPreferences(String param1, String xmlParam, String username) {
        LOGGER.info("create webPreferences,path: {}", param1);
        Response<ResponseBody> response;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(Stage.APPXML), xmlParam);
            response = wikiClient.createSpace1WebPreferences(username,
                    client, param1, requestBody).execute();
            LOGGER.info("create webPreferences code:{} ", response.code());
        } catch (IOException e) {
            throw new CommonException("error.webPreferences.create", e);
        }

        return response.code();
    }

    @Override
    public int createSpace2WebPreferences(String param1, String param2, String xmlParam, String username) {
        LOGGER.info("create webPreferences,path: {}/{}", param1, param2);
        Response<ResponseBody> response;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(Stage.APPXML), xmlParam);
            response = wikiClient.createSpace2WebPreferences(username,
                    client, param1, param2, requestBody).execute();
            LOGGER.info("create webPreferences code:{} ", response.code());
        } catch (IOException e) {
            throw new CommonException("error.webPreferences.create", e);
        }

        return response.code();
    }

    @Override
    public int createSpace3WebPreferences(String param1, String param2, String param3, String xmlParam, String username) {
        LOGGER.info("create webPreferences,path: {}/{}/{}", param1, param2, param3);
        Response<ResponseBody> response;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(Stage.APPXML), xmlParam);
            response = wikiClient.createSpace3WebPreferences(username,
                    client, param1, param2, param3, requestBody).execute();
            LOGGER.info("create webPreferences code:{} ", response.code());
        } catch (IOException e) {
            throw new CommonException("error.webPreferences.create", e);
        }

        return response.code();
    }
}

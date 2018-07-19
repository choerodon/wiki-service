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

import io.choerodon.wiki.domain.service.IWikiSpaceCodeWebHomeService;
import io.choerodon.wiki.infra.common.Stage;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Zenger on 2018/7/6.
 */
@Service
public class IWikiSpaceCodeWebHomeServiceImpl implements IWikiSpaceCodeWebHomeService {

    private static final Logger logger = LoggerFactory.getLogger(IWikiSpaceCodeWebHomeServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    public IWikiSpaceCodeWebHomeServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public int createSpace1CodeWebHome(String param1, String xmlParam, String username) {
        Response<ResponseBody> response = null;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(Stage.APPXML), xmlParam);
            response = wikiClient.createSpace1CodeWebHome(username,
                    client, param1, requestBody).execute();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return response == null ? 500 : response.code();
    }

    @Override
    public int createSpace2CodeWebHome(String param1, String param2, String xmlParam, String username) {
        Response<ResponseBody> response = null;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(Stage.APPXML), xmlParam);
            response = wikiClient.createSpace2CodeWebHome(username,
                    client, param1, param2, requestBody).execute();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return response == null ? 500 : response.code();
    }

    @Override
    public int createSpace3CodeWebHome(String param1, String param2, String param3, String xmlParam, String username) {
        Response<ResponseBody> response = null;
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(Stage.APPXML), xmlParam);
            response = wikiClient.createSpace3CodeWebHome(username,
                    client, param1, param2, param3, requestBody).execute();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return response == null ? 500 : response.code();
    }
}

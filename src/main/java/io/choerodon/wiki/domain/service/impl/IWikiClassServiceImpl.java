package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;

import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import io.choerodon.wiki.domain.service.IWikiClassService;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Zenger on 2018/7/11.
 */
@Service
public class IWikiClassServiceImpl implements IWikiClassService {

    private static final Logger logger = LoggerFactory.getLogger(IWikiClassServiceImpl.class);
    private static final String CLASSNAME = "XWiki.XWikiGroups";

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    public IWikiClassServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public String getPageClassResource(String pageName, String username) {
        try {
            Response<ResponseBody> response = wikiClient.getPageClassResource(username, client, pageName).execute();
            return response.body().string();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return "";
    }

    @Override
    public void deletePageClass(String username, String name, int objectNumber) {
        try {
            Response<ResponseBody> response = wikiClient.deletePageClass(username, client, name, CLASSNAME, objectNumber).execute();
            logger.info("Delete the status code returned by the page object: " + response.code());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

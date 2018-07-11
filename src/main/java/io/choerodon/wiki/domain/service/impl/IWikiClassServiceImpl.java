package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;

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

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    public IWikiClassServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public void getPageClassResource(String pageName,String username) {
        try {
            Response<String> response = wikiClient.getPageClassResource(username,client, pageName).execute();
            String xmlString = response.body();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

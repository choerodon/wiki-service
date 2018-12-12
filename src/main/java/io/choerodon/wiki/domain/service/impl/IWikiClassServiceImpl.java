package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;

import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.service.IWikiClassService;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Zenger on 2018/7/11.
 */
@Service
public class IWikiClassServiceImpl implements IWikiClassService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IWikiClassServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    public IWikiClassServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public String getPageClassResource(String space, String pageName, String className, String username) {
        LOGGER.info("get page class resource,path:{} and page: {} and class: {}", space, pageName, className);
        try {
            Response<ResponseBody> response = wikiClient.getPageClassResource(username, client, space, pageName, className).execute();
            LOGGER.info("get page class resource code:{} ", response.code());
            return response.body().string();
        } catch (IOException | CommonException e) {
            throw new CommonException("error.page.class.get", e);
        }
    }

    @Override
    public String getProjectPageClassResource(String org, String project, String pageName, String className, String username) {
        LOGGER.info("get page class resource,path:{}/{} and page: {} and class: {}", org, project, pageName, className);
        try {
            Response<ResponseBody> response = wikiClient.getProjectPageClassResource(username,
                    client, org, project, pageName, className).execute();
            LOGGER.info("get page class resource code:{} ", response.code());
            return response.body().string();
        } catch (IOException | CommonException e) {
            throw new CommonException("error.page.class.get", e);
        }
    }

    @Override
    public void deletePageClass(String username, String space, String name, String className, int objectNumber) {
        LOGGER.info("delete page class resource,path:{} and page: {} and class: {}", space, name, className);
        try {
            Response<ResponseBody> response = wikiClient.deletePageClass(username, client, space, name, className, objectNumber).execute();
            LOGGER.info("delete page class resource code:{} ", response.code());
        } catch (IOException e) {
            throw new CommonException("error.page.class.delete", e);
        }
    }

    @Override
    public void deleteProjectPageClass(String username, String org, String project, String name, String className, int objectNumber) {
        LOGGER.info("delete page class resource,path:{}/{} and page: {} and class: {}", org, project, name, className);
        try {
            Response<ResponseBody> response = wikiClient.deleteProjectPageClass(username, client, org, project,
                    name, className, objectNumber).execute();
            LOGGER.info("delete page class resource code:{} ", response.code());
        } catch (IOException e) {
            throw new CommonException("error.page.class.delete", e);
        }
    }
}

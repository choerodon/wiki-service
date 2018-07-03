package io.choerodon.wiki.domain.service.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.service.IWikiSpaceService;
import io.choerodon.wiki.infra.config.RetrofitConfig;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;
import io.choerodon.wiki.infra.feign.WikiClient;
import io.choerodon.wiki.infra.mapper.WikiSpaceMapper;

/**
 * Created by Zenger on 2018/7/3.
 */
@Service
public class IWikiSpaceServiceImpl implements IWikiSpaceService {

    private static final Logger logger = LoggerFactory.getLogger(IWikiSpaceServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;
    private WikiSpaceMapper wikiSpaceMapper;

    public IWikiSpaceServiceImpl(WikiClient wikiClient,
                                 WikiSpaceMapper wikiSpaceMapper) {
        this.wikiClient = wikiClient;
        this.wikiSpaceMapper = wikiSpaceMapper;
    }

    @Override
    public void createSpace1(WikiSpaceE wikiSpaceE, String param1, String xmlParam) {
        try {
            Call<Object> call = wikiClient.createSpace1WebHome(
                    client, param1, new ByteArrayInputStream(xmlParam.getBytes("UTF-8")));
            asyn(call, wikiSpaceE);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    private void asyn(Call<Object> call, WikiSpaceE wikiSpaceE) {
        //异步方法
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.raw().code() == 201) {
                    wikiSpaceE.setSynchro(true);
                    if (wikiSpaceMapper.updateByPrimaryKey(ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class)) != 1) {
                        throw new CommonException("error.wikispace.update");
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable throwable) {
                logger.error("request wiki is error: ", throwable.getMessage());
            }
        });
    }
}

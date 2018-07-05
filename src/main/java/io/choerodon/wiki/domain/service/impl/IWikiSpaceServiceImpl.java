package io.choerodon.wiki.domain.service.impl;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.service.IWikiSpaceService;
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
    public void createSpace1(Long id, String param1, String xmlParam) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/xml"),xmlParam);
        Call<ResponseBody> call = wikiClient.createSpace1WebHome(
                client, param1, requestBody);
        asyn(call, id);
    }

    @Override
    public void createSpace2(Long id, String param1, String param2, String xmlParam) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/xml"),xmlParam);
        Call<ResponseBody> call = wikiClient.createSpace2WebHome(
                client, param1,param2, requestBody);
        asyn(call, id);
    }

    private void asyn(Call<ResponseBody> call, Long id) {
        //异步方法
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 201) {
                    WikiSpaceDO wikiSpaceDO = wikiSpaceMapper.selectByPrimaryKey(id);
                    wikiSpaceDO.setSynchro(true);
                    if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDO) != 1) {
                        throw new CommonException("error.wikispace.update");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                logger.error("request wiki is error: ", throwable.getMessage());
            }
        });
    }
}

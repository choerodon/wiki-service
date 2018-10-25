package io.choerodon.wiki.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.application.entity.WikiLogoE;
import io.choerodon.wiki.domain.service.IWikiLogoService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.exception.NetworkRequestStatusCodeException;
import io.choerodon.wiki.infra.feign.WikiClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * create xingyu 2018/10/25
 */
@Service
public class IWikiLogoServiceImpl implements IWikiLogoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IWikiLogoServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    public IWikiLogoServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public Boolean updateLogo(String username,String xmlParam) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(BaseStage.APPXML), xmlParam);
            String spaceName = "ChoerodonConfig";
            String pageName = "ChoerodonTheme";
            Integer objectNumber = 0;
            String className = "FlamingoThemesCode.ThemeClass";
            Call<ResponseBody> call = wikiClient.updateObject(username, client, spaceName, pageName,
                    className, objectNumber, requestBody);
            Response response = call.execute();
            if ( response.code() == BaseStage.ACCEPTED) {
                return true;
            } else {
                throw new NetworkRequestStatusCodeException("error update logo return status code: " + response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.update.logo", e);
        }
    }


}

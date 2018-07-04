package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Zenger on 2018/7/3.
 */
@Service
public class IWikiUserServiceImpl implements IWikiUserService {

    private static final Logger logger = LoggerFactory.getLogger(IWikiUserServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    public IWikiUserServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public Boolean createUser(WikiUserE wikiUserE, String param1, String xmlParam) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/xml"), xmlParam);
            Call<ResponseBody> call = wikiClient.createUser(
                    client, param1, requestBody);
            Response response = call.execute();
            if(response.code()==201 || response.code()==202){
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean checkUserExsist(String userName) {
        Call<ResponseBody> call = wikiClient.checkUserExsist(
                client, userName);
        try {
            Response response = call.execute();
            if(response.code()==200){
                return true;
            }else if(response.code()==404){
                return false;
            }else {
                throw new CommonException("获取用户失败");
            }
        }catch (IOException e){
            logger.error(e.getMessage());
            throw new CommonException("获取用户失败");
        }
    }

}

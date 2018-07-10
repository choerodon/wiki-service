package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Ernst on 2018/7/6.
 */
@Service
public class IWikiGroupServiceImpl implements IWikiGroupService {

    private static final Logger logger = LoggerFactory.getLogger(IWikiGroupServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    public IWikiGroupServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public Boolean createGroup(String groupName, String xmlParam) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/xml"), xmlParam);
            Call<ResponseBody> call = wikiClient.createGroup(
                    client, groupName, requestBody);
            Response response = call.execute();
            if (response.code() == 201 || response.code() == 202) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean createGroupUsers(String groupName, String userName) {
        try{
            FormBody body = new FormBody.Builder().add("className","XWiki.XWikiGroups").add("property#member","XWiki."+userName).build();
            Call<ResponseBody> call = wikiClient.createGroupUsers(client,groupName,body);
            Response response = call.execute();
            if(response.code() == 201){
                return true;
            }
            return false;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

}

package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;
import java.util.List;

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

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.exception.NetworkRequestStatusCodeException;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Ernst on 2018/7/5.
 */
@Service
public class IWikiUserServiceImpl implements IWikiUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IWikiUserServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    @Value("${wiki.default-group}")
    private String defaultGroup;

    private WikiClient wikiClient;

    public IWikiUserServiceImpl(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    @Override
    public Boolean createUser(String loginName, String xmlParam, String username) {
        LOGGER.info("create wiki user: {}", loginName);
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(BaseStage.APPXML), xmlParam);
            Call<ResponseBody> call = wikiClient.createUser(username,
                    client, loginName, requestBody);
            Response response = call.execute();
            LOGGER.info("create wiki user code:{} ", response.code());
            if (response.code() == BaseStage.CREATED || response.code() == BaseStage.ACCEPTED) {
                return addUserToDefaultGroup(loginName, username);
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new CommonException("error.user.create,connection wiki timeout", e);
        }
    }

    @Override
    public Boolean checkDocExsist(String username, String param1) {
        LOGGER.info("Check if the page exists in the wiki: {}", param1);
        Call<ResponseBody> call = wikiClient.checkDocExsist(username,
                client, param1);
        try {
            Response response = call.execute();
            LOGGER.info("Check if the page exists in the wiki return code: {}", response.code());
            if (response.code() == BaseStage.OK) {
                return true;
            } else if (response.code() == BaseStage.NOT_FOUND) {
                return false;
            } else {
                throw new NetworkRequestStatusCodeException("Check that the page has returned error code in the wiki: " + response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.wiki.page.check,connection wiki timeout", e);
        }
    }

    @Override
    public Boolean deletePage(String pageName, String username) {
        LOGGER.info("delete page: {}", pageName);
        Call<ResponseBody> call = wikiClient.deletePage(username,
                client, BaseStage.SPACE, pageName);
        try {
            Response response = call.execute();
            LOGGER.info("delete page code: {}", response.code());
            if (response.code() == BaseStage.NO_CONTENT) {
                return true;
            } else if (response.code() == BaseStage.NOT_FOUND) {
                throw new NetworkRequestStatusCodeException("error get page return code: " + response.code());
            } else {
                throw new NetworkRequestStatusCodeException("error delete page return code: " + response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.delete.page,connection wiki timeout", e);
        }
    }

    @Override
    public Boolean createWikiUserToGroup(List<WikiUserE> wikiUserEList, String username) {
        LOGGER.info("create user data: {}", wikiUserEList.toString());
        Call<ResponseBody> call = wikiClient.createWikiUserToGroup(username,
                wikiUserEList);
        try {
            Response response = call.execute();
            LOGGER.info("create user to group return code: {}", response.code());
            if (response.code() == BaseStage.NO_CONTENT) {
                return true;
            } else {
                throw new NetworkRequestStatusCodeException("error create user to group return code: " + response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.create.user.to.group,connection wiki timeout", e);
        }
    }

    private Boolean addUserToDefaultGroup(String loginName, String username) throws IOException {
        try {
            LOGGER.info("add user:{} to default group", loginName);
            FormBody body = new FormBody.Builder().add("className", "XWiki.XWikiGroups").add("property#member", "XWiki." + loginName.replace(".", "\\.")).build();
            Call<ResponseBody> addGroupCall = wikiClient.createGroupUsers(username, client, defaultGroup, body);
            Response addGroupResponse = addGroupCall.execute();
            LOGGER.info("add user:{} to default group return code:{}", loginName, addGroupResponse.code());
            return addGroupResponse.code() == BaseStage.CREATED || addGroupResponse.code() == BaseStage.ACCEPTED;
        } catch (IOException e) {
            throw new CommonException("error.delete.page,connection wiki timeout", e);
        }
    }
}

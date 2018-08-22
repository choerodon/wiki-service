package io.choerodon.wiki.domain.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.Stage;
import io.choerodon.wiki.infra.feign.WikiClient;

/**
 * Created by Ernst on 2018/7/6.
 */
@Service
public class IWikiGroupServiceImpl implements IWikiGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IWikiGroupServiceImpl.class);

    @Value("${wiki.client}")
    private String client;

    private WikiClient wikiClient;

    private IWikiUserServiceImpl iWikiUserService;

    public IWikiGroupServiceImpl(WikiClient wikiClient, IWikiUserServiceImpl iWikiUserService) {
        this.wikiClient = wikiClient;
        this.iWikiUserService = iWikiUserService;
    }

    @Override
    public Boolean createGroup(String groupName, String username) {
        LOGGER.info("create group: {}", groupName);
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(Stage.APPXML), getGroupXml());
            Call<ResponseBody> call = wikiClient.createGroup(username,
                    client, groupName, requestBody);
            Response response = call.execute();
            LOGGER.info("create group code:{} ", response.code());
            if (response.code() == 201 || response.code() == 202) {
                return true;
            } else {
                throw new CommonException("error.create.group", response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.create.group", e);
        }
    }

    @Override
    public Boolean createGroupUsers(String groupName, String loginName, String username) {
        LOGGER.info("user add to group,user: {} add group: {}", loginName, groupName);
        try {
            //如果组不存在则新建组
            Boolean flag = iWikiUserService.checkDocExsist(username, groupName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Does the group exist? " + flag);
            }
            if (!flag) {
                this.createGroup(groupName, username);
            }

            FormBody body = new FormBody.Builder().add("className", "XWiki.XWikiGroups").add("property#member", "XWiki." + loginName).build();
            Call<ResponseBody> call = wikiClient.createGroupUsers(username, client, groupName, body);
            Response response = call.execute();
            LOGGER.info("create the code returned by the group user:{}", response.code());
            if (response.code() == 201) {
                return true;
            } else {
                throw new CommonException("error.create.group.user", response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.create.group.user", e);
        }
    }

    @Override
    public Boolean disableOrgGroupView(String organizationCode, String organizationName, String username) {
        try {
            String groupName = "O-" + organizationCode + Stage.USER_GROUP;
            LOGGER.info("disable organization group view,groupName:{}", groupName);
            Boolean falg = iWikiUserService.checkDocExsist(username, groupName);
            if (!falg) {
                throw new CommonException(Stage.ERROR_QUERY_GROUP);
            }

            Call<ResponseBody> call = wikiClient.offerRightToOrgGroupView(username, client,
                    "O-" + organizationName, getBody(groupName, "0", "view"));
            Response response = call.execute();
            LOGGER.info("disable organization group view code: {}", response.code());
            if (response.code() == 201) {
                return true;
            } else {
                throw new CommonException("error.organization.disable.group.view", response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.organization.disable.group.view", e);
        }
    }

    @Override
    public Boolean disableProjectGroupView(String projectName, String projectCode, String organizationName, String organizationCode, String username) {
        try {
            String groupName = "";
            if (iWikiUserService.checkDocExsist(username, "P-" + organizationCode + "-" + projectCode + Stage.USER_GROUP)) {
                groupName = "P-" + organizationCode + "-" + projectCode + Stage.USER_GROUP;
            } else if (iWikiUserService.checkDocExsist(username, "P-" + projectCode + Stage.USER_GROUP)) {
                groupName = "P-" + projectCode + Stage.USER_GROUP;
            }
            LOGGER.info("disable project group view,groupName:{}", groupName);
            if (!StringUtils.isEmpty(groupName)) {
                Call<ResponseBody> call = wikiClient.offerRightToProjectGroupView(username, client,
                        "O-" + organizationName, "P-" + projectName, getBody(groupName, "0", "view"));
                Response response = call.execute();
                LOGGER.info("disable project group view code: {}", response.code());
                if (response.code() == 201) {
                    return true;
                } else {
                    throw new CommonException("error.project.disable.group.view", response.code());
                }
            }
        } catch (IOException e) {
            throw new CommonException("error.project.disable.group.view", e);
        }

        return false;
    }

    @Override
    public Boolean addRightsToOrg(WikiGroupDTO wikiGroupDTO, List<String> rights, Boolean isAdmin, String username) {
        try {
            String groupName = "O-" + wikiGroupDTO.getOrganizationCode() + (isAdmin ? Stage.ADMIN_GROUP : Stage.USER_GROUP);
            LOGGER.info("{} assignment permission", groupName);
            Boolean falg = iWikiUserService.checkDocExsist(username, groupName);
            if (!falg) {
                throw new CommonException(Stage.ERROR_QUERY_GROUP);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String right : rights) {
                stringBuilder.append(right);
                stringBuilder.append(",");
            }
            String levels = stringBuilder.toString();
            levels = levels.substring(0, levels.length() - 1);

            String encodeStr = "O-" + wikiGroupDTO.getOrganizationName();
            URLEncoder.encode(encodeStr, "UTF-8");
            Call<ResponseBody> call = wikiClient.offerRightToOrgGroupView(username, client, encodeStr, getBody(groupName, "1", levels));
            Response response = call.execute();
            LOGGER.info("{} assignment permission return code: {}",groupName, response.code());
            if (response.code() == 201) {
                return true;
            } else {
                throw new CommonException("error.organization.add.rights", response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.organization.add.rights", e);
        }
    }

    @Override
    public Boolean addRightsToProject(WikiGroupDTO wikiGroupDTO, List<String> rights, Boolean isAdmin, String username) {
        try {
            String groupName = "P-" + wikiGroupDTO.getOrganizationCode() + "-" + wikiGroupDTO.getProjectCode() + (isAdmin ? Stage.ADMIN_GROUP : Stage.USER_GROUP);
            LOGGER.info("{} assignment permission", groupName);
            Boolean falg = iWikiUserService.checkDocExsist(username, groupName);
            if (!falg) {
                throw new CommonException(Stage.ERROR_QUERY_GROUP);
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String right : rights) {
                stringBuilder.append(right);
                stringBuilder.append(",");
            }
            String levels = stringBuilder.toString();
            levels = levels.substring(0, levels.length() - 1);

            Call<ResponseBody> call = wikiClient.offerRightToProjectGroupView(username, client, "O-" + wikiGroupDTO.getOrganizationName(),
                    "P-" + wikiGroupDTO.getProjectName(), getBody(groupName, "1", levels));
            Response response = call.execute();
            LOGGER.info("{} assignment permission return code: {}",groupName, response.code());
            if (response.code() == 201) {
                return true;
            } else {
                throw new CommonException("error.project.add.rights", response.code());
            }
        } catch (IOException e) {
            throw new CommonException("error.project.add.rights", e);
        }
    }

    private String getGroupXml() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/group.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }

    private FormBody getBody(String groupName, String allow, String levels) {
        return new FormBody.Builder().add("className", "XWiki.XWikiGlobalRights").add("property#allow", allow)
                .add("property#groups", "XWiki." + groupName).add("property#levels", levels).build();
    }
}

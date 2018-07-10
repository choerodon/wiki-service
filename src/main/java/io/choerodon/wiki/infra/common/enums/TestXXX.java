package io.choerodon.wiki.infra.common.enums;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.*;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TestXXX {
    public static void main(String[] args) {



        FormBody body = new FormBody.Builder().add("className","XWiki.XWikiGroups").add("property#member","XWiki.lkllklkl").build();
        String url = "http://xwiki-alpha.saas.hand-china.com/rest/wikis/xwiki/spaces/XWiki/pages/XWikiAdminGroup/objects";

        String result = postFormBody(url, body);
        System.out.println(result);

    }

    private  static final OkHttpClient client = new OkHttpClient.Builder().
            connectionPool(new ConnectionPool(100,10, TimeUnit.MINUTES))
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS).build();


    public static String postFormBody(String url, FormBody body){
        Request request = new Request.Builder().url(url).header("Authorization","Basic YWRtaW46aGFuZGhhbmQ=")
                .post(body).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

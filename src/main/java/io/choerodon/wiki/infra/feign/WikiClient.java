package io.choerodon.wiki.infra.feign;

import java.io.InputStream;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by Zenger on 2018/7/3.
 */
public interface WikiClient {

    @GET( "/rest/wikis/xwiki/spaces/testCreat/pages/WebHome?objects=true")
    Call<ResponseBody> getWebHome();

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/pages/WebHome?objects=true")
    Call<ResponseBody> createSpace1WebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/pages/WebHome?objects=true")
    Call<ResponseBody> createSpace2WebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/pages/WebHome?objects=true")
    Call<ResponseBody> createSpace3WebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/XWiki/pages/{param1}?objects=true")
    Call<ResponseBody> createUser(
            @Path("client") String client,
            @Path("param1") String param1,
            @Body RequestBody xmlParam);

    @GET("/rest/wikis/{client}/spaces/XWiki/pages/{param1}?objects=true")
    Call<ResponseBody> checkUserExsist(
            @Path("client") String client,
            @Path("param1") String param1);

}

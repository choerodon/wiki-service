package io.choerodon.wiki.infra.feign;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Zenger on 2018/7/3.
 */
public interface WikiClient {

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/pages/WebHome?objects=true")
    Call<Object> createSpace1WebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Body InputStream xmlStream);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/pages/WebHome?objects=true")
    Call<Object> createSpace2WebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body String xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/pages/WebHome?objects=true")
    Call<Object> createSpace3WebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body String xmlParam);
}

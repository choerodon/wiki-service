package io.choerodon.wiki.infra.feign;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by Zenger on 2018/7/3.
 */
public interface WikiClient {

    //webHome
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

    //WebPreferences
    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/pages/WebPreferences?objects=true")
    Call<ResponseBody> createSpace1WebPreferences(
            @Path("client") String client,
            @Path("param1") String param1,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/pages/WebPreferences?objects=true")
    Call<ResponseBody> createSpace2WebPreferences(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/pages/WebPreferences?objects=true")
    Call<ResponseBody> createSpace3WebPreferences(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);


    //codeWebHome
    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/Code/pages/WebHome?objects=true")
    Call<ResponseBody> createSpace1CodeWebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/Code/pages/WebHome?objects=true")
    Call<ResponseBody> createSpace2CodeWebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/spaces/Code/pages/WebHome?objects=true")
    Call<ResponseBody> createSpace3CodeWebHome(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);

    //spaceClass
    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/Code/pages/{param2}Class?objects=true")
    Call<ResponseBody> createSpace1Class(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/Code/pages/{param3}Class?objects=true")
    Call<ResponseBody> createSpace2Class(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/spaces/Code/pages/{param4}Class?objects=true")
    Call<ResponseBody> createSpace3Class(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Path("param4") String param4,
            @Body RequestBody xmlParam);

    //sheet
    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/Code/pages/{param2}Sheet?objects=true")
    Call<ResponseBody> createSpace1Sheet(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/Code/pages/{param3}Sheet?objects=true")
    Call<ResponseBody> createSpace2Sheet(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/spaces/Code/pages/{param4}Sheet?objects=true")
    Call<ResponseBody> createSpace3Sheet(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Path("param4") String param4,
            @Body RequestBody xmlParam);

    //template
    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/Code/pages/{param2}Template?objects=true")
    Call<ResponseBody> createSpace1Template(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/Code/pages/{param3}Template?objects=true")
    Call<ResponseBody> createSpace2Template(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/spaces/Code/pages/{param4}Template?objects=true")
    Call<ResponseBody> createSpace3Template(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Path("param4") String param4,
            @Body RequestBody xmlParam);

    //templateProvider
    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/Code/pages/{param2}TemplateProvider?objects=true")
    Call<ResponseBody> createSpace1TemplateProvider(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/Code/pages/{param3}TemplateProvider?objects=true")
    Call<ResponseBody> createSpace2TemplateProvider(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/spaces/Code/pages/{param4}TemplateProvider?objects=true")
    Call<ResponseBody> createSpace3TemplateProvider(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Path("param4") String param4,
            @Body RequestBody xmlParam);

    //translations
    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/Code/pages/{param2}Translations?objects=true")
    Call<ResponseBody> createSpace1Translations(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/Code/pages/{param3}Translations?objects=true")
    Call<ResponseBody> createSpace2Translations(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Body RequestBody xmlParam);

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/{param1}/spaces/{param2}/spaces/{param3}/spaces/Code/pages/{param4}Translations?objects=true")
    Call<ResponseBody> createSpace3Translations(
            @Path("client") String client,
            @Path("param1") String param1,
            @Path("param2") String param2,
            @Path("param3") String param3,
            @Path("param4") String param4,
            @Body RequestBody xmlParam);

    //user
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

    @Headers({"Content-Type:application/xml;charset=UTF-8"})
    @PUT("/rest/wikis/{client}/spaces/XWiki/pages/{groupName}?objects=true")
    Call<ResponseBody> createGroup(
            @Path("client") String client,
            @Path("groupName") String groupName,
            @Body RequestBody xmlParam);
}

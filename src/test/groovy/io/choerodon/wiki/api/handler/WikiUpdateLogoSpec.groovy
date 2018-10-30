package io.choerodon.wiki.api.handler

import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.api.dto.WikiLogoDTO
import io.choerodon.wiki.api.eventhandler.WikiEventHandler
import io.choerodon.wiki.infra.feign.WikiClient
import okhttp3.Headers
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.internal.http.RealResponseBody
import okio.BufferedSource
import okio.Okio
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Create by xingyu 2018/10/25
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class WikiUpdateLogoSpec extends Specification {

    @Autowired
    private WikiEventHandler wikiEventHandler

    WikiClient wikiClient;

    void setup() {
        wikiClient = Mock(WikiClient)
    }

    def '修改wiki logo'(){
        given: '定义请求数据格式'
        def data = "{\n" +
                "\"logo\": \"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_86d58ae1.png\",\n" +
                "\"simpleName\": \"org-demo\" \n" +
                "}";
        WikiLogoDTO wikiLogoDTO = new WikiLogoDTO();
        wikiLogoDTO.setLogo("http://iam.choerodon.staging.saas.hand-china.com/assets/choerodon_logo_picture.52a5292b.svg")
        wikiLogoDTO.setSimpleName("xingyu")

        and: 'Mock'
        1 * wikiClient.updateObject(_,_) >> getCall(202)

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleLogoUpdateEvent(data)

        then: '数据校验'
        Assert.assertEquals(data, entity)
    }

    Call<ResponseBody> getCall(int code) {
        return new Call<ResponseBody>() {
            @Override
            Response<ResponseBody> execute() throws IOException {
                okhttp3.Response.Builder builder = new okhttp3.Response.Builder()
                builder.code(code)
                builder.message("haha")
                okhttp3.Response rawResponse = new okhttp3.Response(builder)

                BufferedSource buffer = null
                def source = Okio.source(this.class.getResourceAsStream("/xml/webhome.xml"));
                buffer = Okio.buffer(source);

                RealResponseBody realResponseBody = new RealResponseBody(new Headers(), buffer)

                Response<ResponseBody> response = new Response<>(rawResponse, realResponseBody, null);

                return response;
            }

            @Override
            void enqueue(Callback<ResponseBody> callback) {

            }

            @Override
            boolean isExecuted() {
                return false
            }

            @Override
            void cancel() {

            }

            @Override
            boolean isCanceled() {
                return false
            }

            @Override
            Call<ResponseBody> clone() {
                return null
            }

            @Override
            Request request() {
                return null
            }
        }
    }
}

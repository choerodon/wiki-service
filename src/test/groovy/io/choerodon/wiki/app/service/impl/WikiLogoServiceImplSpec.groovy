package io.choerodon.wiki.app.service.impl

import io.choerodon.wiki.api.dto.WikiLogoDTO
import io.choerodon.wiki.app.service.WikiLogoService
import io.choerodon.wiki.domain.service.IWikiLogoService
import io.choerodon.wiki.domain.service.impl.IWikiClassServiceImpl
import io.choerodon.wiki.domain.service.impl.IWikiLogoServiceImpl
import io.choerodon.wiki.infra.feign.WikiClient
import okhttp3.Headers
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.internal.http.RealResponseBody
import okio.BufferedSource
import okio.Okio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import spock.lang.Specification

class WikiLogoServiceImplSpec extends Specification {

    WikiClient wikiClient;
    IWikiLogoServiceImpl iWikiLogoService;
    WikiLogoServiceImpl wikiLogoService;

    void setup() {
        wikiClient = Mock(WikiClient)
        iWikiLogoService = new IWikiLogoServiceImpl(wikiClient)
        wikiLogoService = new WikiLogoServiceImpl(iWikiLogoService)
    }

    def 'updateLogo'() {
        given: '自定义数据'
        WikiLogoDTO wikiLogoDTO = new WikiLogoDTO()
        wikiLogoDTO.setLogo("http://iam.choerodon.staging.saas.hand-china.com/assets/choerodon_logo_picture.52a5292b.svg");
        wikiLogoDTO.setSimpleName("xingyu");

        when: ''
        wikiLogoService.updateLogo(wikiLogoDTO, "testName");

        then: ''
        1 * wikiClient.updateObject(*_) >> getCall(202)
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

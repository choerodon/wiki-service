package io.choerodon.wiki.domain.service.impl

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

/**
 * Created by Zenger on 2018/7/25.
 */
class IWikiClassServiceImplSpec extends Specification {

    WikiClient wikiClient;
    IWikiClassServiceImpl service

    void setup() {
        wikiClient = Mock(WikiClient)
        service = new IWikiClassServiceImpl(wikiClient)
    }

    def 'getPageClassResource'() {
        when: ''
        service.getPageClassResource("1", "2", "3", "4")

        then: ''
        1 * wikiClient.getPageClassResource(*_) >> getCall(200)
    }

    def 'getProjectPageClassResource'() {
        when: ''
        service.getProjectPageClassResource("1", "2", "3", "4", "5")

        then: ''
        1 * wikiClient.getProjectPageClassResource(*_) >> getCall(200)
    }

    def 'deletePageClass'() {
        when: ''
        service.deletePageClass("1", "2", "3", "4", 5)

        then: ''
        1 * wikiClient.deletePageClass(*_) >> getCall(200)
    }

    def 'deleteProjectPageClass'() {
        when: ''
        service.deleteProjectPageClass("1", "2", "3", "4", "5", 6)

        then: ''
        1 * wikiClient.deleteProjectPageClass(*_) >> getCall(200)
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

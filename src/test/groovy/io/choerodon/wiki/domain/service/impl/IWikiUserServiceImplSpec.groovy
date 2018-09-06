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
class IWikiUserServiceImplSpec extends Specification {

    WikiClient wikiClient
    IWikiUserServiceImpl service

    void setup() {
        wikiClient = Mock(WikiClient)
        service = new IWikiUserServiceImpl(wikiClient)
    }

    def 'createUser'() {
        when: ''
        service.createUser("1", "2", "3")

        then: ''
        1 * wikiClient.createUser(*_) >> getCall(201)
        1 * wikiClient.createGroupUsers(*_) >> getCall(201)
    }

    def 'checkDocExsist'() {
        when: ''
        service.checkDocExsist("1", "2")

        then: ''
        1 * wikiClient.checkDocExsist(*_) >> getCall(200)
    }

    def 'deletePage'() {
        when: ''
        service.deletePage("1", "2")

        then: ''
        1 * wikiClient.deletePage(*_) >> getCall(204)
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

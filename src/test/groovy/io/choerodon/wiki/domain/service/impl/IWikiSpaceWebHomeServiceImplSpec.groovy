package io.choerodon.wiki.domain.service.impl

import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository
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
class IWikiSpaceWebHomeServiceImplSpec extends Specification {

    WikiClient wikiClient
    WikiSpaceRepository wikiSpaceRepository
    IWikiSpaceWebHomeServiceImpl service

    void setup() {
        wikiClient = Mock(WikiClient)
        wikiSpaceRepository = Mock(WikiSpaceRepository)
        service = new IWikiSpaceWebHomeServiceImpl(wikiClient,wikiSpaceRepository)
    }

    def 'createSpace1WebHome'() {
        when: ''
        service.createSpace1WebHome(1, "2", "3","4")

        then: ''
        1 * wikiClient.createSpace1WebHome(*_) >> getCall(201)
    }

    def 'createSpace2WebHome'() {
        when: ''
        service.createSpace2WebHome(1, "2", "3", "4","5")

        then: ''
        1 * wikiClient.createSpace2WebHome(*_) >> getCall(201)
    }

    def 'createSpace3WebHome'() {
        when: ''
        service.createSpace3WebHome(1, "2", "3", "4", "5","6")

        then: ''
        1 * wikiClient.createSpace3WebHome(*_) >> getCall(201)
    }

    def 'deletePage'() {
        when: ''
        service.deletePage(1, "2", "3","4")

        then: ''
        1 * wikiClient.deletePage(*_) >> getCall(201)
    }

    def 'deletePage1'() {
        when: ''
        service.deletePage1(1, "2", "3", "4","5")

        then: ''
        1 * wikiClient.deletePage1(*_) >> getCall(201)
    }

    def 'deletePage2'() {
        when: ''
        service.deletePage2(1, "2", "3", "4", "5","6")

        then: ''
        1 * wikiClient.deletePage2(*_) >> getCall(201)
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

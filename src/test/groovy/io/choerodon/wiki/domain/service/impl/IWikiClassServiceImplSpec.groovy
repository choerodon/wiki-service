//package io.choerodon.wiki.domain.service.impl
//
//import io.choerodon.wiki.infra.feign.WikiClient
//import okhttp3.MediaType
//import okhttp3.Request
//import okhttp3.ResponseBody
//import okio.BufferedSource
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import spock.lang.Specification
//
///**
// * Created by Zenger on 2018/7/25.
// */
//class IWikiClassServiceImplSpec extends Specification {
//
//    WikiClient wikiClient;
//    IWikiClassServiceImpl service
//    void setup(){
//        wikiClient=Mock(WikiClient)
//        service = new IWikiClassServiceImpl(wikiClient)
//    }
//
//    def 'getPageClassResource'() {
//        given:''
//        Call<ResponseBody> call = new Call<ResponseBody>() {
//            @Override
//            Response<ResponseBody> execute() throws IOException {
//                return null;
//            }
//
//            @Override
//            void enqueue(Callback<ResponseBody> callback) {
//
//            }
//
//            @Override
//            boolean isExecuted() {
//                return false
//            }
//
//            @Override
//            void cancel() {
//
//            }
//
//            @Override
//            boolean isCanceled() {
//                return false
//            }
//
//            @Override
//            Call<ResponseBody> clone() {
//                return null
//            }
//
//            @Override
//            Request request() {
//                return null
//            }
//        }
//
//        when:''
//        service.getPageClassResource("1","2","3","4")
//
//        then:''
//        1 * wikiClient.getPageClassResource(*_) >> call
//    }
//}

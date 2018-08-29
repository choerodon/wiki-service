package io.choerodon.wiki.domain.service.impl

import io.choerodon.wiki.api.dto.WikiGroupDTO
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
class IWikiGroupServiceImplSpec extends Specification {

    WikiClient wikiClient
    IWikiUserServiceImpl iWikiUserService
    IWikiGroupServiceImpl service

    void setup(){
        wikiClient=Mock(WikiClient)
        iWikiUserService=Mock(IWikiUserServiceImpl)
        service = new IWikiGroupServiceImpl(wikiClient,iWikiUserService)
    }

    def 'createGroup'() {
        when:''
        service.createGroup("1","2")

        then:''
        1 * wikiClient.createGroup(*_) >>  getCall(201)
    }

    def 'createGroupUsers'() {
        when:''
        service.createGroupUsers("1","2","3")

        then:''
        1 * iWikiUserService.checkDocExsist(*_) >> false
        1 * wikiClient.createGroupUsers(*_) >>  getCall(201)
        1 * wikiClient.createGroup(*_) >>  getCall(201)
    }

    def 'disableOrgGroupView'() {
        when:''
        service.disableOrgGroupView("1","2","3")

        then:''
        1 * iWikiUserService.checkDocExsist(*_) >> true
        1 * wikiClient.offerRightToOrgGroupView(*_) >>  getCall(201)
    }

    def 'disableProjectGroupView'() {
        when:''
        service.disableProjectGroupView("1","2","3","4","5")

        then:''
        1 * iWikiUserService.checkDocExsist(*_) >> true
        1 * wikiClient.offerRightToProjectGroupView(*_) >>  getCall(201)
    }

    def 'addRightsToOrg'() {
        given:'参数定义'
        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO()
        wikiGroupDTO.setGroupName("test")
        wikiGroupDTO.setOrganizationCode("test")
        wikiGroupDTO.setOrganizationName("test")
        wikiGroupDTO.setProjectCode("test")
        wikiGroupDTO.setProjectName("test")

        List<String> rights = Arrays.asList("a","b")

        when:''
        service.addRightsToOrg(wikiGroupDTO,rights,true,"admin")

        then:''
        1 * iWikiUserService.checkDocExsist(*_) >> true
        1 * wikiClient.offerRightToOrgGroupView(*_) >>  getCall(201)
    }

    def 'addRightsToProject'() {
        given:'参数定义'
        WikiGroupDTO wikiGroupDTO = new WikiGroupDTO()
        wikiGroupDTO.setGroupName("test")
        wikiGroupDTO.setOrganizationCode("test")
        wikiGroupDTO.setOrganizationName("test")
        wikiGroupDTO.setProjectCode("test")
        wikiGroupDTO.setProjectName("test")

        List<String> rights = Arrays.asList("a","b")

        when:''
        service.addRightsToProject(wikiGroupDTO,rights,true,"admin")

        then:''
        1 * iWikiUserService.checkDocExsist(*_) >> true
        1 * wikiClient.offerRightToProjectGroupView(*_) >>  getCall(201)
    }

    Call<ResponseBody> getCall(int code) {
        return new Call<ResponseBody>() {
            @Override
            Response<ResponseBody> execute() throws IOException {
                okhttp3.Response.Builder builder = new okhttp3.Response.Builder()
                builder.code(code)
                builder.message("haha")
                okhttp3.Response rawResponse = new okhttp3.Response(builder)

                BufferedSource buffer  = null
                def source = Okio.source(this.class.getResourceAsStream("/xml/webhome.xml"));
                buffer  = Okio.buffer(source);

                RealResponseBody realResponseBody = new RealResponseBody(new Headers(),buffer)

                Response<ResponseBody> response = new Response<>(rawResponse,realResponseBody,null);

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

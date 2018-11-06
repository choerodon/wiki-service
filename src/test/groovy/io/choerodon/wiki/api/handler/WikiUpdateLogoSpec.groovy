package io.choerodon.wiki.api.handler

import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.api.dto.WikiLogoDTO
import io.choerodon.wiki.api.eventhandler.WikiEventHandler
import io.choerodon.wiki.domain.service.IWikiLogoService
import io.choerodon.wiki.domain.service.impl.IWikiLogoServiceImpl
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

    @Autowired
    private IWikiLogoService iWikiLogoService;

    def '修改wiki logo'(){
        given: '定义请求数据格式'
        def data = "{\n" +
                "\"logo\": \"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_86d58ae1.png\",\n" +
                "\"simpleName\": \"org-demo\" \n" +
                "}";
        WikiLogoDTO wikiLogoDTO = new WikiLogoDTO();
        wikiLogoDTO.setSystemLogo("http://iam.choerodon.staging.saas.hand-china.com/assets/choerodon_logo_picture.52a5292b.svg")
        wikiLogoDTO.setSystemName("xingyu")

        and: 'Mock'
        1 * iWikiLogoService.updateLogo(*_)

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleLogoUpdateEvent(data)

        then: '数据校验'
        Assert.assertEquals(data, entity)
    }

}

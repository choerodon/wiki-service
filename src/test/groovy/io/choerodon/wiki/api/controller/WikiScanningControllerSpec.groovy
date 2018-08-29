package io.choerodon.wiki.api.controller

import io.choerodon.wiki.IntegrationTestConfiguration
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by Zenger on 2018/7/25.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class WikiScanningControllerSpec extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    void setup() {
    }

    def '扫描组织和项目'() {
        when: '模拟发送消息'
        def entity = restTemplate.getForEntity("/v1/scan",ResponseEntity)

        then: '校验返回数据'
        Assert.assertEquals(200, entity.statusCodeValue);
    }

    def '同步指定组织和项目组织和项目'() {

        when: '模拟发送消息'
        def entity = restTemplate.getForEntity("/v1/organizations/{organization_id}/spaces/sync_org",ResponseEntity,1L)

        then: '校验返回数据'
        Assert.assertEquals(200, entity.statusCodeValue);
    }

    def '更新wiki系统主页'() {
        given: '定义请求数据格式'

        when: '模拟发送消息'
        def entity = restTemplate.postForEntity("/v1/wiki/page",null,ResponseEntity)

        then: '校验返回数据'
        Assert.assertEquals(201, entity.statusCodeValue);
    }
}

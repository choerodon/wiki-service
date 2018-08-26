package io.choerodon.wiki.api.controller

import io.choerodon.core.domain.Page
import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.api.dto.WikiSpaceDTO
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO
import io.choerodon.wiki.api.eventhandler.WikiEventHandler
import io.choerodon.wiki.domain.application.entity.ProjectE
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE
import io.choerodon.wiki.domain.application.entity.iam.UserE
import io.choerodon.wiki.domain.application.repository.IamRepository
import io.choerodon.wiki.domain.service.*
import io.choerodon.wiki.infra.dataobject.iam.UserDO
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by Zenger on 2018/7/25.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class WikiCreateUserSpec extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private WikiEventHandler wikiEventHandler

    @Autowired
    private IWikiUserService iWikiUserService

    @Autowired
    private IWikiGroupService iWikiGroupService

    @Autowired
    private IamRepository iamRepository

    @Shared
    def OrganizationE organizationE

    @Shared
    def ProjectE projectE

    @Shared
    def UserE userE

    void setup() {
        UserDO userDO = new UserDO()
        userDO.setId(1L)
        userDO.setEmail("test@org.com")
        userDO.setLoginName("test")

        organizationE = new OrganizationE()
        organizationE.setId(1)
        organizationE.setCode("org")
        organizationE.setName("测试组织")
        organizationE.setEnabled(true)

        projectE = new ProjectE()
        projectE.setId(1L)
        projectE.setEnabled(true)
        projectE.setName("93ecb0-8")
        projectE.setCode("4c93c5a0-8")
        projectE.setOrganization(organizationE);

        userE = new UserE()
        userE.setId(9602)
        userE.setLoginName("WT1336")
        userE.setEmail("biran.wt@hand-china.com")
        userE.setRealName("毕冉")
        userE.setPhone("110")
        userE.setOrganization(organizationE)
    }

    def '创建用户'() {
        given: '定义请求数据格式'
        def payload = "[{\"id\":\"9602\"," +
                "\"name\":\"毕冉\"," +
                "\"username\":\"WT1336\"," +
                "\"email\":\"biran.wt@hand-china.com\"}]";

        and: 'Mock'
        1 * iamRepository.queryByLoginName(_) >>  userE
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        1 * iWikiUserService.checkDocExsist(_, _) >> false
        1 * iWikiUserService.createUser(*_)
        1 * iWikiGroupService.createGroupUsers(*_)

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleCreateUserEvent(payload)

        then: '校验返回数据'
        Assert.assertEquals(payload, entity);
    }
}

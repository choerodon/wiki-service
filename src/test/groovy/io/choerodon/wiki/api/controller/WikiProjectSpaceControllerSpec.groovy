package io.choerodon.wiki.api.controller

import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.api.dto.WikiSpaceDTO
import io.choerodon.wiki.api.eventhandler.WikiEventHandler
import io.choerodon.wiki.domain.application.repository.IamRepository
import io.choerodon.wiki.domain.service.IWikiClassService
import io.choerodon.wiki.domain.service.IWikiGroupService
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService
import io.choerodon.wiki.domain.service.IWikiSpaceWebPreferencesService
import io.choerodon.wiki.domain.service.IWikiUserService
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class WikiProjectSpaceControllerSpec extends Specification {
    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private WikiEventHandler wikiEventHandler

    @Autowired
    private IWikiSpaceWebPreferencesService iWikiSpaceWebPreferencesService

    @Autowired
    @Qualifier("mockiWikiSpaceWebHomeService")
    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService

    @Autowired
    private IWikiUserService iWikiUserService

    @Autowired
    private IWikiGroupService iWikiGroupService

    @Autowired
    private IamRepository iamRepository

    @Autowired
    private IWikiClassService iWikiClassService

    @Shared
    def organizationId = 1L

    @Shared
    def project_id = 1L

    @Shared
    def spaceName = '敏捷回顾会议记录'

    def '检查项目下空间名唯一性'() {
        given: '给定一个空间名'

        when: '向接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/space/check?' + 'name=' + spaceName, Boolean, project_id)

        then: '状态码为200;返回的数据为true'
        entity.statusCode.is2xxSuccessful()
        Assert.assertTrue(entity.body)
    }

    def '创建项目对应wiki空间'() {
        given: '定义请求数据格式'
        def data = "{\n" +
                "  \"projectId\": 1,\n" +
                "  \"projectCode\": \"2018\",\n" +
                "  \"projectName\": \"2018\",\n" +
                "  \"organizationCode\": \"2018\",\n" +
                "  \"organizationName\": \"2018\",\n" +
                "  \"userName\": \"2018\",\n" +
                "  \"userId\": 1,\n" +
                "  \"roleLabels\": null\n" +
                "}";

        when: '模拟发送kafka消息'
        wikiEventHandler.handleProjectCreateEvent(data)

        then: ''
        1 * iWikiSpaceWebHomeService.createSpace2WebHome(_, _, _, _) >> 201
        1 * iWikiSpaceWebPreferencesService.createSpace2WebPreferences(_, _, _, _) >> 201
        2 * iWikiUserService.checkDocExsist(_, _) >>> false >> true
        2 * iWikiGroupService.createGroup(_, _)
        2 * iWikiGroupService.addRightsToOrg(_, _, _, _)
        0 * iWikiGroupService.addRightsToProject(_, _, _, _)
        1 * iamRepository.queryUserById(_) >> userE
        1 * iWikiUserService.checkDocExsist(_, _) >> false
        1 * iWikiUserService.createUser(_, _, _, _)
        1 * iWikiGroupService.createGroupUsers(_, _, _)

        2 * iWikiUserService.checkDocExsist(_, _) >>> false >> true
    }

    def '项目下创建wiki空间'() {
        given: '定义请求数据格式'
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO()
        wikiSpaceDTO.setIcon("flag")
        wikiSpaceDTO.setName(spaceName)

        when: '向接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/space', wikiSpaceDTO, null, project_id)

        then: '状态码为201'
        1 * iWikiSpaceWebHomeService.createSpace3WebHome(_,_,_,_,_) >> 201
        1 * iWikiSpaceWebPreferencesService.createSpace3WebPreferences(_,_,_,_,_) >> 201
        Assert.assertEquals(201, entity.statusCodeValue)
    }
}

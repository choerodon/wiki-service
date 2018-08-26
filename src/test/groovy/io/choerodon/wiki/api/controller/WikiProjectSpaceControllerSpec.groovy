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
class WikiProjectSpaceControllerSpec extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private WikiEventHandler wikiEventHandler

    @Autowired
    @Qualifier("mockiWikiSpaceWebHomeService")
    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService

    @Autowired
    private IWikiSpaceWebPreferencesService iWikiSpaceWebPreferencesService

    @Autowired
    private IWikiUserService iWikiUserService

    @Autowired
    private IWikiGroupService iWikiGroupService

    @Autowired
    private IamRepository iamRepository

    @Autowired
    private IWikiClassService iWikiClassService

    @Shared
    def projectId = 1L

    @Shared
    def projectSpaceName = "93ecb0-8"

    @Shared
    def spaceName = 'wewe'

    @Shared
    def OrganizationE organizationE

    @Shared
    def ProjectE projectE

    @Shared
    def UserE userE

    @Shared
    def wikiId

    @Shared
    def projectUnderWikiId

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
        userE.setId(1L)
        userE.setLoginName("test")
        userE.setEmail("test@org.com")
        userE.setRealName("test")
    }

    def '检查项目下空间名唯一性'() {
        given: '给定一个空间名'

        when: '向接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/space/check?' + 'name=' + spaceName, Boolean, projectId)

        then: '状态码为200;返回的数据为true'
        entity.statusCode.is2xxSuccessful()
        Assert.assertTrue(entity.body)
    }

    def '创建项目对应wiki空间'() {
        given: '定义请求数据格式'
        def data = "{\n" +
                "  \"projectId\":1 ,\n" +
                "  \"projectCode\": \"4c93c5a0-8\",\n" +
                "  \"projectName\":\"93ecb0-8\",\n" +
                "  \"organizationCode\": \"apitest\",\n" +
                "  \"organizationName\": \"API测试用组织修改\",\n" +
                "  \"userName\": \"test\",\n" +
                "  \"userId\":11751 ,\n" +
                "  \"roleLabels\": [\"project.wiki.admin\",\"project.wiki.user\"]\n" +
                "}"

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace2WebHome(*_) >> 201
        1 * iWikiSpaceWebPreferencesService.createSpace2WebPreferences(*_) >> 201
        2 * iWikiUserService.checkDocExsist(_, _) >>> false >> true
        2 * iWikiGroupService.createGroup(_, _)
        2 * iWikiGroupService.addRightsToProject(_, _, _, _)
        1 * iamRepository.queryUserById(_) >> userE
        1 * iWikiUserService.checkDocExsist(_, _) >> false
        1 * iWikiUserService.createUser(_, _, _, _)
        1 * iWikiGroupService.createGroupUsers(_, _, _)
        2 * iWikiUserService.checkDocExsist(_, _) >>> false >> true

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleProjectCreateEvent(data)

        then: '校验返回数据'
        Assert.assertEquals(data, entity);
    }

    def '项目下创建wiki空间'() {
        given: '定义请求数据格式'
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO()
        wikiSpaceDTO.setIcon("flag")
        wikiSpaceDTO.setName(spaceName)

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace3WebHome(_,_,_,_,_) >> 201
        1 * iWikiSpaceWebPreferencesService.createSpace3WebPreferences(_,_,_,_,_) >> 201

        when: '向接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/space', wikiSpaceDTO, null, projectId)

        then: '状态码为201'
        Assert.assertEquals(201, entity.statusCodeValue)
    }

    def '分页查询项目下创建的空间'() {
        given: '定义请求数据格式'
        def searchParam = ""

        when: '向接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/space/list_by_options?page=0&size=10', searchParam, Page.class, projectId)
        List<WikiSpaceListTreeDTO> list = entity.body.content
        wikiId = list.get(0).id
        projectUnderWikiId =list.get(0).children.get(0).id

        then: '状态码为201'
        Assert.assertEquals(201, entity.statusCodeValue)
    }

    def '查询项目下单个wiki空间'() {
        given: '定义请求数据格式'
        def id = wikiId

        when: '向接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/space/{id}', WikiSpaceResponseDTO.class, projectId, id)

        then: '状态码为200,返回数据与请求数据相同'
        Assert.assertEquals(200, entity.statusCodeValue)
        Assert.assertEquals(id, entity.body.getId())
    }

    def '更新项目对应的空间'() {
        given: '定义请求数据格式'
        def id = wikiId

        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO()
        wikiSpaceDTO.setIcon("dns")
        wikiSpaceDTO.setName("P-" + projectSpaceName)

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace2WebHome(*_)

        when: '向接口发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/space/{id}', HttpMethod.PUT,
                new HttpEntity<>(wikiSpaceDTO), WikiSpaceResponseDTO.class, projectId, id)

        then: '状态码为201,返回数据与请求数据相同'
        Assert.assertEquals(201, entity.statusCodeValue)
        Assert.assertEquals(id, entity.body.getId())
        Assert.assertEquals("dns", entity.body.getIcon())
    }

    def '更新项目下的空间'() {
        given: '定义请求数据格式'
        def id = projectUnderWikiId
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO()
        wikiSpaceDTO.setIcon("dns")
        wikiSpaceDTO.setName(spaceName)

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace3WebHome(*_)

        when: '向接口发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/space/{id}', HttpMethod.PUT,
                new HttpEntity<>(wikiSpaceDTO), WikiSpaceResponseDTO.class, projectId, id)

        then: '状态码为201,返回数据与请求数据相同'
        Assert.assertEquals(201, entity.statusCodeValue)
        Assert.assertEquals(id, entity.body.getId())
        Assert.assertEquals("dns", entity.body.getIcon())
    }

    def '项目禁用'() {
        given: '定义请求数据格式'
        def payload = "{\"projectId\":1," +
                "\"projectCode\":null," +
                "\"projectName\":null," +
                "\"organizationCode\":null," +
                "\"organizationName\":null," +
                "\"userName\":null," +
                "\"userId\":null," +
                "\"roleLabels\":null}"

        and: 'Mock'
        1 * iamRepository.queryIamProject(_) >> projectE
        1 * iamRepository.queryOrganizationById(_) >> new OrganizationE()
        1 * iWikiGroupService.disableProjectGroupView(*_)

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleProjectDisableEvent(payload)

        then: '校验返回数据'
        Assert.assertEquals(payload, entity);
    }

    def '项目启用'() {
        given: '定义请求数据格式'
        def payload = "{\"projectId\":1," +
                "\"projectCode\":null," +
                "\"projectName\":null," +
                "\"organizationCode\":null," +
                "\"organizationName\":null," +
                "\"userName\":null," +
                "\"userId\":null," +
                "\"roleLabels\":null}"

        def page = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' +
                '<objects xmlns="http://www.xwiki.org">\n' +
                '    <objectSummary>\n' +
                '        <className>XWiki.XWikiGlobalRights</className>\n' +
                '        <number>1</number>\n' +
                '        <headline>0</headline>\n' +
                '    </objectSummary>\n' +
                '</objects>'

        and: 'Mock'
        1 * iamRepository.queryIamProject(_) >> projectE
        1 * iamRepository.queryOrganizationById(_) >> new OrganizationE()
        1 * iWikiClassService.getProjectPageClassResource(*_) >> page
        1 * iWikiClassService.deleteProjectPageClass(*_)

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleProjectEnableEvent(payload)

        then: '校验返回数据'
        Assert.assertEquals(payload, entity);
    }

    def '删除项目下的空间'() {
        given: '定义请求数据格式'
        def id = wikiId
        Page<ProjectE> projectEPage = new Page<>()
        projectEPage.setTotalPages(2)
        projectEPage.setContent(Arrays.asList(projectE))


        and: 'Mock'
        1 * iamRepository.queryIamProject(_) >> projectE
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        2 * iWikiSpaceWebHomeService.deletePage(*_) >> 204
        2 * iWikiSpaceWebHomeService.deletePage1(*_) >> 204
//        2 * iamRepository.pageByProject(*_) >>  projectEPage
        2 * iWikiSpaceWebHomeService.deletePage2(*_) >> 204

        when: '向接口发请求'
        restTemplate.delete('/v1/projects/{project_id}/space/{id}',projectId, id)

        then: '校验返回数据'
    }
}

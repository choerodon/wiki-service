package io.choerodon.wiki.api.controller

import com.github.pagehelper.PageInfo
import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.api.dto.WikiSpaceDTO
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO
import io.choerodon.wiki.api.eventhandler.WikiEventHandler
import io.choerodon.wiki.domain.application.entity.WikiSpaceE
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE
import io.choerodon.wiki.domain.application.entity.iam.UserE
import io.choerodon.wiki.domain.application.repository.IamRepository
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository
import io.choerodon.wiki.domain.service.*
import io.choerodon.wiki.infra.common.enums.SpaceStatus
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType
import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO
import io.choerodon.wiki.infra.dataobject.iam.UserDO
import io.choerodon.wiki.infra.feign.IamServiceClient
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by Zenger on 2018/7/25.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class WikiOrganizationSpaceControllerSpec extends Specification {

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
    private WikiSpaceRepository wikiSpaceRepository

    @Autowired
    private IWikiClassService iWikiClassService

    @Shared
    def organizationId = 1L

    @Shared
    def spaceName = '敏捷回顾会议记录'

    @Shared
    def orgSpaceName = '客户演示'

    @Shared
    def OrganizationE organizationE

    @Shared
    def OrganizationDO organizationDO

    @Shared
    def UserE userE

    @Shared
    def UserDO userDO

    @Shared
    def wikiId

    @Shared
    def orgUnderWikiId

    @Shared
    def path = '/v1/organizations/{organization_id}/space'

    @Shared
    IamServiceClient iamServiceClient

    @Shared
    ResponseEntity<OrganizationDO> organization

    void setup() {
        organizationE = new OrganizationE()
        organizationE.setId(1)
        organizationE.setCode("org")
        organizationE.setName("测试组织")
        organizationE.setEnabled(true)

        organizationDO = new OrganizationDO()
        organizationDO.setId(1)
        organizationDO.setCode("org")
        organizationDO.setName("测试组织")
        organizationDO.setEnabled(true)

        userE = new UserE()
        userE.setId(1L)
        userE.setLoginName("test")
        userE.setEmail("test@org.com")
        userE.setRealName("test")

        userDO = new UserDO()
        userDO.setId(1L)
        userDO.setEmail("test@org.com")
        userDO.setLoginName("test")

        organization = new ResponseEntity<>(organizationDO, HttpStatus.OK)
    }

    def '检查组织下空间名唯一性'() {
        given: '给定一个空间名'

        when: '向接口发请求'
        def entity = restTemplate.getForEntity(path + '/check?name=' + spaceName, Boolean.class, organizationId)

        then: '状态码为200;返回的数据为true'
        entity.statusCode.is2xxSuccessful()
        Assert.assertTrue(entity.body)
    }

    def '创建组织对应wiki空间'() {
        given: '定义请求数据格式'
        def data = "{\n" +
                "\"id\":1 ,\n" +
                "\"name\": \"客户演示\",\n" +
                "\"code\": \"org-demo\",\n" +
                "\"userId\":2\n" +
                "}";

        List<UserDO> list = new ArrayList<>();
        list.add(userDO)
        ResponseEntity<List<UserDO>> responseEntity = new ResponseEntity<>(list, HttpStatus.OK)

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace1WebHome(*_) >> 201
        1 * iWikiSpaceWebPreferencesService.createSpace1WebPreferences(*_) >> 201
        2 * iWikiUserService.checkDocExsist(_, _) >>> false >> true
        2 * iWikiGroupService.createGroup(_, _)
        2 * iWikiGroupService.addRightsToOrg(_, _, _, _)
        1 * iamRepository.queryUserByIds(*_) >> userE
        1 * iWikiUserService.checkDocExsist(_, _) >> false
        1 * iWikiUserService.createUser(_, _, _) >> true
        1 * iWikiGroupService.createGroupUsers(_, _, _)
        2 * iWikiUserService.checkDocExsist(_, _) >>> false >> true

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleOrganizationCreateEvent(data)

        then: '校验返回数据'
        Assert.assertEquals(data, entity);
    }

    def '组织下创建wiki空间'() {
        given: '定义请求数据格式'
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO()
        wikiSpaceDTO.setIcon("flag")
        wikiSpaceDTO.setName(spaceName)

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace2WebHome(*_) >> 201
        1 * iWikiSpaceWebPreferencesService.createSpace2WebPreferences(*_) >> 201

        when: '向接口发请求'
        def entity = restTemplate.postForEntity(path, wikiSpaceDTO, null, organizationId)
        Thread.sleep(2000)

        then: '状态码为201'
        Assert.assertEquals(201, entity.statusCodeValue)
    }

    def '分页查询组织下创建的空间'() {
        given: '定义请求数据格式'
        def searchParam = ""

        when: '向接口发请求'
        def entity = restTemplate.postForEntity(path + '/list_by_options?page=0&size=10', searchParam, Page.class, organizationId)
        List<WikiSpaceListTreeDTO> list = entity.body.content
        wikiId = list.get(0).id
        orgUnderWikiId = list.get(0).children.get(0).id

        then: '状态码为201'
        Assert.assertEquals(201, entity.statusCodeValue)
    }

    def '查询组织下的wiki空间'() {
        given: '定义请求数据格式'
        def id = organizationId

        when: '向接口发请求'
        def entity = restTemplate.getForEntity(path + '/under', List.class, id)

        then: '状态码为200,返回数据与请求数据相同'
        Assert.assertEquals(200, entity.statusCodeValue)
    }

    def '查询组织下单个wiki空间'() {
        given: '定义请求数据格式'
        def id = wikiId

        when: '向接口发请求'
        def entity = restTemplate.getForEntity(path + '/{id}', WikiSpaceResponseDTO.class, organizationId, id)

        then: '状态码为200,返回数据与请求数据相同'
        Assert.assertEquals(200, entity.statusCodeValue)
        Assert.assertEquals(id, entity.body.getId())
    }

    def '更新组织对应的空间'() {
        given: '定义请求数据格式'
        def id = wikiId
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO()
        wikiSpaceDTO.setIcon("dns")
        wikiSpaceDTO.setName("O-" + orgSpaceName)

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace1WebHome(*_)

        when: '向接口发请求'
        def entity = restTemplate.exchange(path + '/{id}', HttpMethod.PUT,
                new HttpEntity<>(wikiSpaceDTO), WikiSpaceResponseDTO.class, organizationId, id)

        then: '状态码为201,返回数据与请求数据相同'
        Assert.assertEquals(201, entity.statusCodeValue)
        Assert.assertEquals(id, entity.body.getId())
        Assert.assertEquals("dns", entity.body.getIcon())
    }

    def '更新组织下的空间'() {
        given: '定义请求数据格式'
        def id = orgUnderWikiId
        WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO()
        wikiSpaceDTO.setIcon("dns")
        wikiSpaceDTO.setName(spaceName)

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.createSpace2WebHome(*_)

        when: '向接口发请求'
        def entity = restTemplate.exchange(path + '/{id}', HttpMethod.PUT,
                new HttpEntity<>(wikiSpaceDTO), WikiSpaceResponseDTO.class, organizationId, id)

        then: '状态码为201,返回数据与请求数据相同'
        Assert.assertEquals(201, entity.statusCodeValue)
        Assert.assertEquals(id, entity.body.getId())
        Assert.assertEquals("dns", entity.body.getIcon())
    }

    def '组织禁用'() {
        given: '定义请求数据格式'
        def payload = "{\n" +
                "  \"organizationId\":1 \n" +
                "}"

        and: 'Mock'
        1 * iamRepository.queryOrganizationById(*_) >> organizationE
        1 * iWikiGroupService.disableOrgGroupView(_, _, _)

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleOrganizationDisableEvent(payload)

        then: '校验返回数据'
        Assert.assertEquals(payload, entity);
    }

    def '组织启用'() {
        given: '定义请求数据格式'
        def payload = "{\n" +
                "  \"organizationId\":1 \n" +
                "}"
        def page = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' +
                '<objects xmlns="http://www.xwiki.org">\n' +
                '    <objectSummary>\n' +
                '        <className>XWiki.XWikiGlobalRights</className>\n' +
                '        <number>1</number>\n' +
                '        <headline>0</headline>\n' +
                '    </objectSummary>\n' +
                '</objects>'

        and: 'Mock'
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        1 * iWikiClassService.getPageClassResource(_, _, _, _) >> page
        1 * iWikiClassService.deletePageClass(_, _, _, _, _)

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleOrganizationEnableEvent(payload)

        then: '校验返回数据'
        Assert.assertEquals(payload, entity);
    }

    def '角色同步'() {
        given: '定义请求数据格式'
        def payload = "[\n" +
                "  {\n" +
                "    \"userId\": 1,\n" +
                "    \"username\": \"test\",\n" +
                "    \"resourceId\": 1,\n" +
                "    \"resourceType\": \"organization\",\n" +
                "    \"roleLabels\": [\n" +
                "      \"organization.wiki.admin\"\n" +
                "    ],\n" +
                "    \"uuid\": null\n" +
                "  }\n" +
                "]";

        ResponseEntity<UserDO> userDOResponseEntity = new ResponseEntity<>(userDO, HttpStatus.OK)

        and: 'Mock'
        2 * iamRepository.queryOrganizationById(*_) >> organizationE
        1 * iamRepository.queryByLoginName(*_) >> userE
        3 * iWikiUserService.checkDocExsist(*_) >> true
        1 * iWikiGroupService.createGroupUsers(_, _, _) >> true

        when: '模拟发送消息'
        def entity = wikiEventHandler.handleCreateGroupMemberEvent(payload)

        then: '校验返回数据'
        Assert.assertEquals(payload, entity);
    }

    def '去除角色不包含label'() {
        given: '定义请求数据格式'
        def payload = "[\n" +
                "  {\n" +
                "    \"username\": \"test\",\n" +
                "    \"resourceId\": 1,\n" +
                "    \"resourceType\": \"organization\",\n" +
                "    \"roleLabels\": null,\n" +
                "    \"userId\": 1,\n" +
                "    \"uuid\": null\n" +
                "  }\n" +
                "]";

        def admin = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' +
                '<objects xmlns="http://www.xwiki.org">\n' +
                '    <objectSummary>\n' +
                '        <pageName>O-orgAdminGroup</pageName>\n' +
                '        <number>1</number>\n' +
                '        <headline>XWiki.test</headline>\n' +
                '    </objectSummary>\n' +
                '</objects>'
        def user = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' +
                '<objects xmlns="http://www.xwiki.org">\n' +
                '    <objectSummary>\n' +
                '        <pageName>O-orgUserGroup</pageName>\n' +
                '        <number>1</number>\n' +
                '        <headline>XWiki.test</headline>\n' +
                '    </objectSummary>\n' +
                '</objects>'

        and: 'Mock'
        2 * iamRepository.queryOrganizationById(_) >> organizationE
        1 * iWikiClassService.getPageClassResource(_, _, _, _) >> admin
        1 * iWikiClassService.getPageClassResource(_, _, _, _) >> user
        2 * iWikiClassService.deletePageClass(_, _, _, _, _)
        2 * iWikiUserService.checkDocExsist(*_) >> true

        when: '模拟发送消息'
        def entity = wikiEventHandler.handledeleteMemberRoleEvent(payload)

        then: '校验返回数据'
        Assert.assertEquals(payload, entity);
    }

    def '删除组织下的空间'() {
        given: '定义请求数据格式'
        def id = wikiId
        PageInfo<ProjectDO> page = new PageInfo<>()
        page.setTotalPages(2)
        ProjectDO projectDO = new ProjectDO()
        projectDO.setId(1)
        projectDO.setOrganizationId(1L)
        page.setContent(Arrays.asList(projectDO))

        ResponseEntity<PageInfo<ProjectDO>> pageResponseEntity = new ResponseEntity<>(page, HttpStatus.OK)
        ResponseEntity<ProjectDO> projectDOResponseEntity = new ResponseEntity<>(projectDO, HttpStatus.OK)

        and: 'Mock'
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        4 * iWikiSpaceWebHomeService.deletePage(*_) >> 204
        2 * iWikiSpaceWebHomeService.deletePage1(*_) >> 204

        when: '向接口发请求'
        restTemplate.delete(path + '/{id}', organizationId, id)

        then: '校验返回数据'
    }
}

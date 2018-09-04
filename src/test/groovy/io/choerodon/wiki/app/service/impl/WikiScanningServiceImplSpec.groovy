package io.choerodon.wiki.app.service.impl

import io.choerodon.core.domain.Page
import io.choerodon.wiki.app.service.WikiGroupService
import io.choerodon.wiki.app.service.WikiSpaceService
import io.choerodon.wiki.domain.application.entity.ProjectE
import io.choerodon.wiki.domain.application.entity.WikiSpaceE
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE
import io.choerodon.wiki.domain.application.entity.iam.RoleE
import io.choerodon.wiki.domain.application.entity.iam.UserE
import io.choerodon.wiki.domain.application.repository.IamRepository
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Zenger on 2018/7/25.
 */
class WikiScanningServiceImplSpec extends Specification {

    IamRepository iamRepository;
    WikiSpaceRepository wikiSpaceRepository;
    WikiSpaceService wikiSpaceService;
    WikiGroupService wikiGroupService;
    IWikiSpaceWebHomeService iWikiSpaceWebHomeService;
    WikiScanningServiceImpl service

    @Shared
    def OrganizationE organizationE

    @Shared
    def ProjectE projectE

    @Shared
    def UserE userE

    void setup() {
        iamRepository = Mock(IamRepository)
        wikiSpaceRepository = Mock(WikiSpaceRepository)
        wikiSpaceService = Mock(WikiSpaceService)
        wikiGroupService = Mock(WikiGroupService)
        iWikiSpaceWebHomeService = Mock(IWikiSpaceWebHomeService)
        service = new WikiScanningServiceImpl(iamRepository,
                wikiSpaceRepository,
                wikiSpaceService,
                wikiGroupService,
                iWikiSpaceWebHomeService)

        organizationE = new OrganizationE()
        organizationE.setId(1)
        organizationE.setCode("org")
        organizationE.setName("测试组织")
        organizationE.setEnabled(false)
        organizationE.setProjectCount(2)

        projectE = new ProjectE()
        projectE.setId(1L)
        projectE.setEnabled(false)
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

    def 'syncOrg'() {
        given: '定义请求数据格式'
        def orgId = 1L

        Page<ProjectE> projectEPage = new Page<>()
        projectEPage.setTotalPages(2)
        projectEPage.setContent(Arrays.asList(projectE))

        RoleE roleE = new RoleE()
        roleE.setId(1L)
        Page<RoleE> rolePage = new Page<>()
        rolePage.setContent(Arrays.asList(roleE))

        Page<UserE> userEPage = new Page<>()
        userEPage.setTotalPages(2)
        userEPage.setContent(Arrays.asList(userE))

        and: 'Mock'
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        3 * wikiSpaceRepository.getWikiSpaceList(*_) >> null
        3 * wikiSpaceService.create(*_)
        6 * wikiGroupService.create(*_)
        2 * iamRepository.pageByProject(*_) >> projectEPage
        5 * iamRepository.roleList(*_) >> rolePage
        2 * iamRepository.pagingQueryUsersByRoleIdOnOrganizationLevel(*_) >> userEPage
        8 * iamRepository.pagingQueryUsersByRoleIdOnProjectLevel(*_) >> userEPage
        10 * wikiGroupService.setUserToGroup(*_)
        2 * wikiGroupService.disableProjectGroup(*_)
        1 * wikiGroupService.disableOrganizationGroup(*_)

        when: '模拟发送消息'
        service.syncOrg(orgId)

        then: ''
    }

    def 'scanning'() {
        given: '定义请求数据格式'
        OrganizationE organization = new OrganizationE()
        organization.setId(1)
        organization.setCode("org")
        organization.setName("测试组织")
        organization.setEnabled(true)
        organization.setProjectCount(2)
        Page<OrganizationE> pageByOrganization = new Page<>()
        pageByOrganization.setTotalPages(2)
        pageByOrganization.setContent(Arrays.asList(organization))

        Page<ProjectE> page = new Page<>()
        page.setTotalPages(1)
        page.setContent(Arrays.asList(projectE))

        WikiSpaceE wikiSpaceE = new WikiSpaceE()
        wikiSpaceE.setId(1L)
        List<WikiSpaceE> wikiSpaceEList = new ArrayList<>()
        wikiSpaceEList.add(wikiSpaceE)

        and: 'Mock'
        2 * iamRepository.pageByOrganization(*_) >> pageByOrganization
        4 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList
        2 * iamRepository.pageByProject(*_) >> page

        when: '模拟发送消息'
        service.scanning()

        then: ''
    }

    def 'updateWikiPage'() {
        given: '定义请求数据格式'
        WikiSpaceE orgSpace = new WikiSpaceE()
        orgSpace.setId(1)
        orgSpace.setResourceId(256)
        orgSpace.setResourceType("organization")
        orgSpace.setName("O-知识管理测试4")
        orgSpace.setIcon("domain")
        orgSpace.setPath("O-知识管理测试4")
        orgSpace.setStatus("success")
        List<WikiSpaceE> orgWikiSpaceEList = new ArrayList<>()
        orgWikiSpaceEList.add(orgSpace)

        WikiSpaceE orgUnderSpace = new WikiSpaceE()
        orgUnderSpace.setId(2)
        orgUnderSpace.setResourceId(256)
        orgUnderSpace.setResourceType("organization-s")
        orgUnderSpace.setName("test1")
        orgUnderSpace.setIcon("instance_outline")
        orgUnderSpace.setPath("O-知识管理测试4/test1")
        orgUnderSpace.setStatus("success")
        List<WikiSpaceE> orgUnderList = new ArrayList<>()
        orgUnderList.add(orgUnderSpace)

        WikiSpaceE projectSpace = new WikiSpaceE()
        projectSpace.setId(3)
        projectSpace.setResourceId(237)
        projectSpace.setResourceType("project")
        projectSpace.setName("P-知识管理测试项目1")
        projectSpace.setIcon("branch")
        projectSpace.setPath("O-知识管理测试4/P-知识管理测试项目1")
        projectSpace.setStatus("success")
        List<WikiSpaceE> projectWikiSpaceList = new ArrayList<>()
        projectWikiSpaceList.add(projectSpace)

        WikiSpaceE projectUnderSpace = new WikiSpaceE()
        projectUnderSpace.setId(4)
        projectUnderSpace.setResourceId(237)
        projectUnderSpace.setResourceType("project-s")
        projectUnderSpace.setName("test4")
        projectUnderSpace.setIcon("compass")
        projectUnderSpace.setPath("O-知识管理测试4/P-知识管理测试项目1/test4")
        projectUnderSpace.setStatus("success")
        List<WikiSpaceE> projectUnderSpaceList = new ArrayList<>()
        projectUnderSpaceList.add(projectUnderSpace)

        and: 'Mock'
        1 * wikiSpaceRepository.getWikiSpaceByType(*_) >> orgWikiSpaceEList
        1 * wikiSpaceRepository.getWikiSpaceByType(*_) >> projectWikiSpaceList
        1 * iWikiSpaceWebHomeService.createSpace1WebHome(*_)
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> orgUnderList
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> projectUnderSpaceList
        2 * iWikiSpaceWebHomeService.createSpace2WebHome(*_)
        1 * iWikiSpaceWebHomeService.createSpace3WebHome(*_)

        when: '模拟发送消息'
        service.updateWikiPage()

        then: ''
    }
}

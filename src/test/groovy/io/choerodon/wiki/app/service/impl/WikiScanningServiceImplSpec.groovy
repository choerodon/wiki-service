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
import io.choerodon.wiki.domain.service.IWikiGroupService
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
    IWikiGroupService iWikiGroupService;
    WikiScanningServiceImpl service

    @Shared
    def OrganizationE organizationE

    @Shared
    def ProjectE projectE

    @Shared
    def UserE userE

    @Shared
    def List<WikiSpaceE> wikiSpaceEList

    void setup() {
        iamRepository = Mock(IamRepository)
        wikiSpaceRepository = Mock(WikiSpaceRepository)
        wikiSpaceService = Mock(WikiSpaceService)
        wikiGroupService = Mock(WikiGroupService)
        iWikiSpaceWebHomeService = Mock(IWikiSpaceWebHomeService)
        iWikiGroupService = Mock(IWikiGroupService)
        service = new WikiScanningServiceImpl(iamRepository,
                wikiSpaceRepository,
                wikiSpaceService,
                wikiGroupService,
                iWikiSpaceWebHomeService,
                iWikiGroupService)

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

        WikiSpaceE wikiSpaceE = new WikiSpaceE()
        wikiSpaceE.setId(1L)
        wikiSpaceE.setStatus("success")
        wikiSpaceEList = new ArrayList<>()
        wikiSpaceEList.add(wikiSpaceE)
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
        page.setTotalPages(0)
        page.setContent(Arrays.asList(projectE))

        and: 'Mock'
        2 * iamRepository.pageByOrganization(*_) >> pageByOrganization
        4 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList
        2 * iamRepository.pageByProject(*_) >> page

        when: '模拟发送消息'
        service.scanning()

        then: ''
    }

    def 'syncOrgAndProject'() {
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

        WikiSpaceE wikiSpaceE = new WikiSpaceE()
        wikiSpaceE.setId(1L)
        wikiSpaceE.setStatus("failed")
        List<WikiSpaceE> wikiSpaceEList1 = new ArrayList<>()
        wikiSpaceEList1.add(wikiSpaceE)

        and: 'Mock'
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> null
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList1
        2 * wikiSpaceService.create(*_)
        4 * wikiGroupService.create(*_)
        2 * iamRepository.pageByProject(*_) >> projectEPage
        4 * iamRepository.roleList(*_) >> rolePage
        8 * iamRepository.pagingQueryUsersByRoleIdOnProjectLevel(*_) >> userEPage
        8 * wikiGroupService.setUserToGroup(*_)
        2 * wikiGroupService.disableProjectGroup(*_)

        when: '模拟发送消息'
        service.syncOrgAndProject(orgId)

        then: ''
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

        List<WikiSpaceE> wikiSpaceEList = new ArrayList<>()
        WikiSpaceE wikiSpaceE = new WikiSpaceE()
        wikiSpaceE.setId(1)
        wikiSpaceE.setStatus("failed")
        wikiSpaceEList.add(wikiSpaceE)

        and: 'Mock'
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList
        1 * wikiSpaceService.create(*_)
        2 * wikiGroupService.create(*_)
        1 * iamRepository.roleList(*_) >> rolePage
        2 * iamRepository.pagingQueryUsersByRoleIdOnOrganizationLevel(*_) >> userEPage
        2 * wikiGroupService.setUserToGroup(*_)
        1 * wikiGroupService.disableOrganizationGroup(*_)

        when: '模拟发送消息'
        service.syncOrg(orgId)

        then: ''
    }

    def 'syncProject'() {
        given: '定义请求数据格式'
        def projectId = 1L

        List<WikiSpaceE> wikiSpaceList = new ArrayList<>()
        WikiSpaceE wikiSpaceE = new WikiSpaceE()
        wikiSpaceE.setId(1)
        wikiSpaceE.setStatus("failed")
        wikiSpaceList.add(wikiSpaceE)

        Page<ProjectE> page = new Page<>()
        page.setTotalPages(0)
        page.setContent(Arrays.asList(projectE))

        and: 'Mock'
        1 * iamRepository.queryIamProject(*_) >> projectE
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceList
        2 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        1 * iamRepository.pageByProject(*_) >> page

        when: '模拟发送消息'
        service.syncProject(projectId)

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

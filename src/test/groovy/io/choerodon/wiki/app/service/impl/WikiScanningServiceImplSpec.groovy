package io.choerodon.wiki.app.service.impl

import com.github.pagehelper.PageInfo
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
import io.choerodon.wiki.domain.service.IWikiUserService
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
    IWikiUserService iWikiUserService;
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
        iWikiUserService = Mock(IWikiUserService)

        service = new WikiScanningServiceImpl(iamRepository,
                wikiSpaceRepository,
                wikiSpaceService,
                wikiGroupService,
                iWikiSpaceWebHomeService,
                iWikiGroupService,
                iWikiUserService)

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
        wikiSpaceE.setPath("O-用户测试组织5b3")
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

        PageInfo<OrganizationE> pageByOrganization = new PageInfo<>()
        pageByOrganization.setTotalPages(2)
        pageByOrganization.setContent(Arrays.asList(organization))

        PageInfo<ProjectE> page = new PageInfo<>()
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
        PageInfo<ProjectE> projectEPage = new PageInfo<>()
        projectEPage.setTotalPages(2)
        projectEPage.setContent(Arrays.asList(projectE))

        RoleE roleE = new RoleE()
        roleE.setId(1L)
        PageInfo<RoleE> rolePage = new PageInfo<>()
        rolePage.setContent(Arrays.asList(roleE))

        PageInfo<UserE> userEPage = new PageInfo<>()
        userEPage.setTotalPages(2)
        userEPage.setContent(Arrays.asList(userE))

        WikiSpaceE wikiSpaceE = new WikiSpaceE()
        wikiSpaceE.setId(1L)
        wikiSpaceE.setStatus("failed")
        wikiSpaceE.setPath("O-用户测试组织5b3")
        List<WikiSpaceE> wikiSpaceEList1 = new ArrayList<>()
        wikiSpaceEList1.add(wikiSpaceE)

        and: 'Mock'
        1 * iamRepository.queryOrganizationById(_) >> organizationE
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> null
        1 * wikiSpaceRepository.getWikiSpaceList(*_) >> wikiSpaceEList1
        1 * wikiSpaceService.create(*_)
        2 * wikiGroupService.create(*_)
        2 * iamRepository.pageByProject(*_) >> projectEPage
        2 * iamRepository.roleList(*_) >> rolePage
        4 * iamRepository.pagingQueryUsersByRoleIdOnProjectLevel(*_) >> userEPage
        4 * wikiGroupService.setUserToGroup(*_)
        1 * wikiGroupService.disableProjectGroup(*_)

        when: '模拟发送消息'
        service.syncOrgAndProject(orgId)

        then: ''
    }

    def 'syncOrg'() {
        given: '定义请求数据格式'
        def orgId = 1L

        PageInfo<ProjectE> projectEPage = new PageInfo<>()
        projectEPage.setTotalPages(2)
        projectEPage.setContent(Arrays.asList(projectE))

        RoleE roleE = new RoleE()
        roleE.setId(1L)
        PageInfo<RoleE> rolePage = new PageInfo<>()
        rolePage.setContent(Arrays.asList(roleE))

        PageInfo<UserE> userEPage = new PageInfo<>()
        userEPage.setTotalPages(2)
        userEPage.setContent(Arrays.asList(userE))

        List<WikiSpaceE> wikiSpaceEList = new ArrayList<>()
        WikiSpaceE wikiSpaceE = new WikiSpaceE()
        wikiSpaceE.setId(1)
        wikiSpaceE.setStatus("failed")
        wikiSpaceE.setPath("O-用户测试组织5b3")
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
        wikiSpaceE.setPath("O-用户测试组织5b3")
        wikiSpaceList.add(wikiSpaceE)

        PageInfo<ProjectE> page = new PageInfo<>()
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

        and: 'Mock'
        1 * iWikiSpaceWebHomeService.updateWikiSpaceResource()

        when: '模拟发送消息'
        service.updateWikiPage()

        then: ''
    }
}

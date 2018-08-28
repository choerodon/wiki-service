package io.choerodon.wiki.app.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.wiki.api.dto.GroupMemberDTO
import io.choerodon.wiki.domain.application.entity.ProjectE
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE
import io.choerodon.wiki.domain.application.entity.iam.UserE
import io.choerodon.wiki.domain.application.repository.IamRepository
import io.choerodon.wiki.domain.service.IWikiClassService
import io.choerodon.wiki.domain.service.IWikiGroupService
import io.choerodon.wiki.domain.service.IWikiUserService
import spock.lang.Specification

class WikiGroupServiceImplSpec extends Specification {

    IWikiGroupService iWikiGroupService;
    IWikiUserService iWikiUserService;
    IamRepository iamRepository;
    IWikiClassService iWikiClassService;
    WikiGroupServiceImpl wikiGroupService;

    def setup() {
        iWikiGroupService = Mock(IWikiGroupService);
        iWikiUserService = Mock(IWikiUserService);
        iamRepository = Mock(IamRepository);
        iWikiClassService = Mock(IWikiClassService);
        wikiGroupService = new WikiGroupServiceImpl(
                iWikiGroupService,
                iWikiUserService,
                iamRepository,
                iWikiClassService
        )
    }

    def "disableOrganizationGroup"() {
        when:
        wikiGroupService.disableOrganizationGroup(1L, "testUserName");
        then:
        1 * iamRepository.queryOrganizationById(_) >> null
        def exception = thrown(CommonException)
        exception.message == "error.query.organization"
    }

    def "disableProjectGroup"() {
        when:
        wikiGroupService.disableProjectGroup(1L, "testUserName")
        then:
        1 * iamRepository.queryIamProject(_) >> null
        def e = thrown(CommonException)
        e.message == "error.query.project"
    }

    def "enableOrganizationGroup"() {
        when:
        wikiGroupService.enableOrganizationGroup(1L, "testUserName")
        then:
        1 * iamRepository.queryOrganizationById(_) >> null
        def e = thrown(CommonException)
        e.message == "error.query.organization"
    }

    def "enableProjectGroup"() {
        when:
        wikiGroupService.enableProjectGroup(1L, "testUserName")
        then:
        1 * iamRepository.queryIamProject(_) >> null
        def e = thrown(CommonException)
        e.message == "error.query.project"
    }

    def "setUserToGroup"() {
        when:
        wikiGroupService.setUserToGroup("testGroupName", 1L, "testUserName")
        then:
        1 * iamRepository.queryUserById(_) >> new UserE()
        def e = thrown(CommonException)
        e.message == "error.query.user"
    }

    def "getGroupName"() {
        given:
        GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
        List<String> roleLabels = new ArrayList<>();
        roleLabels.add("roleLabel1")
        groupMemberDTO.setRoleLabels(roleLabels)
        when:
        wikiGroupService.getGroupName(groupMemberDTO, "testUsername")
        then: ''
    }

    def "getGroupName1"() {
        given:
        GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
        List<String> roleLabels = new ArrayList<>();
        roleLabels.add("project.wiki.user")
        groupMemberDTO.setRoleLabels(roleLabels)
        when:
        wikiGroupService.getGroupName(groupMemberDTO, "testUsername")
        then: ''
    }

    def "getGroupNameBuffer"() {
        given:
        GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
        groupMemberDTO.setResourceType("project")
        groupMemberDTO.setResourceId(1L)

        OrganizationE organizationE = new OrganizationE();
        organizationE.setId(1L)

        ProjectE projectE = new ProjectE();
        projectE.setOrganization(organizationE)
        when:
        wikiGroupService.getGroupNameBuffer(groupMemberDTO, "testUsername", "testType")
        then:
        1 * iamRepository.queryIamProject(_) >> projectE
        1 * iamRepository.queryOrganizationById(_) >> new OrganizationE()
        1 * iWikiUserService.checkDocExsist(_, _) >> true
    }

}

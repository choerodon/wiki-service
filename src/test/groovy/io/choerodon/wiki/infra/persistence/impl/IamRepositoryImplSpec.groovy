package io.choerodon.wiki.infra.persistence.impl

import io.choerodon.core.domain.Page
import io.choerodon.core.exception.CommonException
import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO
import io.choerodon.wiki.infra.dataobject.iam.RoleDO
import io.choerodon.wiki.infra.dataobject.iam.UserDO
import io.choerodon.wiki.infra.feign.IamServiceClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by Zenger on 2018/7/25.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class IamRepositoryImplSpec extends Specification {

    IamServiceClient iamServiceClient
    IamRepositoryImpl service

    void setup() {
        iamServiceClient = Mock(IamServiceClient)
        service = new IamRepositoryImpl(iamServiceClient)
    }

    def 'pageByOrganization'() {
        given: '自定义数据'
        Page<OrganizationDO> page = new Page<>()
        page.setTotalPages(1)
        OrganizationDO organizationDO = new OrganizationDO()
        organizationDO.setId(1)
        page.setContent(Arrays.asList(organizationDO))
        ResponseEntity<Page<ProjectDO>> pageResponseEntity = new ResponseEntity<>(page, HttpStatus.OK)

        when: ''
        service.pageByOrganization(0, 10)

        then: ''
        1 * iamServiceClient.pageByOrganization(*_) >> pageResponseEntity
    }

    def 'roleList'() {
        given: '自定义数据'
        Page<RoleDO> page = new Page<>()
        page.setTotalPages(1)
        RoleDO roleDO = new RoleDO()
        roleDO.setId(1)
        page.setContent(Arrays.asList(roleDO))
        ResponseEntity<Page<RoleDO>> responseEntity = new ResponseEntity<>(page, HttpStatus.OK)

        when: ''
        service.roleList("1")

        then: ''
        1 * iamServiceClient.roleList(*_) >> responseEntity
    }

    def 'pagingQueryUsersByRoleIdOnProjectLevel'() {
        given: '自定义数据'
        Page<UserDO> page = new Page<>()
        page.setTotalPages(1)
        UserDO userDO = new UserDO()
        userDO.setId(1)
        page.setContent(Arrays.asList(userDO))
        ResponseEntity<Page<UserDO>> responseEntity = new ResponseEntity<>(page, HttpStatus.OK)

        when: ''
        service.pagingQueryUsersByRoleIdOnProjectLevel(1, 2, 3, 4)

        then: ''
        1 * iamServiceClient.pagingQueryUsersByRoleIdOnProjectLevel(*_) >> responseEntity
    }

    def 'pagingQueryUsersByRoleIdOnOrganizationLevel'() {
        given: '自定义数据'
        Page<UserDO> page = new Page<>()
        page.setTotalPages(1)
        UserDO userDO = new UserDO()
        userDO.setId(1)
        page.setContent(Arrays.asList(userDO))
        ResponseEntity<Page<UserDO>> responseEntity = new ResponseEntity<>(page, HttpStatus.OK)

        when: ''
        service.pagingQueryUsersByRoleIdOnOrganizationLevel(1, 2, 3, 4)

        then: ''
        1 * iamServiceClient.pagingQueryUsersByRoleIdOnOrganizationLevel(*_) >> responseEntity
    }

    def "queryByLoginNameFalied"() {
        given: '自定义数据'
        ResponseEntity<UserDO> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when: ''
        service.queryByLoginName("测试名字")
        then: ''
        1 * iamServiceClient.queryByLoginName(_) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.user.get"
    }

    def "queryOrganizationByIdFalied"() {
        given: '自定义数据'
        ResponseEntity<OrganizationDO> organization = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when: ''
        service.queryOrganizationById(1L)
        then: ''
        1 * iamServiceClient.queryOrganizationById(_) >> organization
        def e = thrown(CommonException)
        e.message == "error.organization.get"
    }

    def "queryIamProjectFalied"() {
        given: '自定义数据'
        ResponseEntity<ProjectDO> projectDO = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when: ''
        service.queryIamProject(1L)
        then: ''
        1 * iamServiceClient.queryIamProject(_) >> projectDO
        def e = thrown(CommonException)
        e.message == "error.project.get"
    }

    def "queryUserByIdFaliedUserGet"() {
        given: '自定义数据'
        ResponseEntity<List<UserDO>> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when: ''
        service.queryUserById(1L)
        then: ''
        1 * iamServiceClient.queryUsersByIds(_) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.user.get"
    }

    def "queryUserByIdFaliedUserQuery"() {
        given: '自定义数据'
        ResponseEntity<List<UserDO>> responseEntity = new ResponseEntity<>(HttpStatus.OK)
        when: ''
        service.queryUserById(1L)
        then: ''
        1 * iamServiceClient.queryUsersByIds(_) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.user.query"
    }

    def "pageByOrganizationFailedGet"() {
        given: '自定义数据'
        ResponseEntity<Page<OrganizationDO>> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when:
        service.pageByOrganization(3, 4)
        then:
        1 * iamServiceClient.pageByOrganization(_, _) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.organization.get"
    }

    def "pageByOrganizationFailedQuery"() {
        given: '自定义数据'
        ResponseEntity<Page<OrganizationDO>> responseEntity = new ResponseEntity<>(HttpStatus.OK)
        when:
        service.pageByOrganization(3, 4)
        then:
        1 * iamServiceClient.pageByOrganization(_, _) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.organization.get"
    }

    def "pageByProjectFailedGet1"() {
        given: '自定义数据'
        ResponseEntity<Page<ProjectDO>> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when:
        service.pageByProject(1L, 3, 4)
        then:
        1 * iamServiceClient.pageByProject(_, _, _) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.project.get"
    }

    def "pageByProjectFailedGet2"() {
        given: '自定义数据'
        ResponseEntity<Page<ProjectDO>> responseEntity = new ResponseEntity<>(HttpStatus.OK)
        when:
        service.pageByProject(1L, 3, 4)
        then:
        1 * iamServiceClient.pageByProject(_, _, _) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.project.get"
    }

    def "roleListFailedGet1"() {
        given: '自定义数据'
        ResponseEntity<Page<RoleDO>> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when:
        service.roleList("testStringCode")
        then:
        1 * iamServiceClient.roleList(_) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.organization.get"
    }

    def "roleListFailedGet2"() {
        given: '自定义数据'
        ResponseEntity<Page<RoleDO>> responseEntity = new ResponseEntity<>(HttpStatus.OK)
        when:
        service.roleList("testStringCode")
        then:
        1 * iamServiceClient.roleList(_) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.organization.get"
    }

    def "pagingQueryUsersByRoleIdOnProjectLevelFailed"() {
        given: '自定义数据'
        ResponseEntity<Page<UserDO>> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when:
        service.pagingQueryUsersByRoleIdOnProjectLevel(1L, 1L, 1, 2)
        then:
        1 * iamServiceClient.pagingQueryUsersByRoleIdOnProjectLevel(_, _, _, _, _) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.organization.get"
    }

    def "pagingQueryUsersByRoleIdOnOrganizationLevelFailed"() {
        given: '自定义数据'
        ResponseEntity<Page<UserDO>> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        when:
        service.pagingQueryUsersByRoleIdOnOrganizationLevel(1L, 1L, 1, 2)
        then:
        1 * iamServiceClient.pagingQueryUsersByRoleIdOnOrganizationLevel(_, _, _, _, _) >> responseEntity
        def e = thrown(CommonException)
        e.message == "error.organization.get"
    }
}

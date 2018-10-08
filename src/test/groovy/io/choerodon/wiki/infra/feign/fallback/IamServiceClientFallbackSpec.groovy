package io.choerodon.wiki.infra.feign.fallback

import io.choerodon.core.exception.FeignException
import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.domain.application.valueobject.RoleAssignmentSearch
import io.choerodon.wiki.infra.dataobject.iam.RoleSearchDO
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Zenger on 2018/10/8.
 */
@Import(IntegrationTestConfiguration)
class IamServiceClientFallbackSpec extends Specification {

    @Shared
    IamServiceClientFallback iamServiceClientFallback

    void setup() {
        iamServiceClientFallback = new IamServiceClientFallback()
    }

    def 'queryByLoginName'() {
        when: ''
        iamServiceClientFallback.queryByLoginName("1")

        then: ''
        def e = thrown(FeignException)
        e.message == "error.user.get"
    }

    def 'queryOrganizationById'() {
        when: ''
        iamServiceClientFallback.queryOrganizationById(1L)

        then: ''
        def e = thrown(FeignException)
        e.message == "error.organization.get"
    }

    def 'queryIamProject'() {
        when: ''
        iamServiceClientFallback.queryIamProject(1L)

        then: ''
        def e = thrown(FeignException)
        e.message == "error.project.get"
    }

    def 'queryUsersByIds'() {
        when: ''
        iamServiceClientFallback.queryUsersByIds(Arrays.asList(1L))

        then: ''
        def e = thrown(FeignException)
        e.message == "error.user.get"
    }

    def 'pageByOrganization'() {
        when: ''
        iamServiceClientFallback.pageByOrganization(0, 400)

        then: ''
        def e = thrown(FeignException)
        e.message == "error.organization.get"
    }

    def 'queryOrgById'() {
        when: ''
        iamServiceClientFallback.queryOrgById(1L)

        then: ''
        def e = thrown(FeignException)
        e.message == "error.organization.get"
    }

    def 'pageByProject'() {
        when: ''
        iamServiceClientFallback.pageByProject(1L, 0, 400)

        then: ''
        def e = thrown(FeignException)
        e.message == "error.project.get"
    }

    def 'roleList'() {
        when: ''
        iamServiceClientFallback.roleList(new RoleSearchDO())

        then: ''
        def e = thrown(FeignException)
        e.message == "error.role.get"
    }

    def 'pagingQueryUsersByRoleIdOnProjectLevel'() {
        when: ''
        iamServiceClientFallback.pagingQueryUsersByRoleIdOnProjectLevel(1L, 1l, 0, 400, new RoleAssignmentSearch())

        then: ''
        def e = thrown(FeignException)
        e.message == "error.user.get"
    }

    def 'pagingQueryUsersByRoleIdOnOrganizationLevel'() {
        when: ''
        iamServiceClientFallback.pagingQueryUsersByRoleIdOnOrganizationLevel(1L, 1l, 0, 400, new RoleAssignmentSearch())

        then: ''
        def e = thrown(FeignException)
        e.message == "error.user.get"
    }
}

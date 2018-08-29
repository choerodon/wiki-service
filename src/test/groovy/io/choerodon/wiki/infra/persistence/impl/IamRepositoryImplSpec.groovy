package io.choerodon.wiki.infra.persistence.impl

import io.choerodon.core.convertor.ConvertPageHelper
import io.choerodon.core.domain.Page
import io.choerodon.wiki.infra.dataobject.iam.OrganizationDO
import io.choerodon.wiki.infra.dataobject.iam.ProjectDO
import io.choerodon.wiki.infra.dataobject.iam.RoleDO
import io.choerodon.wiki.infra.dataobject.iam.UserDO
import io.choerodon.wiki.infra.feign.IamServiceClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by Zenger on 2018/7/25.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class IamRepositoryImplSpec extends Specification {

    IamServiceClient iamServiceClient
    IamRepositoryImpl service

    void setup(){
        iamServiceClient=Mock(IamServiceClient)
        service = new IamRepositoryImpl(iamServiceClient)
    }

    def 'pageByOrganization'() {
        given:'自定义数据'
        Page<OrganizationDO> page = new Page<>()
        page.setTotalPages(1)
        OrganizationDO organizationDO = new OrganizationDO()
        organizationDO.setId(1)
        page.setContent(Arrays.asList(organizationDO))
        ResponseEntity<Page<ProjectDO>> pageResponseEntity = new ResponseEntity<>(page,HttpStatus.OK)

        when:''
        service.pageByOrganization(0,10)

        then:''
        1 * iamServiceClient.pageByOrganization(*_) >>  pageResponseEntity
    }

    def 'roleList'() {
        given:'自定义数据'
        Page<RoleDO> page = new Page<>()
        page.setTotalPages(1)
        RoleDO roleDO = new RoleDO()
        roleDO.setId(1)
        page.setContent(Arrays.asList(roleDO))
        ResponseEntity<Page<RoleDO>> responseEntity = new ResponseEntity<>(page,HttpStatus.OK)

        when:''
        service.roleList("1")

        then:''
        1 * iamServiceClient.roleList(*_) >>  responseEntity
    }

    def 'pagingQueryUsersByRoleIdOnProjectLevel'() {
        given:'自定义数据'
        Page<UserDO> page = new Page<>()
        page.setTotalPages(1)
        UserDO userDO = new UserDO()
        userDO.setId(1)
        page.setContent(Arrays.asList(userDO))
        ResponseEntity<Page<UserDO>> responseEntity = new ResponseEntity<>(page,HttpStatus.OK)

        when:''
        service.pagingQueryUsersByRoleIdOnProjectLevel(1,2,3,4)

        then:''
        1 * iamServiceClient.pagingQueryUsersByRoleIdOnProjectLevel(*_) >>  responseEntity
    }

    def 'pagingQueryUsersByRoleIdOnOrganizationLevel'() {
        given:'自定义数据'
        Page<UserDO> page = new Page<>()
        page.setTotalPages(1)
        UserDO userDO = new UserDO()
        userDO.setId(1)
        page.setContent(Arrays.asList(userDO))
        ResponseEntity<Page<UserDO>> responseEntity = new ResponseEntity<>(page,HttpStatus.OK)

        when:''
        service.pagingQueryUsersByRoleIdOnOrganizationLevel(1,2,3,4)

        then:''
        1 * iamServiceClient.pagingQueryUsersByRoleIdOnOrganizationLevel(*_) >>  responseEntity
    }
}

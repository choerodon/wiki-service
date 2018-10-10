package io.choerodon.wiki.infra.common.exception

import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.infra.common.TypeUtil
import org.springframework.context.annotation.Import
import spock.lang.Specification

/**
 * Created by Zenger on 2018/10/8.
 */
@Import(IntegrationTestConfiguration)
class NetworkRequestStatusCodeExceptionSpec extends Specification {

    def 'networkRequestStatusCodeExceptionSpec'() {
        when: ''
        new NetworkRequestStatusCodeException("500","error.network.connect")
        new NetworkRequestStatusCodeException("500",new Throwable() ,"error.network.connect")
        new NetworkRequestStatusCodeException("500",new Throwable())
        new NetworkRequestStatusCodeException(new Throwable() ,"error.network.connect").getParameters()
        new NetworkRequestStatusCodeException(new Throwable() ,"error.network.connect").getCode()
        new NetworkRequestStatusCodeException(new Throwable() ,"error.network.connect").toMap()

        then: ''
    }
}

package io.choerodon.wiki.infra.common

import io.choerodon.wiki.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import spock.lang.Specification

/**
 * Created by Zenger on 2018/10/8.
 */
@Import(IntegrationTestConfiguration)
class TypeUtilSpec extends Specification {


    def 'objToString'() {
        when: ''
        TypeUtil.objToString("1")

        then: ''
    }

    def 'objToStringnull'() {
        when: ''
        TypeUtil.objToString(null)

        then: ''
    }

    def 'objToInteger'() {
        when: ''
        TypeUtil.objToInteger("1")

        then: ''
    }

    def 'objToIntegernull'() {
        when: ''
        TypeUtil.objToInteger(null)

        then: ''
    }

    def 'objToLong'() {
        when: ''
        TypeUtil.objToLong("1")

        then: ''
    }

    def 'objToLongnull'() {
        when: ''
        TypeUtil.objToLong(null)

        then: ''
    }

    def 'objTodouble'() {
        when: ''
        TypeUtil.objTodouble("1")

        then: ''
    }

    def 'objTodoublenull'() {
        when: ''
        TypeUtil.objTodouble(null)

        then: ''
    }

    def 'objToInt'() {
        when: ''
        TypeUtil.objToInt("1")

        then: ''
    }

    def 'objToIntnull'() {
        when: ''
        TypeUtil.objToInt(null)

        then: ''
    }

    def 'objToBoolean'() {
        when: ''
        TypeUtil.objToBoolean("true")

        then: ''
    }

    def 'objToBooleannull'() {
        when: ''
        TypeUtil.objToBoolean(null)

        then: ''
    }

    def 'cast'() {
        when: ''
        TypeUtil.cast(new ArrayList(5).add(1))

        then: ''
    }

    def 'castnull'() {
        when: ''
        TypeUtil.cast(null)

        then: ''
    }
}

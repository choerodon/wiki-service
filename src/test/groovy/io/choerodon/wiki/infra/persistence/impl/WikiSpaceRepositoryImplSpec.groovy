package io.choerodon.wiki.infra.persistence.impl

import io.choerodon.core.domain.Page
import io.choerodon.core.exception.CommonException
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.wiki.IntegrationTestConfiguration
import io.choerodon.wiki.domain.application.entity.WikiSpaceE
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO
import io.choerodon.wiki.infra.feign.IamServiceClient
import io.choerodon.wiki.infra.mapper.WikiSpaceMapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by lc on 2018/8/30.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class WikiSpaceRepositoryImplSpec extends Specification {

    WikiSpaceMapper wikiSpaceMapper;
    WikiSpaceRepositoryImpl service;

    @Shared
    def WikiSpaceE wikiSpaceE;

    void setup() {
        wikiSpaceMapper = Mock(WikiSpaceMapper)
        service = new WikiSpaceRepositoryImpl(wikiSpaceMapper)

        wikiSpaceE = new WikiSpaceE();
        wikiSpaceE.setName("测试名字")
        wikiSpaceE.setId(1L)
    }

    def "insertFailed"() {
        when:
        service.insert(wikiSpaceE)
        then:
        def e = thrown(CommonException)
        e.message == "error.space.insert";
    }

    def "insertIfNotExistNotNull"() {
        when:
        service.insertIfNotExist(wikiSpaceE)
        then:
        1 * wikiSpaceMapper.selectOne(_) >> new WikiSpaceDO()
    }

    def "insertIfNotExistFailed"() {
        when:
        service.insertIfNotExist(wikiSpaceE)
        then:
        def e = thrown(CommonException)
        e.message == "error.space.insert"
    }

    def "listWikiSpaceByPage"(){
        given:'自定义数据'
        PageRequest pageRequest = new PageRequest(1,2);
        String searchParam = "{\"searchParam\":{},\"param\":\"\"}"
        List<WikiSpaceDO> listWikiSpaceByPage = new Page<>();
        and:'Mock'
        1 * wikiSpaceMapper.listWikiSpaceByPage(_,_,_,_) >> listWikiSpaceByPage
        when:
        service.listWikiSpaceByPage(1L,"testType",pageRequest,searchParam)
        then:''
    }

    def "listWikiSpaceByPageFailed"(){
        given:'自定义数据'
        PageRequest pageRequest = new PageRequest(1,2);
        when:
        service.listWikiSpaceByPage(1L,"testType",pageRequest,"testValue")
        then:
        def e = thrown(CommonException)
        e.message == "error.space.list.query"
    }

    def "checkNameFailed"() {
        when:
        service.checkName(1L, "testName", "testType")
        then:
        1 * wikiSpaceMapper.checkName(_, _, _) >> 1
        def e = thrown(CommonException)
        e.message == "error.space.name.check"
    }

    def "updateFailed"(){
        when:
        service.update(wikiSpaceE)
        then:
        def e = thrown(CommonException)
        e.message == "error.space.update"
    }

    def "getWikiSpaceByType"(){
        when:
        service.getWikiSpaceByType("testresourceType")
        then:
        1 * wikiSpaceMapper.select(_) >> new Page<WikiSpaceE>()
    }
}

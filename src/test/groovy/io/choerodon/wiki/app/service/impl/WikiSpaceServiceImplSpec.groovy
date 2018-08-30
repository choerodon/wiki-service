package io.choerodon.wiki.app.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.wiki.app.service.WikiSpaceAsynService
import io.choerodon.wiki.domain.application.entity.WikiSpaceE
import io.choerodon.wiki.domain.application.repository.IamRepository
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by lc on 2018/8/30.
 */
class WikiSpaceServiceImplSpec extends Specification {

    WikiSpaceRepository wikiSpaceRepository;
    WikiSpaceAsynService wikiSpaceAsynService;
    IWikiSpaceWebHomeService iWikiSpaceWebHomeService;
    IamRepository iamRepository;
    WikiSpaceServiceImpl wikiSpaceService;

    @Shared
    def WikiSpaceE wikiSpaceE;

    void setup() {
        wikiSpaceRepository=Mock(WikiSpaceRepository)
        wikiSpaceAsynService=Mock(WikiSpaceAsynService)
        iWikiSpaceWebHomeService=Mock(IWikiSpaceWebHomeService)
        iamRepository=Mock(IamRepository)
        wikiSpaceService = new WikiSpaceServiceImpl(
                wikiSpaceRepository,
                wikiSpaceAsynService,
                iamRepository,
                iWikiSpaceWebHomeService)

        wikiSpaceE = new WikiSpaceE();
        wikiSpaceE.setId(1L);
        wikiSpaceE.setName("测试名字");

    }


    def "delete"(){
        given:
        wikiSpaceE.setResourceId(1L);
        and:'Mock'
        1 * wikiSpaceRepository.selectById(_) >> wikiSpaceE
        when:
        wikiSpaceService.delete(11L,1L)
        then:
        def e = thrown(CommonException)
        e.message == "error.resourceId.equal"
    }

   def "checkCodeDelete"(){
       when:
       wikiSpaceService.checkCodeDelete(403,1L)
       then:
       1 * wikiSpaceRepository.selectById(_) >> wikiSpaceE
       1 * wikiSpaceRepository.update(wikiSpaceE)
   }

}

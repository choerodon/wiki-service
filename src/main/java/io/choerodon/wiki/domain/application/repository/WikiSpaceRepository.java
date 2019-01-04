package io.choerodon.wiki.domain.application.repository;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface WikiSpaceRepository {

    List<WikiSpaceE> getWikiSpaceList(Long resourceId, String resourceType);

    WikiSpaceE insert(WikiSpaceE wikiSpaceE);

    WikiSpaceE insertIfNotExist(WikiSpaceE wikiSpaceE);

    Page<WikiSpaceE> listWikiSpaceByPage(Long resourceId, String type,
                                         PageRequest pageRequest, String searchParam);

    WikiSpaceE selectById(Long id);

    Boolean checkName(Long resourceId, String name, String type);

    WikiSpaceE update(WikiSpaceE wikiSpaceE);

    WikiSpaceE updateSelective(WikiSpaceE wikiSpaceE);

    List<WikiSpaceE> getWikiSpaceByType(String resourceType);

    WikiSpaceE selectOne(Long resourceId, String name, String type);

    List<WikiSpaceE> select(String resourceType, String name);

    WikiSpaceE selectOrgOrPro(Long resourceId, String type);

    List<WikiSpaceE> selectSubSpaces(Long resourceId, String type);
}

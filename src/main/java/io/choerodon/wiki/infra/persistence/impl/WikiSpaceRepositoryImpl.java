package io.choerodon.wiki.infra.persistence.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;
import io.choerodon.wiki.infra.mapper.WikiSpaceMapper;

/**
 * Created by Zenger on 2018/7/3.
 */
@Service
public class WikiSpaceRepositoryImpl implements WikiSpaceRepository {

    private WikiSpaceMapper wikiSpaceMapper;

    public WikiSpaceRepositoryImpl(WikiSpaceMapper wikiSpaceMapper) {
        this.wikiSpaceMapper = wikiSpaceMapper;
    }

    @Override
    public List<WikiSpaceE> getWikiSpaceList(Long resourceId, String resourceType) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceId(resourceId);
        wikiSpaceDO.setResourceType(resourceType);
        return ConvertHelper.convertList(wikiSpaceMapper.select(wikiSpaceDO), WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE insert(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class);
        if (wikiSpaceMapper.insert(wikiSpaceDO) != 1) {
            throw new CommonException("error.space.insert");
        }
        return ConvertHelper.convert(wikiSpaceDO, WikiSpaceE.class);
    }
}

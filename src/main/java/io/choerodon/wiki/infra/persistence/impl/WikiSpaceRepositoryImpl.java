package io.choerodon.wiki.infra.persistence.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.infra.common.TypeUtil;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;
import io.choerodon.wiki.infra.mapper.WikiSpaceMapper;

/**
 * Created by Zenger on 2018/7/3.
 */
@Service
public class WikiSpaceRepositoryImpl implements WikiSpaceRepository {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(WikiSpaceRepositoryImpl.class);

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
    public Boolean deleteSpaceById(Long id) {
        if (wikiSpaceMapper.deleteByPrimaryKey(id) != 1) {
            return false;
        }
        return true;
    }

    @Override
    public WikiSpaceE insert(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class);
        if (wikiSpaceMapper.insert(wikiSpaceDO) != 1) {
            throw new CommonException("error.space.insert");
        }
        return ConvertHelper.convert(wikiSpaceDO, WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE insertIfNotExist(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class);
        WikiSpaceDO wikiSpaceDOCheck = new WikiSpaceDO();
        wikiSpaceDOCheck.setResourceId(wikiSpaceDO.getResourceId());
        wikiSpaceDOCheck.setResourceType(wikiSpaceDO.getResourceType());
        WikiSpaceDO wikiSpaceDOCheck2 = wikiSpaceMapper.selectOne(wikiSpaceDOCheck);
        if (wikiSpaceDOCheck2 != null) {
            return ConvertHelper.convert(wikiSpaceDOCheck2, WikiSpaceE.class);
        }
        if (wikiSpaceMapper.insert(wikiSpaceDO) != 1) {
            throw new CommonException("error.space.insert");
        }
        return ConvertHelper.convert(wikiSpaceDO, WikiSpaceE.class);
    }

    @Override
    public Page<WikiSpaceE> listWikiSpaceByPage(Long resourceId, String type,
                                                PageRequest pageRequest, String searchParam) {
        Page<WikiSpaceDO> wikiSpaceDOPage = null;
        try {
            if (!StringUtils.isEmpty(searchParam)) {
                Map<String, Object> searchParamMap = objectMapper.readValue(searchParam, Map.class);
                wikiSpaceDOPage = PageHelper.doPageAndSort(
                        pageRequest, () -> wikiSpaceMapper.listWikiSpaceByPage(
                                resourceId,
                                type,
                                TypeUtil.cast(searchParamMap.get(TypeUtil.SEARCH_PARAM)),
                                TypeUtil.cast(searchParamMap.get(TypeUtil.PARAM))));
            } else {
                wikiSpaceDOPage = PageHelper.doPageAndSort(
                        pageRequest, () -> wikiSpaceMapper.listWikiSpaceByPage(resourceId,
                                type,
                                null,
                                null));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return ConvertPageHelper.convertPage(wikiSpaceDOPage, WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE selectById(Long id) {
        return ConvertHelper.convert(wikiSpaceMapper.selectByPrimaryKey(id), WikiSpaceE.class);
    }

    @Override
    public Boolean checkName(Long projectId, String name, String type) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceId(projectId);
        wikiSpaceDO.setResourceType(type);
        wikiSpaceDO.setName(name);
        int selectCount = wikiSpaceMapper.selectCount(wikiSpaceDO);
        if (selectCount > 0) {
            throw new CommonException("error.space.name.check");
        }
        return true;
    }

    @Override
    public WikiSpaceE update(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class);
        if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDO) != 1) {
            throw new CommonException("error.space.insert");
        }
        return ConvertHelper.convert(wikiSpaceDO, WikiSpaceE.class);
    }
}

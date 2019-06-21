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
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import com.github.pagehelper.PageHelper;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.infra.common.PageUtil;
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
    public WikiSpaceE insert(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class);
        if (wikiSpaceMapper.insert(wikiSpaceDO) != 1) {
            throw new CommonException("error.space.insert");
        }
        return ConvertHelper.convert(wikiSpaceDO, WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE insertIfNotExist(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceId(wikiSpaceE.getResourceId());
        wikiSpaceDO.setResourceType(wikiSpaceE.getResourceType());
        wikiSpaceDO.setName(wikiSpaceE.getName());
        WikiSpaceDO wikiSpaceDOCheck2 = wikiSpaceMapper.selectOne(wikiSpaceDO);
        if (wikiSpaceDOCheck2 != null) {
            if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDOCheck2) != 1) {
                throw new CommonException("error.space.update");
            }
            return ConvertHelper.convert(wikiSpaceDOCheck2, WikiSpaceE.class);
        } else {
            WikiSpaceDO wikiSpaceDO1 = ConvertHelper.convert(wikiSpaceE,WikiSpaceDO.class);
            if (wikiSpaceMapper.insert(wikiSpaceDO1) != 1) {
                throw new CommonException("error.space.insert");
            }
            return ConvertHelper.convert(wikiSpaceDO1, WikiSpaceE.class);
        }
    }

    @Override
    public PageInfo<WikiSpaceE> listWikiSpaceByPage(Long resourceId, String type,
                                                PageRequest pageRequest, String searchParam) {
        PageInfo<WikiSpaceDO> wikiSpaceDOPage = null;
        try {
            if (!StringUtils.isEmpty(searchParam)) {
                Map<String, Object> searchParamMap = objectMapper.readValue(searchParam, Map.class);
                wikiSpaceDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                        PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> wikiSpaceMapper.listWikiSpaceByPage(
                                resourceId,
                                type,
                                TypeUtil.cast(searchParamMap.get(TypeUtil.SEARCH_PARAM)),
                                TypeUtil.cast(searchParamMap.get(TypeUtil.PARAM))));
            } else {
                wikiSpaceDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                        PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> wikiSpaceMapper.listWikiSpaceByPage(resourceId,
                                type,
                                null,
                                null));
            }
        } catch (IOException e) {
            throw new CommonException("error.space.list.query");
        }

        return ConvertPageHelper.convertPageInfo(wikiSpaceDOPage, WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE selectById(Long id) {
        return ConvertHelper.convert(wikiSpaceMapper.selectByPrimaryKey(id), WikiSpaceE.class);
    }

    @Override
    public Boolean checkName(Long resourceId, String name, String type) {
        int selectCount = wikiSpaceMapper.checkName(resourceId, type, name);
        if (selectCount > 0) {
            throw new CommonException("error.space.name.check");
        }
        return true;
    }

    @Override
    public WikiSpaceE update(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class);
        if (wikiSpaceMapper.updateByPrimaryKey(wikiSpaceDO) != 1) {
            throw new CommonException("error.space.update");
        }
        return ConvertHelper.convert(wikiSpaceDO, WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE updateSelective(WikiSpaceE wikiSpaceE) {
        WikiSpaceDO wikiSpaceDO = ConvertHelper.convert(wikiSpaceE, WikiSpaceDO.class);
        if (wikiSpaceMapper.updateByPrimaryKeySelective(wikiSpaceDO) != 1) {
            throw new CommonException("error.space.update");
        }
        return ConvertHelper.convert(wikiSpaceDO, WikiSpaceE.class);
    }

    @Override
    public List<WikiSpaceE> getWikiSpaceByType(String resourceType) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceType(resourceType);
        return ConvertHelper.convertList(wikiSpaceMapper.select(wikiSpaceDO), WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE selectOne(Long resourceId, String name, String type) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceId(resourceId);
        wikiSpaceDO.setResourceType(type);
        wikiSpaceDO.setName(name);
        return ConvertHelper.convert(wikiSpaceMapper.selectOne(wikiSpaceDO), WikiSpaceE.class);
    }

    @Override
    public List<WikiSpaceE> select(String resourceType, String name) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceType(resourceType);
        wikiSpaceDO.setName(name);
        return ConvertHelper.convertList(wikiSpaceMapper.select(wikiSpaceDO), WikiSpaceE.class);
    }

    @Override
    public WikiSpaceE selectOrgOrPro(Long resourceId, String type) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceId(resourceId);
        wikiSpaceDO.setResourceType(type);
        return ConvertHelper.convert(wikiSpaceMapper.selectOne(wikiSpaceDO), WikiSpaceE.class);
    }

    @Override
    public List<WikiSpaceE> selectSubSpaces(Long resourceId, String type) {
        WikiSpaceDO wikiSpaceDO = new WikiSpaceDO();
        wikiSpaceDO.setResourceId(resourceId);
        wikiSpaceDO.setResourceType(type);
        return ConvertHelper.convertList(wikiSpaceMapper.select(wikiSpaceDO), WikiSpaceE.class);
    }
}

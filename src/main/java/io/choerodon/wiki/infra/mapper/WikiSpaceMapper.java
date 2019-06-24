package io.choerodon.wiki.infra.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.wiki.infra.dataobject.WikiSpaceDO;

/**
 * Created by Zenger on 2018/7/2.
 */
public interface WikiSpaceMapper extends Mapper<WikiSpaceDO> {

    List<WikiSpaceDO> listWikiSpaceByPage(
            @Param("resourceId") Long resourceId,
            @Param("type") String type,
            @Param("searchParam") Map<String, Object> searchParam,
            @Param("param") String param);

    int checkName(
            @Param("resourceId") Long resourceId,
            @Param("type") String type,
            @Param("name") String name);
}

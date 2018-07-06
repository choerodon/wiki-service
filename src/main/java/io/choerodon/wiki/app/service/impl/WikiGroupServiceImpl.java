package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.api.dto.WikiUserDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.app.service.WikiUserService;
import io.choerodon.wiki.domain.application.entity.WikiGroupE;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.entity.WikiUserE;
import io.choerodon.wiki.domain.service.IWikiGroupService;
import io.choerodon.wiki.domain.service.IWikiUserService;
import io.choerodon.wiki.infra.common.FileUtil;

/**
 * Created by Ernst on 2018/7/4.
 */
@Service
public class WikiGroupServiceImpl implements WikiGroupService {

    private IWikiGroupService iWikiGroupService;

    private IWikiUserService iWikiUserService;

    public WikiGroupServiceImpl(IWikiGroupService iWikiGroupService,IWikiUserService iWikiUserService) {
        this.iWikiGroupService = iWikiGroupService;
        this.iWikiUserService = iWikiUserService;
    }

    @Override
    public Boolean create(WikiGroupDTO wikiGroupDTO) {
        Boolean flag = iWikiUserService.checkUserExsist(wikiGroupDTO.getGroupName());
        if(!flag){
            return iWikiGroupService.createGroup(wikiGroupDTO.getGroupName(),getXml());
        }
        return false;
    }

    private String getXml() {
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/group.xml");
        Map<String, String> params = new HashMap<>();
        return FileUtil.replaceReturnString(inputStream, params);
    }
}

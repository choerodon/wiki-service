package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiSpaceService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/2.
 */
@Service
public class WikiSpaceServiceImpl implements WikiSpaceService {

    private WikiSpaceRepository wikiSpaceRepository;
    private IWikiSpaceService iWikiSpaceService;

    public WikiSpaceServiceImpl(WikiSpaceRepository wikiSpaceRepository,
                                IWikiSpaceService iWikiSpaceService) {
        this.wikiSpaceRepository = wikiSpaceRepository;
        this.iWikiSpaceService = iWikiSpaceService;
    }

    @Override
    public void create(WikiSpaceDTO wikiSpaceDTO, Long organizationId, String type) {
        String path = "";
        if (WikiSpaceResourceType.ORGANIZATION_S.getResourceType().equals(type)) {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    organizationId, WikiSpaceResourceType.ORGANIZATION.getResourceType());
            if (wikiSpaceEList == null || wikiSpaceEList.isEmpty() || !wikiSpaceEList.get(0).getSynchro()) {
                throw new CommonException("error.organization.synchronized");
            }
            path = wikiSpaceEList.get(0).getPath();
        } else if (WikiSpaceResourceType.PROJECT_S.getResourceType().equals(type)) {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    organizationId, WikiSpaceResourceType.PROJECT.getResourceType());
            if (wikiSpaceEList == null || wikiSpaceEList.isEmpty() || !wikiSpaceEList.get(0).getSynchro()) {
                throw new CommonException("error.organization.synchronized");
            }
            path = wikiSpaceEList.get(0).getPath();
        }

        WikiSpaceE wikiSpaceE = new WikiSpaceE();
        wikiSpaceE.setName(wikiSpaceDTO.getName());
        wikiSpaceE.setIcon(wikiSpaceDTO.getIcon());
        wikiSpaceE.setDescription(wikiSpaceDTO.getDescribe());
        wikiSpaceE.setResourceId(organizationId);
        wikiSpaceE.setResourceType(type);
        wikiSpaceE.setSynchro(false);
        WikiSpaceResourceType wikiSpaceResourceType = WikiSpaceResourceType.forString(type);
        switch (wikiSpaceResourceType) {
            case ORGANIZATION:
                String param1 = "0-" + wikiSpaceDTO.getName();
                wikiSpaceE.setPath(param1);
                wikiSpaceRepository.insert(wikiSpaceE);
                iWikiSpaceService.createSpace1(wikiSpaceE,param1, getXml(wikiSpaceE));
                break;
            case PROJECT:
                wikiSpaceE.setPath("O-组织" + "/" + "P-" + wikiSpaceDTO.getName());
                break;
            case ORGANIZATION_S:
                wikiSpaceE.setPath(path + "/" + wikiSpaceDTO.getName());
                break;
            case PROJECT_S:
                wikiSpaceE.setPath(path + "/" + wikiSpaceDTO.getName());
                break;
        }
    }

    private String getXml(WikiSpaceE wikiSpaceE){
        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome.xml");
        Map<String, String> params = new HashMap<>();
        params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
        params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
        params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName());
        params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
        params.put("{{ DESCRIPTION }}", wikiSpaceE.getDescription());
        return  FileUtil.replaceReturnString(inputStream,params);
    }
}

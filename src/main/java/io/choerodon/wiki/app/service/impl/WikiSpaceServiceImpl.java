package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;
import io.choerodon.wiki.app.service.WikiSpaceAsynService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/2.
 */
@Service
public class WikiSpaceServiceImpl implements WikiSpaceService {

    private static final String LOCATION = "bin/view/";

    private WikiSpaceRepository wikiSpaceRepository;
    private WikiSpaceAsynService wikiSpaceAsynService;
    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService;

    @Value("${wiki.url}")
    private String wikiUrl;

    public WikiSpaceServiceImpl(WikiSpaceRepository wikiSpaceRepository,
                                WikiSpaceAsynService wikiSpaceAsynService,
                                IWikiSpaceWebHomeService iWikiSpaceWebHomeService) {
        this.wikiSpaceRepository = wikiSpaceRepository;
        this.wikiSpaceAsynService = wikiSpaceAsynService;
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
    }

    @Override
    public Boolean checkName(Long projectId, String name,String type) {
        return wikiSpaceRepository.checkName(projectId,name,type);
    }

    @Override
    public void create(WikiSpaceDTO wikiSpaceDTO, Long resourceId, String type) {
        checkName(resourceId,wikiSpaceDTO.getName(),type);
        String path = getPath(resourceId, type);

        WikiSpaceE wikiSpaceE = new WikiSpaceE();
        wikiSpaceE.setIcon(wikiSpaceDTO.getIcon());
        if (wikiSpaceDTO.getDescription() != null) {
            wikiSpaceE.setDescription(wikiSpaceDTO.getDescription());
        }
        wikiSpaceE.setResourceId(resourceId);
        wikiSpaceE.setResourceType(type);
        wikiSpaceE.setSynchro(false);

        WikiSpaceResourceType wikiSpaceResourceType = WikiSpaceResourceType.forString(type);
        switch (wikiSpaceResourceType) {
            case ORGANIZATION:
                createOrgSpace(wikiSpaceE, wikiSpaceDTO);
                break;
            case PROJECT:
                createProjectSpace(wikiSpaceE, wikiSpaceDTO);
                break;
            case ORGANIZATION_S:
                createOrgUnderSpace(wikiSpaceE, wikiSpaceDTO, path);
                break;
            case PROJECT_S:
                createProjectUnderSpace(wikiSpaceE, wikiSpaceDTO, path);
                break;
            default:
                break;
        }
    }

    @Override
    public Page<WikiSpaceResponseDTO> listWikiSpaceByPage(Long resourceId, String type,
                                                          PageRequest pageRequest, String searchParam) {
        Page<WikiSpaceE> wikiSpaceES = wikiSpaceRepository.listWikiSpaceByPage(resourceId, type,
                pageRequest, searchParam);
        String urlSlash = wikiUrl.endsWith("/") ? "" : "/";
        for (WikiSpaceE ws : wikiSpaceES) {
            ws.setPath(wikiUrl + urlSlash + LOCATION + ws.getPath());
        }
        return ConvertPageHelper.convertPage(wikiSpaceES, WikiSpaceResponseDTO.class);
    }

    @Override
    public WikiSpaceResponseDTO query(Long id) {
        return ConvertHelper.convert(wikiSpaceRepository.selectById(id), WikiSpaceResponseDTO.class);
    }

    @Override
    public void update(Long id, WikiSpaceDTO wikiSpaceDTO, String type) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        if (wikiSpaceE != null && wikiSpaceE.getSynchro()) {
            Map<String, String> params = new HashMap<>();
            if (!wikiSpaceE.getIcon().equals(wikiSpaceDTO.getIcon())) {
                params.put("{{ SPACE_ICON }}", wikiSpaceE.getName());
            }
            if (wikiSpaceDTO.getDescription() != null &&
                    wikiSpaceE.getDescription().equals(wikiSpaceDTO.getDescription())) {
                params.put("{{ DESCRIPTION }}", wikiSpaceE.getDescription());
            }
            if (!params.isEmpty()) {
                params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
                params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
                params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName());
                String[] path = wikiSpaceE.getPath().split("/");
                if (type.equals(WikiSpaceResourceType.ORGANIZATION_S.getResourceType())) {
                    InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                    String xmlParam = FileUtil.replaceReturnString(inputStream, params);
                    params.put("{{ SPACE_PARENT }}", path[0]);
                    iWikiSpaceWebHomeService.createSpace2WebHome(path[0], path[1], xmlParam);
                } else if (type.equals(WikiSpaceResourceType.PROJECT_S.getResourceType())) {
                    InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome2.xml");
                    String xmlParam = FileUtil.replaceReturnString(inputStream, params);
                    params.put("{{ SPACE_ROOT }}", path[0]);
                    params.put("{{ SPACE_PARENT }}", path[1]);
                    iWikiSpaceWebHomeService.createSpace3WebHome(path[0], path[1], path[2], xmlParam);
                }
            }
        }
    }

    private String getPath(Long resourceId, String type) {
        if (WikiSpaceResourceType.ORGANIZATION_S.getResourceType().equals(type)) {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    resourceId, WikiSpaceResourceType.ORGANIZATION.getResourceType());
            if (wikiSpaceEList == null || wikiSpaceEList.isEmpty() || !wikiSpaceEList.get(0).getSynchro()) {
                throw new CommonException("error.organization.synchronized");
            }
            return wikiSpaceEList.get(0).getPath();
        } else if (WikiSpaceResourceType.PROJECT_S.getResourceType().equals(type)) {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    resourceId, WikiSpaceResourceType.PROJECT.getResourceType());
            if (wikiSpaceEList == null || wikiSpaceEList.isEmpty() || !wikiSpaceEList.get(0).getSynchro()) {
                throw new CommonException("error.project.synchronized");
            }
            return wikiSpaceEList.get(0).getPath();
        }

        return "";
    }

    private void createOrgSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO) {
        String orgName = "O-" + wikiSpaceDTO.getName();
        wikiSpaceE.setPath(orgName);
        wikiSpaceE.setName(orgName);
        WikiSpaceE orgSpace = wikiSpaceRepository.insert(wikiSpaceE);
        wikiSpaceAsynService.createOrgSpace(orgName, orgSpace);
    }

    private void createProjectSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO) {
        String[] names = wikiSpaceDTO.getName().split("/");
        String param1 = "O-" + names[0];
        String param2 = "P-" + names[1];
        wikiSpaceE.setPath(param1 + "/" + param2);
        wikiSpaceE.setName(param2);
        WikiSpaceE projectSpace = wikiSpaceRepository.insert(wikiSpaceE);
        wikiSpaceAsynService.createOrgUnderSpace(param1, param2, projectSpace);
    }

    private void createOrgUnderSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String path) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.param.empty");
        }
        String orgUnderName = wikiSpaceDTO.getName();
        wikiSpaceE.setPath(path + "/" + orgUnderName);
        wikiSpaceE.setName(orgUnderName);
        WikiSpaceE orgUnderSpace = wikiSpaceRepository.insert(wikiSpaceE);
        wikiSpaceAsynService.createOrgUnderSpace(path, orgUnderName, orgUnderSpace);
    }

    private void createProjectUnderSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String path) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.param.empty");
        }
        String[] param = path.split("/");
        String projectUnderName = wikiSpaceDTO.getName();
        wikiSpaceE.setPath(path + "/" + projectUnderName);
        wikiSpaceE.setName(projectUnderName);
        WikiSpaceE projectUnderSpace = wikiSpaceRepository.insert(wikiSpaceE);
        wikiSpaceAsynService.createProjectUnderSpace(param[0], param[1], projectUnderName, projectUnderSpace);
    }
}

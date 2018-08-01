package io.choerodon.wiki.app.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;
import io.choerodon.wiki.app.service.WikiSpaceAsynService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.GetUserNameUtil;
import io.choerodon.wiki.infra.common.Stage;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/2.
 */
@Service
public class WikiSpaceServiceImpl implements WikiSpaceService {

    private static final String LOCATION = "bin/view/";
    private static final String TYPE = "project";

    private WikiSpaceRepository wikiSpaceRepository;
    private WikiSpaceAsynService wikiSpaceAsynService;
    private IWikiSpaceWebHomeService iWikiSpaceWebHomeService;
    private IamRepository iamRepository;

    @Value("${wiki.url}")
    private String wikiUrl;

    public WikiSpaceServiceImpl(WikiSpaceRepository wikiSpaceRepository,
                                WikiSpaceAsynService wikiSpaceAsynService,
                                IamRepository iamRepository,
                                IWikiSpaceWebHomeService iWikiSpaceWebHomeService) {
        this.wikiSpaceRepository = wikiSpaceRepository;
        this.wikiSpaceAsynService = wikiSpaceAsynService;
        this.iamRepository = iamRepository;
        this.iWikiSpaceWebHomeService = iWikiSpaceWebHomeService;
    }

    @Override
    public Boolean checkName(Long projectId, String name, String type) {
        return wikiSpaceRepository.checkName(projectId, name, type);
    }

    @Override
    public void create(WikiSpaceDTO wikiSpaceDTO, Long resourceId, String username, String type) {
        checkName(resourceId, wikiSpaceDTO.getName(), type);
        String path = getPath(resourceId, type);

        WikiSpaceE wikiSpaceE = new WikiSpaceE();
        wikiSpaceE.setIcon(wikiSpaceDTO.getIcon());
        wikiSpaceE.setResourceId(resourceId);
        wikiSpaceE.setResourceType(type);
        wikiSpaceE.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());

        WikiSpaceResourceType wikiSpaceResourceType = WikiSpaceResourceType.forString(type);
        switch (wikiSpaceResourceType) {
            case ORGANIZATION:
                createOrgSpace(wikiSpaceE, wikiSpaceDTO, username);
                break;
            case PROJECT:
                createProjectSpace(wikiSpaceE, wikiSpaceDTO, username);
                break;
            case ORGANIZATION_S:
                createOrgUnderSpace(wikiSpaceE, wikiSpaceDTO, path, username);
                break;
            case PROJECT_S:
                createProjectUnderSpace(wikiSpaceE, wikiSpaceDTO, path, username);
                break;
            default:
                break;
        }
    }

    @Override
    public Page<WikiSpaceListTreeDTO> listTreeWikiSpaceByPage(Long resourceId, String type,
                                                              PageRequest pageRequest, String searchParam) {
        Page<WikiSpaceE> wikiSpaceES = wikiSpaceRepository.listWikiSpaceByPage(resourceId, type,
                pageRequest, searchParam);
        String urlSlash = wikiUrl.endsWith("/") ? "" : "/";
        for (WikiSpaceE ws : wikiSpaceES) {
            ws.setPath(wikiUrl + urlSlash + LOCATION + ws.getPath());
        }
        Page<WikiSpaceListTreeDTO> page = ConvertPageHelper.convertPage(wikiSpaceES, WikiSpaceListTreeDTO.class);
        String queryType = type.equals(WikiSpaceResourceType.ORGANIZATION.getResourceType()) ?
                WikiSpaceResourceType.ORGANIZATION_S.getResourceType() : WikiSpaceResourceType.PROJECT_S.getResourceType();
        page.stream().forEach(p -> {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(p.getResourceId(), queryType);
            List<WikiSpaceE> list = new ArrayList<>();
            for (WikiSpaceE ws : wikiSpaceEList) {
                if (!ws.getStatus().equals(SpaceStatus.DELETED.getSpaceStatus())) {
                    ws.setPath(wikiUrl + urlSlash + LOCATION + ws.getPath());
                    list.add(ws);
                }
            }
            p.setChildren(ConvertHelper.convertList(list, WikiSpaceResponseDTO.class));
        });

        return page;
    }

    @Override
    public WikiSpaceResponseDTO query(Long id) {
        return ConvertHelper.convert(wikiSpaceRepository.selectById(id), WikiSpaceResponseDTO.class);
    }

    @Override
    public WikiSpaceResponseDTO update(Long id, WikiSpaceDTO wikiSpaceDTO, String username) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        if (wikiSpaceE != null && wikiSpaceE.getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
            Map<String, String> params = new HashMap<>();
            if (!wikiSpaceE.getIcon().equals(wikiSpaceDTO.getIcon())) {
                params.put("{{ SPACE_ICON }}", wikiSpaceDTO.getIcon());
                wikiSpaceE.setIcon(wikiSpaceDTO.getIcon());
            }
            if (!params.isEmpty()) {
                params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
                params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
                params.put("{{ SPACE_TARGET }}", wikiSpaceE.getName());
                String[] path = wikiSpaceE.getPath().split("/");
                WikiSpaceResourceType wikiSpaceResourceType = WikiSpaceResourceType.forString(wikiSpaceE.getResourceType());
                switch (wikiSpaceResourceType) {
                    case ORGANIZATION:
                        InputStream orgIs = this.getClass().getResourceAsStream("/xml/webhome.xml");
                        String orgXmlParam = FileUtil.replaceReturnString(orgIs, params);
                        iWikiSpaceWebHomeService.createSpace1WebHome(path[0], orgXmlParam, username);
                        break;
                    case PROJECT:
                        params.put("{{ SPACE_PARENT }}", path[0]);
                        InputStream projectIs = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                        String projectXmlParam = FileUtil.replaceReturnString(projectIs, params);
                        iWikiSpaceWebHomeService.createSpace2WebHome(path[0], path[1], projectXmlParam, username);
                        break;
                    case ORGANIZATION_S:
                        params.put("{{ SPACE_PARENT }}", path[0]);
                        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                        String xmlParam = FileUtil.replaceReturnString(inputStream, params);
                        iWikiSpaceWebHomeService.createSpace2WebHome(path[0], path[1], xmlParam, username);
                        break;
                    case PROJECT_S:
                        params.put("{{ SPACE_ROOT }}", path[0]);
                        params.put("{{ SPACE_PARENT }}", path[1]);
                        InputStream is = this.getClass().getResourceAsStream("/xml/webhome2.xml");
                        String xml = FileUtil.replaceReturnString(is, params);
                        iWikiSpaceWebHomeService.createSpace3WebHome(path[0], path[1], path[2], xml, username);
                        break;
                    default:
                        break;
                }
                return ConvertHelper.convert(wikiSpaceRepository.update(wikiSpaceE), WikiSpaceResponseDTO.class);
            }
        }

        return ConvertHelper.convert(wikiSpaceE, WikiSpaceResponseDTO.class);
    }

    @Override
    public void delete(Long resourceId, Long id) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        if (!resourceId.equals(wikiSpaceE.getResourceId())) {
            throw new CommonException("error.resourceId.equal");
        }
        wikiSpaceE.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());
        wikiSpaceRepository.update(wikiSpaceE);

        WikiSpaceResourceType wikiSpaceResourceType = WikiSpaceResourceType.forString(wikiSpaceE.getResourceType());
        switch (wikiSpaceResourceType) {
            case ORGANIZATION:
                //删除组织对应的空间
                deleteOrgPage(resourceId, wikiSpaceE.getId());
                //删除组织下的空间
                List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                        resourceId,
                        WikiSpaceResourceType.ORGANIZATION_S.getResourceType());
                for (WikiSpaceE w : wikiSpaceEList) {
                    deleteOrgUnderPage(w);
                }
                //删除项目对应的空间
                List<ProjectE> projectEList = new ArrayList<>();
                Page<ProjectE> projectEPage = iamRepository.pageByProject(resourceId, 0, 400);
                projectEList.addAll(projectEPage);
                if (projectEPage.getTotalPages() > 1) {
                    for (int i = 1; i < projectEPage.getTotalPages(); i++) {
                        Page<ProjectE> page = iamRepository.pageByProject(resourceId, i, 400);
                        projectEList.addAll(page);
                    }
                }
                for (ProjectE p : projectEList) {
                    deleteProjectPage(p.getId(), wikiSpaceE.getId());
                    //删除项目下对应的空间
                    List<WikiSpaceE> projectUnderSpace = wikiSpaceRepository.getWikiSpaceList(
                            p.getId(),
                            WikiSpaceResourceType.PROJECT_S.getResourceType());
                    for (WikiSpaceE w : projectUnderSpace) {
                        deleteProjectUnderPage(w);
                    }
                }
                break;
            case PROJECT:
                //删除项目对应的空间
                deleteProjectPage(resourceId, wikiSpaceE.getId());
                //删除项目下对应的空间
                List<WikiSpaceE> list = wikiSpaceRepository.getWikiSpaceList(
                        resourceId,
                        WikiSpaceResourceType.PROJECT_S.getResourceType());
                for (WikiSpaceE w : list) {
                    deleteProjectUnderPage(w);
                }
                break;
            case ORGANIZATION_S:
                deleteOrgUnderPage(wikiSpaceE);
                break;
            case PROJECT_S:
                deleteProjectUnderPage(wikiSpaceE);
                break;
            default:
                break;
        }
    }

    @Async
    public void deleteProjectUnderPage(WikiSpaceE wikiSpaceE) {
        String[] param = wikiSpaceE.getPath().split("/");
        int webHome = iWikiSpaceWebHomeService.deletePage2(
                param[0],
                param[1],
                param[2],
                Stage.WEBHOME,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage2(
                param[0],
                param[1],
                param[2],
                Stage.WEBPREFERENCES,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage2(
                param[0],
                param[1],
                param[2],
                Stage.PAGE,
                GetUserNameUtil.getUsername());
        checkCodeDelete(webHome, wikiSpaceE.getId());
    }

    @Async
    public void deleteOrgUnderPage(WikiSpaceE wikiSpaceE) {
        String[] param = wikiSpaceE.getPath().split("/");
        int webHome = iWikiSpaceWebHomeService.deletePage1(
                param[0],
                param[1],
                Stage.WEBHOME,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage1(
                param[0],
                param[1],
                Stage.WEBPREFERENCES,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage1(
                param[0],
                param[1],
                Stage.PAGE,
                GetUserNameUtil.getUsername());
        checkCodeDelete(webHome, wikiSpaceE.getId());
    }

    @Async
    public void deleteOrgPage(Long resourceId, Long id) {
        OrganizationE organization = iamRepository.queryOrganizationById(resourceId);
        String adminGroupName = "O-" + organization.getCode() + Stage.ADMIN_GROUP;
        String userGroupName = "O-" + organization.getCode() + Stage.USER_GROUP;
        iWikiSpaceWebHomeService.deletePage(
                Stage.SPACE,
                adminGroupName,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage(
                Stage.SPACE,
                userGroupName,
                GetUserNameUtil.getUsername());
        int webHome = iWikiSpaceWebHomeService.deletePage(
                "O-" + organization.getName(),
                Stage.WEBHOME,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage(
                "O-" + organization.getName(),
                Stage.WEBPREFERENCES,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage(
                "O-" + organization.getName(),
                Stage.PAGE,
                GetUserNameUtil.getUsername());
        checkCodeDelete(webHome, id);
    }

    @Async
    public void deleteProjectPage(Long resourceId, Long id) {
        ProjectE projectE = iamRepository.queryIamProject(resourceId);
        if (projectE != null) {
            Long orgId = projectE.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            String adminGroupName = "P-" + organization.getCode() + "-" + projectE.getCode() + Stage.ADMIN_GROUP;
            String userGroupName = "P-" + organization.getCode() + "-" + projectE.getCode() + Stage.USER_GROUP;
            iWikiSpaceWebHomeService.deletePage(
                    Stage.SPACE,
                    adminGroupName,
                    GetUserNameUtil.getUsername());
            iWikiSpaceWebHomeService.deletePage(
                    Stage.SPACE,
                    userGroupName,
                    GetUserNameUtil.getUsername());
            int webHome = iWikiSpaceWebHomeService.deletePage1(
                    "O-" + organization.getName(),
                    "P-" + projectE.getName(),
                    Stage.WEBHOME,
                    GetUserNameUtil.getUsername());
            iWikiSpaceWebHomeService.deletePage1(
                    "O-" + organization.getName(),
                    "P-" + projectE.getName(),
                    Stage.WEBPREFERENCES,
                    GetUserNameUtil.getUsername());
            iWikiSpaceWebHomeService.deletePage1(
                    "O-" + organization.getName(),
                    "P-" + projectE.getName(),
                    Stage.PAGE,
                    GetUserNameUtil.getUsername());
            checkCodeDelete(webHome, id);
        }
    }

    public void checkCodeDelete(int webHomeCode, long id) {
        if (webHomeCode == 204) {
            WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
            wikiSpaceE.setStatus(SpaceStatus.DELETED.getSpaceStatus());
            wikiSpaceRepository.update(wikiSpaceE);
        } else {
            WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
            wikiSpaceE.setStatus(SpaceStatus.FAILED.getSpaceStatus());
            wikiSpaceRepository.update(wikiSpaceE);
        }
    }

    private String getPath(Long resourceId, String type) {
        if (WikiSpaceResourceType.ORGANIZATION_S.getResourceType().equals(type)) {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    resourceId, WikiSpaceResourceType.ORGANIZATION.getResourceType());
            if (wikiSpaceEList == null || wikiSpaceEList.isEmpty()
                    || !wikiSpaceEList.get(0).getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
                throw new CommonException("error.organization.synchronized");
            }
            return wikiSpaceEList.get(0).getPath();
        } else if (WikiSpaceResourceType.PROJECT_S.getResourceType().equals(type)) {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
                    resourceId, WikiSpaceResourceType.PROJECT.getResourceType());
            if (wikiSpaceEList == null || wikiSpaceEList.isEmpty()
                    || !wikiSpaceEList.get(0).getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
                throw new CommonException("error.project.synchronized");
            }
            return wikiSpaceEList.get(0).getPath();
        }

        return "";
    }

    private void createOrgSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String username) {
        String orgName = "O-" + wikiSpaceDTO.getName();
        wikiSpaceE.setPath(orgName);
        wikiSpaceE.setName(orgName);
        WikiSpaceE orgSpace = wikiSpaceRepository.insertIfNotExist(wikiSpaceE);
        wikiSpaceAsynService.createOrgSpace(orgName, orgSpace, username);
    }

    private void createProjectSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String username) {
        String[] names = wikiSpaceDTO.getName().split("/");
        String param1 = "O-" + names[0];
        String param2 = "P-" + names[1];
        wikiSpaceE.setPath(param1 + "/" + param2);
        wikiSpaceE.setName(param2);
        WikiSpaceE projectSpace = wikiSpaceRepository.insertIfNotExist(wikiSpaceE);
        wikiSpaceAsynService.createProjectSpace(param1, param2, projectSpace, username, TYPE);
    }

    private void createOrgUnderSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String path, String username) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.param.empty");
        }
        String orgUnderName = wikiSpaceDTO.getName();
        wikiSpaceE.setPath(path + "/" + orgUnderName);
        wikiSpaceE.setName(orgUnderName);
        WikiSpaceE orgUnderSpace = wikiSpaceRepository.insert(wikiSpaceE);
        wikiSpaceAsynService.createOrgUnderSpace(path, orgUnderName, orgUnderSpace, username, null);
    }

    private void createProjectUnderSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String path, String username) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.param.empty");
        }
        String[] param = path.split("/");
        String projectUnderName = wikiSpaceDTO.getName();
        wikiSpaceE.setPath(path + "/" + projectUnderName);
        wikiSpaceE.setName(projectUnderName);
        WikiSpaceE projectUnderSpace = wikiSpaceRepository.insert(wikiSpaceE);
        wikiSpaceAsynService.createProjectUnderSpace(param[0], param[1], projectUnderName, projectUnderSpace, username);
    }
}

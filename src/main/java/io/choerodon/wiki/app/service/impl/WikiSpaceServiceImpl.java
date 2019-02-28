package io.choerodon.wiki.app.service.impl;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.wiki.api.dto.MenuDTO;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;
import io.choerodon.wiki.app.service.WikiSpaceAsynService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.event.OrganizationEventPayload;
import io.choerodon.wiki.domain.application.event.ProjectEvent;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.domain.service.IWikiSpaceWebHomeService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.FileUtil;
import io.choerodon.wiki.infra.common.GetUserNameUtil;
import io.choerodon.wiki.infra.common.enums.SpaceStatus;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/2.
 */
@Service
public class WikiSpaceServiceImpl implements WikiSpaceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiSpaceServiceImpl.class);
    private static final List<String> initData = Stream.of("敏捷管理", "版本更新", "猪齿鱼功能介绍").collect(toList());

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
    public Boolean checkName(Long resourceId, String name, String type) {
        return wikiSpaceRepository.checkName(resourceId, name, type);
    }

    @Override
    public void create(WikiSpaceDTO wikiSpaceDTO, Long resourceId, String username, String type, Boolean flag) {
        if (flag) {
            checkName(resourceId, wikiSpaceDTO.getName(), type);
        }
        String path = getPath(resourceId, type);

        WikiSpaceE wikiSpaceE = new WikiSpaceE();
        wikiSpaceE.setIcon(wikiSpaceDTO.getIcon());
        wikiSpaceE.setResourceId(resourceId);
        wikiSpaceE.setResourceType(type);
        wikiSpaceE.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());
        LOGGER.info("start creating spaces, path:{} and wikiSpaceE: {} ", path, wikiSpaceE.toString());
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
            ws.setPath(wikiUrl + urlSlash + BaseStage.LOCATION + ws.getPath());
        }
        Page<WikiSpaceListTreeDTO> page = ConvertPageHelper.convertPage(wikiSpaceES, WikiSpaceListTreeDTO.class);
        String queryType = type.equals(WikiSpaceResourceType.ORGANIZATION.getResourceType()) ?
                WikiSpaceResourceType.ORGANIZATION_S.getResourceType() : WikiSpaceResourceType.PROJECT_S.getResourceType();
        page.stream().forEach(p -> {
            List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(p.getResourceId(), queryType);
            List<WikiSpaceE> list = new ArrayList<>();
            for (WikiSpaceE ws : wikiSpaceEList) {
                if (!ws.getStatus().equals(SpaceStatus.DELETED.getSpaceStatus())) {
                    ws.setPath(wikiUrl + urlSlash + BaseStage.LOCATION + ws.getPath());
                    list.add(ws);
                }
            }
            if (list.isEmpty()) {
                p.setChildren(null);
            } else {
                p.setChildren(ConvertHelper.convertList(list, WikiSpaceResponseDTO.class));
            }
        });

        return page;
    }

    @Override
    public List<WikiSpaceResponseDTO> underOrganization(Long organizationId, String type) {
        return getWikiSpaceByResourceIdAndResourceType(organizationId, type);
    }

    @Override
    public List<WikiSpaceResponseDTO> underProject(Long projectId, String type) {
        return getWikiSpaceByResourceIdAndResourceType(projectId, type);
    }

    @Override
    public WikiSpaceResponseDTO query(Long id) {
        return ConvertHelper.convert(wikiSpaceRepository.selectById(id), WikiSpaceResponseDTO.class);
    }

    @Override
    public WikiSpaceResponseDTO update(Long id, WikiSpaceDTO wikiSpaceDTO, String username) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        LOGGER.info("start update the space,query wikiSpace:{} by id:{}", wikiSpaceE, id);
        if (wikiSpaceE != null && wikiSpaceE.getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
            Map<String, String> params = new HashMap<>(16);
            if (!wikiSpaceE.getIcon().equals(wikiSpaceDTO.getIcon())) {
                params.put("{{ SPACE_ICON }}", wikiSpaceDTO.getIcon());
                wikiSpaceE.setIcon(wikiSpaceDTO.getIcon());
            }
            if (!params.isEmpty()) {
                String[] path = wikiSpaceE.getPath().split("/");
                params.put("{{ SPACE_TITLE }}", wikiSpaceE.getName());
                params.put("{{ SPACE_LABEL }}", wikiSpaceE.getName());
                WikiSpaceResourceType wikiSpaceResourceType = WikiSpaceResourceType.forString(wikiSpaceE.getResourceType());
                switch (wikiSpaceResourceType) {
                    case ORGANIZATION:
                        params.put("{{ SPACE_TARGET }}", path[0].replace(".", "\\."));
                        InputStream orgIs = this.getClass().getResourceAsStream("/xml/webhome.xml");
                        String orgXmlParam = FileUtil.replaceReturnString(orgIs, params);
                        iWikiSpaceWebHomeService.createSpace1WebHome(id, path[0], orgXmlParam, username);
                        break;
                    case PROJECT:
                        params.put("{{ SPACE_PARENT }}", path[0].replace(".", "\\."));
                        params.put("{{ SPACE_TARGET }}", path[1].replace(".", "\\."));
                        InputStream projectIs = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                        String projectXmlParam = FileUtil.replaceReturnString(projectIs, params);
                        iWikiSpaceWebHomeService.createSpace2WebHome(id, path[0], path[1], projectXmlParam, username);
                        break;
                    case ORGANIZATION_S:
                        params.put("{{ SPACE_PARENT }}", path[0].replace(".", "\\."));
                        params.put("{{ SPACE_TARGET }}", path[1].replace(".", "\\."));
                        InputStream inputStream = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                        String xmlParam = FileUtil.replaceReturnString(inputStream, params);
                        iWikiSpaceWebHomeService.createSpace2WebHome(id, path[0], path[1], xmlParam, username);
                        break;
                    case PROJECT_S:
                        params.put("{{ SPACE_ROOT }}", path[0].replace(".", "\\."));
                        params.put("{{ SPACE_PARENT }}", path[1].replace(".", "\\."));
                        params.put("{{ SPACE_TARGET }}", path[2].replace(".", "\\."));
                        InputStream is = this.getClass().getResourceAsStream("/xml/webhome2.xml");
                        String xml = FileUtil.replaceReturnString(is, params);
                        iWikiSpaceWebHomeService.createSpace3WebHome(id, path[0], path[1], path[2], xml, username);
                        break;
                    default:
                        break;
                }
                return ConvertHelper.convert(wikiSpaceRepository.update(wikiSpaceE), WikiSpaceResponseDTO.class);
            }
        } else {
            throw new CommonException("error.space.update");
        }

        return ConvertHelper.convert(wikiSpaceE, WikiSpaceResponseDTO.class);
    }

    @Override
    public void syncOrg(Long id) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        LOGGER.info("start sync the spaces under the organization,query wikiSpace:{} by id:{}", wikiSpaceE, id);
        if (wikiSpaceE != null && wikiSpaceE.getResourceType().equals(WikiSpaceResourceType.ORGANIZATION_S.getResourceType())) {
            if (wikiSpaceE.getStatus().equals(SpaceStatus.FAILED.getSpaceStatus())) {
                wikiSpaceE.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());
                wikiSpaceE = wikiSpaceRepository.update(wikiSpaceE);
                wikiSpaceAsynService.createOrgUnderSpace(wikiSpaceE.getPath().split("/")[0].replace(".", "\\."),
                        wikiSpaceE.getPath().split("/")[1].replace(".", "\\."),
                        wikiSpaceE,
                        BaseStage.USERNAME);
            } else {
                throw new CommonException("error.space.status");
            }
        } else {
            throw new CommonException("error.under.organization.space.not.sync");
        }
    }

    @Override
    public void syncProject(Long id) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        LOGGER.info("start sync the spaces under the project,query wikiSpace:{} by id:{}", wikiSpaceE, id);
        if (wikiSpaceE != null && wikiSpaceE.getResourceType().equals(WikiSpaceResourceType.PROJECT_S.getResourceType())) {
            if (wikiSpaceE.getStatus().equals(SpaceStatus.FAILED.getSpaceStatus())) {
                wikiSpaceE.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());
                wikiSpaceE = wikiSpaceRepository.update(wikiSpaceE);
                wikiSpaceAsynService.createProjectUnderSpace(wikiSpaceE.getPath().split("/")[0].replace(".", "\\."),
                        wikiSpaceE.getPath().split("/")[1].replace(".", "\\."),
                        wikiSpaceE.getPath().split("/")[2].replace(".", "\\."),
                        wikiSpaceE,
                        BaseStage.USERNAME);
            } else {
                throw new CommonException("error.space.status");
            }
        } else {
            throw new CommonException("error.under.project.space.not.sync");
        }
    }

    @Override
    public void delete(Long resourceId, Long id) {
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectById(id);
        LOGGER.info("start delete the space,query wikiSpace:{} by id:{}", wikiSpaceE, id);
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

    @Override
    public String queryWikiMenus(Long projectId, MenuDTO menuDTO) {
        String param = "";
        if (StringUtils.isEmpty(menuDTO.getMenuId())) {
            List<WikiSpaceResponseDTO> wikiSpaceList = this.getWikiSpaceList(projectId, WikiSpaceResourceType.PROJECT.getResourceType());
            if (wikiSpaceList != null && !wikiSpaceList.isEmpty() && wikiSpaceList.get(0).getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
                String[] path = wikiSpaceList.get(0).getPath().split("/");
                param = "document:xwiki:" + path[0].replace(".", "\\.") + "." + path[1].replace(".", "\\.") + ".WebHome";
            }

        } else {
           param = menuDTO.getMenuId();
        }

        String menuIdStr = null;
        try {
            menuIdStr = URLEncoder.encode(param, BaseStage.ENC);
        } catch (UnsupportedEncodingException n) {
            throw new CommonException(n.getMessage());
        }

        return iWikiSpaceWebHomeService.getPageMenuUnderProject(menuIdStr, BaseStage.USERNAME);
    }

    @Override
    public List<WikiSpaceResponseDTO> getWikiSpaceList(Long resourceId, String resourceType) {
        return ConvertHelper.convertList(wikiSpaceRepository.getWikiSpaceList(
                resourceId,
                resourceType), WikiSpaceResponseDTO.class);
    }

    @Override
    public void createDemo(Long id, String username) {
        List<WikiSpaceResponseDTO> wikiSpaceList = this.getWikiSpaceList(id, WikiSpaceResourceType.ORGANIZATION.getResourceType());
        if (wikiSpaceList != null && !wikiSpaceList.isEmpty() && wikiSpaceList.get(0).getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
            //创建组织下的空间
            for (String data : initData) {
                WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectOne(id, data, WikiSpaceResourceType.ORGANIZATION_S.getResourceType());
                String path = wikiSpaceList.get(0).getPath();
                if (StringUtils.isEmpty(path)) {
                    throw new CommonException("error.organization.path.empty");
                }
                if (wikiSpaceE == null) {
                    wikiSpaceE = new WikiSpaceE();
                    wikiSpaceE.setResourceId(id);
                    wikiSpaceE.setResourceType(WikiSpaceResourceType.ORGANIZATION_S.getResourceType());
                    wikiSpaceE.setName(data);
                    wikiSpaceE.setIcon(BaseStage.ORG_ICON);
                    wikiSpaceE.setPath(path + "/" + data);
                    wikiSpaceE.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());
                    LOGGER.info("start creating spaces, path:{} and wikiSpaceE: {} ", wikiSpaceE.getPath(), wikiSpaceE.toString());

                    WikiSpaceE orgUnderSpace = wikiSpaceRepository.insert(wikiSpaceE);

                    wikiSpaceAsynService.createDemoOrgUnderSpace(path, data, orgUnderSpace, username);
                } else {
                    if (!SpaceStatus.SUCCESS.getSpaceStatus().equals(wikiSpaceE.getStatus())) {
                        wikiSpaceAsynService.createDemoOrgUnderSpace(path, data, wikiSpaceE, username);
                    }
                }
            }
        }
    }

    public void deleteProjectUnderPage(WikiSpaceE wikiSpaceE) {
        LOGGER.info("delete the page under the project,wikiSpaceE:{}", wikiSpaceE.toString());
        String[] param = wikiSpaceE.getPath().split("/");
        int webHome = iWikiSpaceWebHomeService.deletePage2(
                wikiSpaceE.getId(),
                param[0].replace(".", "\\."),
                param[1].replace(".", "\\."),
                param[2].replace(".", "\\."),
                BaseStage.WEBHOME,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage2(
                wikiSpaceE.getId(),
                param[0].replace(".", "\\."),
                param[1].replace(".", "\\."),
                param[2].replace(".", "\\."),
                BaseStage.WEBPREFERENCES,
                GetUserNameUtil.getUsername());
        checkCodeDelete(webHome, wikiSpaceE.getId());
    }

    public void deleteOrgUnderPage(WikiSpaceE wikiSpaceE) {
        LOGGER.info("delete the page under the organization,wikiSpaceE:{}", wikiSpaceE.toString());
        String[] param = wikiSpaceE.getPath().split("/");
        int webHome = iWikiSpaceWebHomeService.deletePage1(
                wikiSpaceE.getId(),
                param[0].replace(".", "\\."),
                param[1].replace(".", "\\."),
                BaseStage.WEBHOME,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage1(
                wikiSpaceE.getId(),
                param[0].replace(".", "\\."),
                param[1].replace(".", "\\."),
                BaseStage.WEBPREFERENCES,
                GetUserNameUtil.getUsername());
        checkCodeDelete(webHome, wikiSpaceE.getId());
    }

    public void deleteOrgPage(Long resourceId, Long id) {
        OrganizationE organization = iamRepository.queryOrganizationById(resourceId);
        String adminGroupName = BaseStage.O + organization.getCode() + BaseStage.ADMIN_GROUP;
        String userGroupName = BaseStage.O + organization.getCode() + BaseStage.USER_GROUP;
        iWikiSpaceWebHomeService.deletePage(
                id,
                BaseStage.SPACE,
                adminGroupName,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage(
                id,
                BaseStage.SPACE,
                userGroupName,
                GetUserNameUtil.getUsername());
        int webHome = iWikiSpaceWebHomeService.deletePage(
                id,
                BaseStage.O + organization.getName(),
                BaseStage.WEBHOME,
                GetUserNameUtil.getUsername());
        iWikiSpaceWebHomeService.deletePage(
                id,
                BaseStage.O + organization.getName(),
                BaseStage.WEBPREFERENCES,
                GetUserNameUtil.getUsername());
        checkCodeDelete(webHome, id);
    }

    public void deleteProjectPage(Long resourceId, Long id) {
        ProjectE projectE = iamRepository.queryIamProject(resourceId);
        if (projectE != null) {
            Long orgId = projectE.getOrganization().getId();
            OrganizationE organization = iamRepository.queryOrganizationById(orgId);
            String adminGroupName = BaseStage.P + organization.getCode() + BaseStage.LINE + projectE.getCode() + BaseStage.ADMIN_GROUP;
            String userGroupName = BaseStage.P + organization.getCode() + BaseStage.LINE + projectE.getCode() + BaseStage.USER_GROUP;
            iWikiSpaceWebHomeService.deletePage(
                    id,
                    BaseStage.SPACE,
                    adminGroupName,
                    GetUserNameUtil.getUsername());
            iWikiSpaceWebHomeService.deletePage(
                    id,
                    BaseStage.SPACE,
                    userGroupName,
                    GetUserNameUtil.getUsername());
            int webHome = iWikiSpaceWebHomeService.deletePage1(
                    id,
                    BaseStage.O + organization.getName(),
                    BaseStage.P + projectE.getName(),
                    BaseStage.WEBHOME,
                    GetUserNameUtil.getUsername());
            iWikiSpaceWebHomeService.deletePage1(
                    id,
                    BaseStage.O + organization.getName(),
                    BaseStage.P + projectE.getName(),
                    BaseStage.WEBPREFERENCES,
                    GetUserNameUtil.getUsername());
            checkCodeDelete(webHome, id);
        }
    }

    public void checkCodeDelete(int webHomeCode, long id) {
        LOGGER.info("delete page webHome code: {}", webHomeCode);
        if (webHomeCode == BaseStage.NO_CONTENT || webHomeCode == BaseStage.NOT_FOUND) {
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
        String orgName = BaseStage.O + wikiSpaceDTO.getName();
        wikiSpaceE.setPath(orgName);
        wikiSpaceE.setName(orgName);
        WikiSpaceE orgSpace = wikiSpaceRepository.insertIfNotExist(wikiSpaceE);
        wikiSpaceAsynService.createOrgSpace(orgName, orgSpace, username);
    }

    private void createProjectSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String username) {
        String[] names = wikiSpaceDTO.getName().split("/");
        String param1 = BaseStage.O + names[0];
        String param2 = BaseStage.P + names[1];
        wikiSpaceE.setPath(param1 + "/" + param2);
        wikiSpaceE.setName(param2);
        WikiSpaceE projectSpace = wikiSpaceRepository.insertIfNotExist(wikiSpaceE);
        wikiSpaceAsynService.createProjectSpace(param1, param2, projectSpace, username);
    }

    private void createOrgUnderSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String path, String username) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.param.empty");
        }
        String orgUnderName = wikiSpaceDTO.getName();
        wikiSpaceE.setPath(path + "/" + orgUnderName);
        wikiSpaceE.setName(orgUnderName);
        WikiSpaceE selectOneSpace = wikiSpaceRepository.selectOne(wikiSpaceE.getResourceId(), orgUnderName, wikiSpaceE.getResourceType());
        WikiSpaceE orgUnderSpace;
        if (selectOneSpace == null) {
            orgUnderSpace = wikiSpaceRepository.insert(wikiSpaceE);
        } else {
            selectOneSpace.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());
            orgUnderSpace = wikiSpaceRepository.update(selectOneSpace);
        }
        wikiSpaceAsynService.createOrgUnderSpace(path, orgUnderName, orgUnderSpace, username);
    }

    private void createProjectUnderSpace(WikiSpaceE wikiSpaceE, WikiSpaceDTO wikiSpaceDTO, String path, String username) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.param.empty");
        }
        String[] param = path.split("/");
        String projectUnderName = wikiSpaceDTO.getName();
        wikiSpaceE.setPath(path + "/" + projectUnderName);
        wikiSpaceE.setName(projectUnderName);
        WikiSpaceE selectOneSpace = wikiSpaceRepository.selectOne(wikiSpaceE.getResourceId(), projectUnderName, wikiSpaceE.getResourceType());
        WikiSpaceE projectUnderSpace;
        if (selectOneSpace == null) {
            projectUnderSpace = wikiSpaceRepository.insert(wikiSpaceE);
        } else {
            selectOneSpace.setStatus(SpaceStatus.OPERATIING.getSpaceStatus());
            projectUnderSpace = wikiSpaceRepository.update(selectOneSpace);
        }
        wikiSpaceAsynService.createProjectUnderSpace(param[0], param[1], projectUnderName, projectUnderSpace, username);
    }

    public List<WikiSpaceResponseDTO> getWikiSpaceByResourceIdAndResourceType(Long resourceId, String resourceType) {
        String urlSlash = wikiUrl.endsWith("/") ? "" : "/";
        List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(resourceId, resourceType);
        List<WikiSpaceE> list = new ArrayList<>();
        wikiSpaceEList.stream()
                .filter(ws -> ws.getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus()))
                .forEach(ws -> {
                    ws.setPath(wikiUrl + urlSlash + BaseStage.LOCATION + ws.getPath());
                    list.add(ws);
                });

        return ConvertHelper.convertList(list, WikiSpaceResponseDTO.class);
    }

    @Override
    public void updateAndSyncProject(ProjectEvent projectEvent) {
        Long projectId = projectEvent.getProjectId();
        String projectName = projectEvent.getProjectName();
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectOrgOrPro(projectId, WikiSpaceResourceType.PROJECT.getResourceType());
        if (SpaceStatus.SUCCESS.getSpaceStatus().equals(wikiSpaceE.getStatus())) {
            String updateName = BaseStage.P + projectName;
            if (!updateName.equals(wikiSpaceE.getName())) {
                WikiSpaceE updateSpace = new WikiSpaceE();
                updateSpace.setId(wikiSpaceE.getId());
                updateSpace.setName(updateName);
                updateSpace.setObjectVersionNumber(wikiSpaceE.getObjectVersionNumber());
                updateWiki(wikiSpaceE, updateName);
                wikiSpaceRepository.updateSelective(updateSpace);
            }
        }
    }

    @Override
    public void updateAndSyncOrganization(OrganizationEventPayload organizationEventPayload) {
        Long organizationId = organizationEventPayload.getId();
        String organizationName = organizationEventPayload.getName();
        WikiSpaceE wikiSpaceE = wikiSpaceRepository.selectOrgOrPro(organizationId, WikiSpaceResourceType.ORGANIZATION.getResourceType());
        if (SpaceStatus.SUCCESS.getSpaceStatus().equals(wikiSpaceE.getStatus())) {
            String updateName = BaseStage.O + organizationName;
            if (!updateName.equals(wikiSpaceE.getName())) {
                WikiSpaceE updateSpace = new WikiSpaceE();
                updateSpace.setId(wikiSpaceE.getId());
                updateSpace.setObjectVersionNumber(wikiSpaceE.getObjectVersionNumber());
                updateSpace.setName(updateName);
                updateWiki(wikiSpaceE, updateName);
                wikiSpaceRepository.updateSelective(updateSpace);
            }
        }
    }

    public void updateWiki(WikiSpaceE wikiSpaceE, String updateName) {
        if (wikiSpaceE != null && wikiSpaceE.getStatus().equals(SpaceStatus.SUCCESS.getSpaceStatus())) {
            Map<String, String> params = new HashMap<>(16);
            params.put("{{ SPACE_ICON }}", wikiSpaceE.getIcon());
            params.put("{{ SPACE_TITLE }}", updateName);
            params.put("{{ SPACE_LABEL }}", updateName);
            String[] path = wikiSpaceE.getPath().split("/");
            WikiSpaceResourceType wikiSpaceResourceType = WikiSpaceResourceType.forString(wikiSpaceE.getResourceType());
            switch (wikiSpaceResourceType) {
                case ORGANIZATION:
                    params.put("{{ SPACE_TARGET }}", path[0].replace(".", "\\."));
                    InputStream orgIs = this.getClass().getResourceAsStream("/xml/webhome.xml");
                    String orgXmlParam = FileUtil.replaceReturnString(orgIs, params);
                    iWikiSpaceWebHomeService.createSpace1WebHome(wikiSpaceE.getId(), path[0], orgXmlParam, BaseStage.USERNAME);
                    break;
                case PROJECT:
                    params.put("{{ SPACE_PARENT }}", path[0].replace(".", "\\."));
                    params.put("{{ SPACE_TARGET }}", path[1].replace(".", "\\."));
                    InputStream projectIs = this.getClass().getResourceAsStream("/xml/webhome1.xml");
                    String projectXmlParam = FileUtil.replaceReturnString(projectIs, params);
                    iWikiSpaceWebHomeService.createSpace2WebHome(wikiSpaceE.getId(), path[0], path[1], projectXmlParam, BaseStage.USERNAME);
                    break;
                default:
                    break;
            }
        } else {
            throw new CommonException("error.space.update");
        }

    }

}

package io.choerodon.wiki.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.wiki.api.dto.WikiGroupDTO;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.app.service.WikiGroupService;
import io.choerodon.wiki.app.service.WikiScanningService;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.domain.application.entity.ProjectE;
import io.choerodon.wiki.domain.application.entity.WikiSpaceE;
import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;
import io.choerodon.wiki.domain.application.repository.IamRepository;
import io.choerodon.wiki.domain.application.repository.WikiSpaceRepository;
import io.choerodon.wiki.infra.common.Stage;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/18.
 */
@Service
public class WikiScanningServiceImpl implements WikiScanningService {

    private static final String PROJECT_ICON = "project";
    private static final String USERNAME = "admin";

    private IamRepository iamRepository;
    private WikiSpaceRepository wikiSpaceRepository;
    private WikiSpaceService wikiSpaceService;
    private WikiGroupService wikiGroupService;

    public WikiScanningServiceImpl(IamRepository iamRepository,
                                   WikiSpaceRepository wikiSpaceRepository,
                                   WikiSpaceService wikiSpaceService,
                                   WikiGroupService wikiGroupService) {
        this.iamRepository = iamRepository;
        this.wikiSpaceRepository = wikiSpaceRepository;
        this.wikiSpaceService = wikiSpaceService;
        this.wikiGroupService = wikiGroupService;
    }

    @Override
    public void scanning() {
//        List<OrganizationE> organizationEList = new ArrayList<>();
//        Page<OrganizationE> pageByOrganization = iamRepository.pageByOrganization(0,400);
//        organizationEList.addAll(pageByOrganization.getContent());
//        int page = pageByOrganization.getTotalPages();
//        if (page > 1) {
//            for (int i = 1;i < page;i++) {
//                Page<OrganizationE> list = iamRepository.pageByOrganization(i,400);
//                organizationEList.addAll(list.getContent());
//            }
//        }
//
//        organizationEList.stream().forEach(organizationE -> {
//            if (organizationE.getEnabled()) {
//                List<WikiSpaceE> wikiSpaceEList = wikiSpaceRepository.getWikiSpaceList(
//                        organizationE.getId(), WikiSpaceResourceType.ORGANIZATION.getResourceType());
//                if (wikiSpaceEList != null && !wikiSpaceEList.isEmpty()) {
//                    if (organizationE.getProjectCount() != 0) {
//                        List<ProjectE> projectEList = new ArrayList<>();
//                        Page<ProjectE> projectEPage = iamRepository.pageByProject(organizationE.getId(),0,400);
//                        projectEList.addAll(projectEPage.getContent());
//                        int projectPage = projectEPage.getTotalPages();
//                        if (projectPage > 1) {
//                            for (int i = 1;i < projectPage;i++) {
//                                Page<ProjectE> list = iamRepository.pageByProject(organizationE.getId(), i, 400);
//                                projectEList.addAll(list.getContent());
//                            }
//                        }
//
//                        projectEList.stream().forEach(projectE -> {
//                            List<WikiSpaceE> wikiSpaceES = wikiSpaceRepository.getWikiSpaceList(
//                                    projectE.getId(), WikiSpaceResourceType.PROJECT.getResourceType());
//                            if (wikiSpaceES == null || wikiSpaceES.isEmpty()) {
//                                WikiSpaceDTO wikiSpaceDTO = new WikiSpaceDTO();
//                                wikiSpaceDTO.setName(organizationE.getName()+ "/" + projectE.getName());
//                                wikiSpaceDTO.setIcon(PROJECT_ICON);
//                                wikiSpaceService.create(wikiSpaceDTO, projectE.getId(), USERNAME,
//                                        WikiSpaceResourceType.PROJECT.getResourceType());
//                                //创建组
//                                WikiGroupDTO wikiGroupDTO = new WikiGroupDTO();
//                                String adminGroupName = "P-" + projectE.getCode() + Stage.ADMIN_GROUP;
//                                String userGroupName = "P-" + projectE.getCode() + Stage.USER_GROUP;
//                                wikiGroupDTO.setGroupName(adminGroupName);
//                                wikiGroupDTO.setProjectCode(projectE.getCode());
//                                wikiGroupDTO.setProjectName(projectE.getName());
//                                wikiGroupDTO.setOrganizationName(organizationE.getName());
//                                wikiGroupService.create(wikiGroupDTO, USERNAME, true, false);
//                                wikiGroupService.setUserToGroup(adminGroupName, projectEvent.getUserId(), USERNAME);
//                                wikiGroupDTO.setGroupName(userGroupName);
//                                wikiGroupService.create(wikiGroupDTO, USERNAME, false, false);
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }
}

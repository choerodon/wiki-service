package io.choerodon.wiki.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.wiki.app.service.WikiScanningService;
import io.choerodon.wiki.infra.common.BaseStage;

/**
 * Created by Zenger on 2018/7/18.
 */
@RestController
@RequestMapping(value = "/v1")
public class WikiScanningController {

    private WikiScanningService wikiScanningService;

    public WikiScanningController(WikiScanningService wikiScanningService) {
        this.wikiScanningService = wikiScanningService;
    }

    /**
     * 同步组织和项目
     *
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "同步组织和项目")
    @GetMapping(value = "/site/scan")
    public ResponseEntity scanning() {
        wikiScanningService.scanning();
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 同步指定组织和项目组织和项目
     *
     * @param organizationId 组织id
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "同步指定组织和项目")
    @GetMapping("/organizations/{organization_id}/space/sync")
    public ResponseEntity syncOrgAndProject(@ApiParam(value = "组织ID", required = true)
                                            @PathVariable(value = "organization_id") Long organizationId) {
        wikiScanningService.syncOrgAndProject(organizationId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 同步指定组织
     *
     * @param organizationId 组织id
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "同步指定组织")
    @GetMapping("/organizations/{organization_id}/space/sync_org")
    public ResponseEntity syncOrg(@ApiParam(value = "组织ID", required = true)
                                  @PathVariable(value = "organization_id") Long organizationId) {
        wikiScanningService.syncOrg(organizationId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 同步指定项目
     *
     * @param projectId 项目id
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "同步指定项目")
    @GetMapping("/projects/{project_id}/space/sync_project")
    public ResponseEntity syncProject(@ApiParam(value = "项目ID", required = true)
                                      @PathVariable(value = "project_id") Long projectId) {
        wikiScanningService.syncProject(projectId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 更新wiki系统主页
     *
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新wiki系统主页")
    @PostMapping(value = "/site/main_page")
    public ResponseEntity update() {
        wikiScanningService.updateWikiPage();
        return new ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * 同步项目下的成员到wiki系统的组织成员组
     *
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "同步项目下的成员到wiki系统的组织成员组")
    @PostMapping(value = "/site/organization_user_group")
    public ResponseEntity syncOrganizationUserGroup() {
        wikiScanningService.syncOrganizationUserGroup();
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 同步平台管理员到wiki的系统管理员
     *
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "同步平台管理员到wiki的系统管理员组")
    @PostMapping(value = "/site/sync_xwiki_admin_group")
    public ResponseEntity syncXWikiAdminGroup() {
        wikiScanningService.syncXWikiAdminGroup();
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 更新wiki组中重复数据和带点用户
     *
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新wiki组中重复数据和带点用户")
    @PostMapping(value = "/wiki/group_users")
    public ResponseEntity updateGrpupUsers() {
        wikiScanningService.updateGrpupUsers();
        return new ResponseEntity(HttpStatus.CREATED);
    }
}

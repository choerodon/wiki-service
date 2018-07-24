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
import io.choerodon.wiki.infra.common.Stage;

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
     * 扫描组织和项目
     *
     * @return DevopsServiceDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    Stage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "扫描组织和项目")
    @GetMapping(value = "/scan")
    public ResponseEntity query() {
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
                    Stage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "同步指定组织和项目组织和项目")
    @GetMapping("/organizations/{organization_id}/spaces/sync_org")
    public ResponseEntity syncOrg(@ApiParam(value = "组织ID", required = true)
                                  @PathVariable(value = "organization_id") Long organizationId) {
        wikiScanningService.syncOrg(organizationId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 删除指定的空间
     *
     * @param id 空间id
     * @return Boolean
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    Stage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "删除指定的空间")
    @DeleteMapping("/spaces/{id}")
    public ResponseEntity<Boolean> deleteSpace(@ApiParam(value = "空间id", required = true)
                                               @PathVariable("id") Long id) {
        return new ResponseEntity<Boolean>(
                wikiScanningService.deleteSpaceById(id), HttpStatus.NO_CONTENT);
    }
}

package io.choerodon.wiki.api.controller.v1;

import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;

/**
 * Created by Zenger on 2018/7/2.
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/space")
public class WikiOrganizationSpaceController {

    private WikiSpaceService wikiSpaceService;

    public WikiOrganizationSpaceController(WikiSpaceService wikiSpaceService) {
        this.wikiSpaceService = wikiSpaceService;
    }

    /**
     * 组织下创建wiki空间
     *
     * @param organizationId 组织id
     * @param wikiSpaceDTO   空间信息
     * @return responseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织下创建wiki空间")
    @PostMapping
    public ResponseEntity create(
            @ApiParam(value = "组织ID", required = true)
            @PathVariable(value = "organization_id") Long organizationId,
            @ApiParam(value = "空间信息", required = true)
            @RequestBody @Valid WikiSpaceDTO wikiSpaceDTO) {

        wikiSpaceService.create(wikiSpaceDTO, organizationId, WikiSpaceResourceType.ORGANIZATION_S.getResourceType());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

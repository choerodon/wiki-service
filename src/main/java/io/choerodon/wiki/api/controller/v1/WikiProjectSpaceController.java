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
@RequestMapping(value = "/v1/projects/{project_id}/space")
public class WikiProjectSpaceController {

    private WikiSpaceService wikiSpaceService;

    public WikiProjectSpaceController(WikiSpaceService wikiSpaceService) {
        this.wikiSpaceService = wikiSpaceService;
    }

    /**
     * 项目下创建wiki空间
     *
     * @param projectId 组织id
     * @param wikiSpaceDTO   空间信息
     * @return responseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目下创建wiki空间")
    @PostMapping
    public ResponseEntity create(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "空间信息", required = true)
            @RequestBody @Valid WikiSpaceDTO wikiSpaceDTO) {

        wikiSpaceService.create(wikiSpaceDTO, projectId, WikiSpaceResourceType.PROJECT_S.getResourceType());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

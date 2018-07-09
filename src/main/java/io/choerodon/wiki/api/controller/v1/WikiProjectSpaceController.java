package io.choerodon.wiki.api.controller.v1;

import java.util.Optional;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;
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
     * 检查项目下空间名唯一性
     *
     * @param projectId 项目ID
     * @param name      空间名
     * @return
     */
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "检查项目下空间名唯一性")
    @GetMapping(value = "/check")
    public ResponseEntity<Boolean> checkName(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "空间名", required = true)
            @RequestParam String name) {
        return Optional.ofNullable(wikiSpaceService.checkName(projectId, name,WikiSpaceResourceType.PROJECT_S.getResourceType()))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.space.name.check"));
    }

    /**
     * 项目下创建wiki空间
     *
     * @param projectId    组织id
     * @param wikiSpaceDTO 空间信息
     * @return responseEntity
     */
    @Permission(level = ResourceLevel.PROJECT)
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

    /**
     * 分页查询项目下创建的空间
     *
     * @param projectId   项目id
     * @param pageRequest 分页参数
     * @param searchParam 查询参数
     * @return Page of WikiSpaceResponseDTO
     */
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "分页查询项目下创建的空间")
    @CustomPageRequest
    @PostMapping(value = "/list_by_options")
    public ResponseEntity<Page<WikiSpaceResponseDTO>> pageByOptions(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "分页参数")
            @ApiIgnore PageRequest pageRequest,
            @ApiParam(value = "查询参数")
            @RequestBody(required = false) String searchParam) {
        return Optional.ofNullable(wikiSpaceService.listWikiSpaceByPage(projectId,
                WikiSpaceResourceType.PROJECT_S.getResourceType(), pageRequest, searchParam))
                .map(target -> new ResponseEntity<>(target, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.wiki.space.query"));
    }

    /**
     * 查询项目下单个wiki空间
     *
     * @param projectId 组织id
     * @param id        空间id
     * @return DevopsServiceDTO
     */
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目下单个wiki空间")
    @GetMapping(value = "/{id}")
    public ResponseEntity<WikiSpaceResponseDTO> query(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "空间ID", required = true)
            @PathVariable Long id) {
        return Optional.ofNullable(wikiSpaceService.query(id))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.wiki.space.query"));
    }

    /**
     * 更新项目下单个空间
     *
     * @param projectId    项目id
     * @param id           空间id
     * @param wikiSpaceDTO 空间信息
     * @return Boolean
     */
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = " 更新项目下单个空间")
    @PutMapping(value = "/{id}")
    public ResponseEntity update(@ApiParam(value = "组织ID", required = true)
                                 @PathVariable(value = "project_id") Long projectId,
                                 @ApiParam(value = "空间ID", required = true)
                                 @PathVariable Long id,
                                 @ApiParam(value = "空间信息", required = true)
                                 @RequestBody @Valid WikiSpaceDTO wikiSpaceDTO) {
        wikiSpaceService.update(id, wikiSpaceDTO, WikiSpaceResourceType.PROJECT_S.getResourceType());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

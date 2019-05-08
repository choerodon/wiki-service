package io.choerodon.wiki.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.wiki.api.dto.MenuDTO;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.infra.common.BaseStage;
import io.choerodon.wiki.infra.common.enums.WikiSpaceResourceType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

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
     * @param projectId 项目id
     * @param name      空间名
     * @return Boolean
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "检查项目下空间名唯一性")
    @GetMapping(value = "/check")
    public ResponseEntity<Boolean> checkName(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "空间名", required = true)
            @RequestParam String name) {
        return new ResponseEntity<>(wikiSpaceService.checkName(
                projectId,
                name,
                WikiSpaceResourceType.PROJECT_S.getResourceType()),
                HttpStatus.OK);
    }

    /**
     * 项目下创建wiki空间
     *
     * @param projectId    项目id
     * @param wikiSpaceDTO 空间信息
     * @return ResponseEntity
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "项目下创建wiki空间")
    @PostMapping
    public ResponseEntity create(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "空间信息", required = true)
            @RequestBody @Valid WikiSpaceDTO wikiSpaceDTO) {
        wikiSpaceService.create(wikiSpaceDTO,
                projectId,
                BaseStage.USERNAME,
                WikiSpaceResourceType.PROJECT_S.getResourceType(), true);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 分页查询项目下创建的空间
     *
     * @param projectId   项目id
     * @param pageRequest 分页参数
     * @param searchParam 查询参数
     * @return Page of WikiSpaceListTreeDTO
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "分页查询项目下创建的空间")
    @CustomPageRequest
    @PostMapping(value = "/list_by_options")
    public ResponseEntity<Page<WikiSpaceListTreeDTO>> pageByOptions(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "分页参数")
            @ApiIgnore PageRequest pageRequest,
            @ApiParam(value = "查询参数")
            @RequestBody(required = false) String searchParam) {
        return new ResponseEntity<>(wikiSpaceService.listTreeWikiSpaceByPage(
                projectId,
                WikiSpaceResourceType.PROJECT.getResourceType(),
                pageRequest,
                searchParam),
                HttpStatus.CREATED);
    }

    /**
     * 查询项目下的wiki空间
     *
     * @param projectId 项目id
     * @return list of wikiSpaceResponseDTO
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询项目下的wiki空间")
    @GetMapping(value = "/under")
    public ResponseEntity<List<WikiSpaceResponseDTO>> underProject(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId) {
        return new ResponseEntity<>(wikiSpaceService.underProject(
                projectId,
                WikiSpaceResourceType.PROJECT_S.getResourceType()),
                HttpStatus.OK);
    }

    /**
     * 查询项目下单个wiki空间
     *
     * @param projectId 项目id
     * @param id        空间id
     * @return WikiSpaceResponseDTO
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询项目下单个wiki空间")
    @GetMapping(value = "/{id}")
    public ResponseEntity<WikiSpaceResponseDTO> query(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "空间ID", required = true)
            @PathVariable Long id) {
        return new ResponseEntity<>(wikiSpaceService.query(id), HttpStatus.OK);
    }

    /**
     * 更新项目下单个空间
     *
     * @param projectId    项目id
     * @param id           空间id
     * @param wikiSpaceDTO 空间信息
     * @return WikiSpaceResponseDTO
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "更新项目下单个空间")
    @PutMapping(value = "/{id}")
    public ResponseEntity<WikiSpaceResponseDTO> update(@ApiParam(value = "项目ID", required = true)
                                                       @PathVariable(value = "project_id") Long projectId,
                                                       @ApiParam(value = "空间ID", required = true)
                                                       @PathVariable Long id,
                                                       @ApiParam(value = "空间信息", required = true)
                                                       @RequestBody @Valid WikiSpaceDTO wikiSpaceDTO) {
        return new ResponseEntity<>(wikiSpaceService.update(
                id,
                wikiSpaceDTO,
                BaseStage.USERNAME),
                HttpStatus.CREATED);
    }

    /**
     * 同步项目下的单个空间
     *
     * @param projectId 项目id
     * @param id        空间id
     * @return ResponseEntity
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "同步项目下的单个空间")
    @PutMapping(value = "/sync/{id}")
    public ResponseEntity sync(
            @ApiParam(value = "项目ID", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "空间ID", required = true)
            @PathVariable Long id) {
        wikiSpaceService.syncProject(id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 删除项目下的空间
     *
     * @param projectId 项目id
     * @param id        空间ID
     * @return ResponseEntity
     */
    @Permission(type = ResourceType.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "删除项目下的空间")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@ApiParam(value = "项目ID", required = true)
                                 @PathVariable(value = "project_id") Long projectId,
                                 @ApiParam(value = "空间ID", required = true)
                                 @PathVariable Long id) {
        wikiSpaceService.delete(projectId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 查询wiki menus列表
     *
     * @param projectId 项目id
     * @param menuDTO
     * @return
     */
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询wiki menus列表")
    @PostMapping("/menus")
    public ResponseEntity<String> queryWikiMenus(@ApiParam(value = "项目ID", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                 @ApiParam(value = "目录", required = true)
                                                 @RequestBody MenuDTO menuDTO) {
        return new ResponseEntity<>(wikiSpaceService.queryWikiMenus(projectId, menuDTO),
                HttpStatus.OK);
    }

}

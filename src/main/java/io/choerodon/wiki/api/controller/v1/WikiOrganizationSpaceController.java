package io.choerodon.wiki.api.controller.v1;

import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.wiki.api.dto.WikiSpaceDTO;
import io.choerodon.wiki.api.dto.WikiSpaceListTreeDTO;
import io.choerodon.wiki.api.dto.WikiSpaceResponseDTO;
import io.choerodon.wiki.app.service.WikiSpaceService;
import io.choerodon.wiki.infra.common.BaseStage;
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
     * 检查组织下空间名唯一性
     *
     * @param organizationId 组织ID
     * @param name           空间名
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "检查组织下空间名唯一性")
    @GetMapping(value = "/check")
    public ResponseEntity<Boolean> checkName(
            @ApiParam(value = "组织ID", required = true)
            @PathVariable(value = "organization_id") Long organizationId,
            @ApiParam(value = "空间名", required = true)
            @RequestParam String name) {
        return new ResponseEntity<>(wikiSpaceService.checkName(
                organizationId,
                name,
                WikiSpaceResourceType.ORGANIZATION_S.getResourceType()),
                HttpStatus.OK);
    }

    /**
     * 组织下创建wiki空间
     *
     * @param organizationId 组织id
     * @param wikiSpaceDTO   空间信息
     * @return responseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "组织下创建wiki空间")
    @PostMapping
    public ResponseEntity create(
            @ApiParam(value = "组织ID", required = true)
            @PathVariable(value = "organization_id") Long organizationId,
            @ApiParam(value = "空间信息", required = true)
            @RequestBody @Valid WikiSpaceDTO wikiSpaceDTO) {
        wikiSpaceService.create(wikiSpaceDTO,
                organizationId,
                BaseStage.USERNAME,
                WikiSpaceResourceType.ORGANIZATION_S.getResourceType());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 分页查询组织下创建的空间
     *
     * @param organizationId 组织id
     * @param pageRequest    分页参数
     * @param searchParam    查询参数
     * @return Page of WikiSpaceListTreeDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "分页查询组织下创建的空间")
    @CustomPageRequest
    @PostMapping(value = "/list_by_options")
    public ResponseEntity<Page<WikiSpaceListTreeDTO>> pageByOptions(
            @ApiParam(value = "组织ID", required = true)
            @PathVariable(value = "organization_id") Long organizationId,
            @ApiParam(value = "分页参数")
            @ApiIgnore PageRequest pageRequest,
            @ApiParam(value = "查询参数")
            @RequestBody(required = false) String searchParam) {
        return new ResponseEntity<>(wikiSpaceService.listTreeWikiSpaceByPage(
                organizationId,
                WikiSpaceResourceType.ORGANIZATION.getResourceType(),
                pageRequest,
                searchParam),
                HttpStatus.CREATED);
    }

    /**
     * 查询组织下单个wiki空间
     *
     * @param organizationId 组织id
     * @param id             空间id
     * @return DevopsServiceDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "查询组织下单个wiki空间")
    @GetMapping(value = "/{id}")
    public ResponseEntity<WikiSpaceResponseDTO> query(
            @ApiParam(value = "组织ID", required = true)
            @PathVariable(value = "organization_id") Long organizationId,
            @ApiParam(value = "空间ID", required = true)
            @PathVariable Long id) {
        return new ResponseEntity<>(wikiSpaceService.query(id), HttpStatus.OK);
    }

    /**
     * 更新组织下单个空间
     *
     * @param organizationId 组织id
     * @param id             空间id
     * @param wikiSpaceDTO   空间信息
     * @return Boolean
     */
    @Permission(level = ResourceLevel.ORGANIZATION,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "更新组织下单个空间")
    @PutMapping(value = "/{id}")
    public ResponseEntity<WikiSpaceResponseDTO> update(@ApiParam(value = "组织ID", required = true)
                                                       @PathVariable(value = "organization_id") Long organizationId,
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
     * 删除组织下的空间
     *
     * @param organizationId 组织id
     * @param id             空间ID
     * @return ResponseEntity
     */
    @Permission(level = ResourceLevel.PROJECT,
            roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR,
                    BaseStage.ORGANIZATION_MEMBER})
    @ApiOperation(value = "删除组织下的空间")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@ApiParam(value = "组织ID", required = true)
                                 @PathVariable(value = "organization_id") Long organizationId,
                                 @ApiParam(value = "空间ID", required = true)
                                 @PathVariable Long id) {
        wikiSpaceService.delete(organizationId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

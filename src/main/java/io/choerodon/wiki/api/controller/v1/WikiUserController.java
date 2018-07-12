package io.choerodon.wiki.api.controller.v1;

import java.util.Optional;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.wiki.api.dto.WikiUserDTO;
import io.choerodon.wiki.app.service.WikiUserService;
import io.choerodon.wiki.infra.common.GetUserNameUtil;

/**
 * Created by Ernst on 2018/7/4.
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/users")
public class WikiUserController {

    private WikiUserService wikiUserService;

    public WikiUserController(WikiUserService wikiUserService) {
        this.wikiUserService = wikiUserService;
    }

    /**
     * 创建wiki用户
     *
     * @param wikiUserDTO 用户信息
     * @return responseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "创建wiki用户")
    @PostMapping
    public ResponseEntity<Boolean> create(
            @ApiParam(value = "用户信息", required = true)
            @RequestBody @Valid WikiUserDTO wikiUserDTO) {

        return Optional.ofNullable(wikiUserService.create(wikiUserDTO, GetUserNameUtil.getUsername()))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.user.create"));
    }

    /**
     * wiki用户是否存在
     *
     * @param userName 用户名
     * @return responseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "wiki用户是否存在")
    @GetMapping("/{user_name}")
    public ResponseEntity<Boolean> checkUser(
            @ApiParam(value = "用户名", required = true)
            @PathVariable String userName) {

        return Optional.ofNullable(wikiUserService.checkUserExsist(userName))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.user.query"));
    }

    /**
     * 删除wiki底下的文档
     *
     * @param pageName 用户名
     * @return responseEntity
     */
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "删除wiki底下的文档")
    @GetMapping("/delete/{page_name}")
    public ResponseEntity<Boolean> delete(
            @ApiParam(value = "page名称", required = true)
            @PathVariable String pageName) {
        return Optional.ofNullable(wikiUserService.deletePage(pageName, GetUserNameUtil.getUsername()))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.page.delete"));
    }
}

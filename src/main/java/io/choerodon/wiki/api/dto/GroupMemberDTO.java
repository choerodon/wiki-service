package io.choerodon.wiki.api.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Ernst on 2018/7/9.
 */
public class GroupMemberDTO {

    private Long userId;

    @ApiModelProperty(value = "用户名/必填")
    private String username;

    @ApiModelProperty(value = "siteId/organizationId/projectId/必填")
    private Long resourceId;

    @ApiModelProperty(value = "site/organization/project/必填")
    private String resourceType;

    @ApiModelProperty(value = "权限类型的label/必填")
    private List<String> roleLabels;

    private String uuid;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public List<String> getRoleLabels() {
        return roleLabels;
    }

    public void setRoleLabels(List<String> roleLabels) {
        this.roleLabels = roleLabels;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}

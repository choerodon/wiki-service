package io.choerodon.wiki.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Zenger on 2018/7/5.
 */
public class WikiSpaceResponseDTO {

    @ApiModelProperty(value = "空间id")
    private Long id;

    @ApiModelProperty(value = "组织id/项目id")
    private Long resourceId;

    @ApiModelProperty(value = "空间类型")
    private String resourceType;

    @ApiModelProperty(value = "空间名称")
    private String name;

    @ApiModelProperty(value = "空间图标")
    private String icon;

    @ApiModelProperty(value = "空间路径")
    private String path;

    @ApiModelProperty(value = "空间状态")
    private String status;

    @ApiModelProperty(value = "审计字段")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}

package io.choerodon.wiki.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Zenger on 2018/7/13.
 */
public class OrganizationDTO {

    @ApiModelProperty(value = "组织id/必填")
    private Long organizationId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}

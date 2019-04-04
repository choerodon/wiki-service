package io.choerodon.wiki.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Zenger on 2018/7/2.
 */
public class WikiSpaceDTO {

    @NotNull
    @Size(min = 1, max = 64, message = "error.icon.size")
    @ApiModelProperty(value = "空间图标/必填")
    private String icon;

    @NotNull
    @Size(min = 1, max = 64, message = "error.name.size")
    @ApiModelProperty(value = "空间名称/必填")
    private String name;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

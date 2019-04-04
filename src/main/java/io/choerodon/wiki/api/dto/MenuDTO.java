package io.choerodon.wiki.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Zenger on 2018/7/13.
 */
public class MenuDTO {

    @ApiModelProperty(value = "文档路径/非必填")
    private String menuId;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}

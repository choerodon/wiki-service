package io.choerodon.wiki.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Zenger on 2018/7/2.
 */
public class WikiSpaceDTO {

    @NotNull
    @Size(min = 1, max = 64, message = "error.icon.size")
    private String icon;

    @NotNull
    @Size(min = 1, max = 128, message = "error.name.size")
    private String name;

    @NotNull
    @Size(min = 1, max = 1000, message = "error.describe.size")
    private String describe;

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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}

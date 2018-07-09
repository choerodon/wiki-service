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

    @Size(min = 0, max = 1000, message = "error.describe.size")
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

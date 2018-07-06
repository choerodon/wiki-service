package io.choerodon.wiki.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Ernst on 2018/7/6.
 */
public class WikiGroupDTO {

    @NotNull
    @Size(min = 1, max = 30, message = "error.group_name.size")
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

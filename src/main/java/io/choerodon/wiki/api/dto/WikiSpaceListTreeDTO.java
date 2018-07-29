package io.choerodon.wiki.api.dto;

import java.util.List;

/**
 * Created by Zenger on 2018/7/28.
 */
public class WikiSpaceListTreeDTO {

    private Long id;
    private Long resourceId;
    private String resourceType;
    private String name;
    private String icon;
    private String path;
    private String status;
    private Long objectVersionNumber;
    private List<WikiSpaceResponseDTO> wikiSpaceResponseDTOList;

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

    public List<WikiSpaceResponseDTO> getWikiSpaceResponseDTOList() {
        return wikiSpaceResponseDTOList;
    }

    public void setWikiSpaceResponseDTOList(List<WikiSpaceResponseDTO> wikiSpaceResponseDTOList) {
        this.wikiSpaceResponseDTOList = wikiSpaceResponseDTOList;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}

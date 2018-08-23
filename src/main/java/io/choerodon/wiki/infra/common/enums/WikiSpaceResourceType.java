package io.choerodon.wiki.infra.common.enums;

/**
 * Created by Zenger on 2018/7/2.
 */
public enum WikiSpaceResourceType {

    /**
     *标记
     */
    ORGANIZATION("organization"),
    ORGANIZATION_S("organization-s"),
    PROJECT("project"),
    PROJECT_S("project-s");

    private String type;

    WikiSpaceResourceType(String type) {
        this.type = type;
    }

    public String getResourceType() {
        return type;
    }

    public static WikiSpaceResourceType forString(String value) {
        switch (value) {
            case "organization":
                return WikiSpaceResourceType.ORGANIZATION;
            case "organization-s":
                return WikiSpaceResourceType.ORGANIZATION_S;
            case "project":
                return WikiSpaceResourceType.PROJECT;
            case "project-s":
                return WikiSpaceResourceType.PROJECT_S;
            default:
                return null;
        }
    }

}

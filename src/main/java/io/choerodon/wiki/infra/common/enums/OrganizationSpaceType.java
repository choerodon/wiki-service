package io.choerodon.wiki.infra.common.enums;

/**
 * Created by Zenger on 2018/7/2.
 */
public enum OrganizationSpaceType {


    PROJECT_OWNER("project.owner"),

    ORGANIZATION_OWNER("organization.owner");

    private String type;

    OrganizationSpaceType(String type) {
        this.type = type;
    }

    public static OrganizationSpaceType forString(String value) {
        switch (value) {
            case "project.owner":
                return OrganizationSpaceType.PROJECT_OWNER;
            case "organization.owner":
                return OrganizationSpaceType.ORGANIZATION_OWNER;
            default:
                return null;
        }
    }

    public String getResourceType() {
        return type;
    }

}

package io.choerodon.wiki.infra.common.enums;

/**
 * Created by Zenger on 2018/7/2.
 */
public enum OrganizationSpaceType {

    /**
     * wiki项目管理员
     */
    PROJECT_WIKI_ADMIN("project.wiki.admin"),

    /**
     * wiki项目成员
     */
    PROJECT_WIKI_USER("project.wiki.user"),

    /**
     * wiki组织管理员
     */
    ORGANIZATION_WIKI_ADMIN("organization.wiki.admin"),

    /**
     * wiki组织成员
     */
    ORGANIZATION_WIKI_USER("organization.wiki.user");



    private String type;

    OrganizationSpaceType(String type) {
        this.type = type;
    }

    public static OrganizationSpaceType forString(String value) {
        switch (value) {
            case "project.wiki.admin":
                return OrganizationSpaceType.PROJECT_WIKI_ADMIN;
            case "project.wiki.user":
                return OrganizationSpaceType.PROJECT_WIKI_USER;
            case "organization.wiki.admin":
                return OrganizationSpaceType.ORGANIZATION_WIKI_ADMIN;
            case "organization.wiki.user":
                return OrganizationSpaceType.ORGANIZATION_WIKI_USER;
            default:
                return null;
        }
    }

    public String getResourceType() {
        return type;
    }

}

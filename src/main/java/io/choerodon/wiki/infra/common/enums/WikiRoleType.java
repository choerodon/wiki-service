package io.choerodon.wiki.infra.common.enums;

/**
 * Created by Zenger on 2018/7/2.
 */
public enum WikiRoleType {

    /**
     * wiki平台管理员
     */
    SITE_ADMIN("site.admin"),

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

    WikiRoleType(String type) {
        this.type = type;
    }

    public String getResourceType() {
        return type;
    }
}

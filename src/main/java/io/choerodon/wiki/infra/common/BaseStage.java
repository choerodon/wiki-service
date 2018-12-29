package io.choerodon.wiki.infra.common;

/**
 * Created by Zenger on 2018/7/18.
 */
public abstract class BaseStage {

    private BaseStage() {

    }

    public static final String APPXML = "Content-Type, application/xml";
    public static final String ERROR_QUERY_GROUP = "error.query.group";
    public static final String USER_GROUP = "UserGroup";
    public static final String ADMIN_GROUP = "AdminGroup";
    public static final String XWIKI_ALL_GROUP = "XWikiAllGroup";
    public static final String XWIKI_ADMIN_GROUP = "XWikiAdminGroup";
    public static final String ORGANIZATION_MEMBER = "role/organization/default/organization-member";
    public static final String USERNAME = "admin";
    public static final String SPACE = "XWiki";
    public static final String WEBHOME = "WebHome";
    public static final String WEBPREFERENCES = "WebPreferences";
    public static final String XWIKIGROUPS = "XWiki.XWikiGroups";
    public static final String XWIKIGLOBALRIGHTS = "XWiki.XWikiGlobalRights";
    public static final String O = "O-";
    public static final String P = "P-";
    public static final String LINE = "-";
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    public static final int NO_CONTENT = 204;
    public static final int NOT_FOUND = 404;
}

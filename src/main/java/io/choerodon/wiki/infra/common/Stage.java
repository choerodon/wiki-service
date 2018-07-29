package io.choerodon.wiki.infra.common;

/**
 * Created by Zenger on 2018/7/18.
 */
public abstract class Stage {

    private Stage(){

    }

    public static final String APPXML = "Content-Type, application/xml";
    public static final String ERROR_QUERY_GROUP = "error.query.group";
    public static final String USER_GROUP = "UserGroup";
    public static final String ADMIN_GROUP = "AdminGroup";
    public static final String ORGANIZATION_MEMBER = "role/organization/default/organization-member";
    public static final String SPACES = "spaces";
    public static final String SPACE = "XWiki";
    public static final String WEBHOME = "WebHome";
    public static final String WEBPREFERENCES = "WebPreferences";
    public static final String PAGE = "页面";
    public static final String XWIKIGROUPS = "XWiki.XWikiGroups";
    public static final String XWIKIGLOBALRIGHTS = "XWiki.XWikiGlobalRights";
}

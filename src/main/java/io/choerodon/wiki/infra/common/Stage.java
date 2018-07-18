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
}

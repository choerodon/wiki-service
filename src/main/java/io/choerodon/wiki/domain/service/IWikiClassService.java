package io.choerodon.wiki.domain.service;

/**
 * Created by Zenger on 2018/7/11.
 */
public interface IWikiClassService {

    String getPageClassResource(String space, String pageName, String className, String username);

    String getProjectPageClassResource(String org, String project, String pageName, String className, String username);

    void deletePageClass(String username, String space, String name, String className, int objectNumber);

    void deleteProjectPageClass(String username, String org, String project, String name, String className, int objectNumber);

    String getOrgPageClassGroupResource(String org, String pageName, String className, String username, int objectNumber);

    String getProjectPageClassGroupResource(String org, String project, String pageName, String className, String username, int objectNumber);
}

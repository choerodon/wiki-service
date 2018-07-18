package io.choerodon.wiki.domain.application.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.choerodon.wiki.domain.application.entity.iam.OrganizationE;

/**
 * Created by Zenger on 2018/3/28.
 */
@Component
@Scope("prototype")
public class ProjectE {
    private Long id;
    private String name;
    private String code;
    private OrganizationE organization;


    public ProjectE() {
    }

    public ProjectE(Long id) {
        this.id = id;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrganizationE getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationE organization) {
        this.organization = organization;
    }

    public void initOrganizationE(Long id){
        organization = new OrganizationE(id);
    }
}

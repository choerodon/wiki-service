package io.choerodon.wiki.domain.application.entity.iam;

/**
 * Created by ernst on 2018/7/9.
 */
public class OrganizationE {
    private Long id;
    private String name;
    private String code;

    public OrganizationE() {
    }

    public OrganizationE(Long id) {
        this.id = id;
    }

    public OrganizationE(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
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
}

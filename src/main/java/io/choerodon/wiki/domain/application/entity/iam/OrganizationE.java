package io.choerodon.wiki.domain.application.entity.iam;

/**
 * Created by ernst on 2018/7/9.
 */
public class OrganizationE {

    private Long id;
    private String name;
    private String code;
    private Boolean enabled;
    private Long projectCount;

    public OrganizationE() {
    }

    public OrganizationE(Long id) {
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Long projectCount) {
        this.projectCount = projectCount;
    }

    @Override
    public String toString() {
        return "OrganizationE{" +
                "id=" + id +
                ", name=" + name +
                ", code=" + code +
                ", enabled=" + enabled +
                ", projectCount=" + projectCount +
                '}';
    }
}

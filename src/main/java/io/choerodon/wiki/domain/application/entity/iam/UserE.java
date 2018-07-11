package io.choerodon.wiki.domain.application.entity.iam;

public class UserE {

    private Long id;
    private String loginName;
    private String email;
    private String realName;
    private OrganizationE organization;

    public UserE(Long id, String loginName, String email, String realName) {
        this.id = id;
        this.loginName = loginName;
        this.email = email;
        this.realName = realName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public OrganizationE getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationE organization) {
        this.organization = organization;
    }
}

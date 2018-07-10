package io.choerodon.wiki.domain.application.valueobject;

/**
 * Created by ernst on 2018/7/9.
 */
public class Organization {
    private Long id;
    private String name;
    private String code;

    public Organization() {
    }

    public Organization(Long id) {
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
}

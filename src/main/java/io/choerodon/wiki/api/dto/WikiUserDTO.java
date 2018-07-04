package io.choerodon.wiki.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Ernst on 2018/7/4.
 */
public class WikiUserDTO {

    @NotNull
    @Size(min = 1, max = 30, message = "error.first_name.size")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 30, message = "error.last_name.size")
    private String lastName;

    @NotNull
    @Size(min = 1, max = 30, message = "error.user_name.size")
    private String userName;

    @NotNull
    @Size(min = 1, max = 30, message = "error.password.size")
    private String password;

    @NotNull
    @Size(min = 1, max = 30, message = "error.email.size")
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

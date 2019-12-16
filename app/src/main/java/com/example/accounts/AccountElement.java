package com.example.accounts;

import java.io.Serializable;
import java.util.Objects;

public class AccountElement implements Serializable {
    private String email;
    private String user;
    private String password;

    public AccountElement(String email, String user, String password) {
        this.email = email;
        this.user = user;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountElement that = (AccountElement) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(user, that.user) &&
                Objects.equals(password, that.password);
    }

}

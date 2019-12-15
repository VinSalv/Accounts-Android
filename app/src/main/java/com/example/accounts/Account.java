package com.example.accounts;

import java.io.Serializable;

public class Account implements Serializable {
    private String owner;
    private String name;
    private String email;
    private String user;
    private String password;
    private boolean isChecked;

    public Account(String owner, String name, String email, String user, String password, boolean isChecked) {
        this.owner = owner;
        this.name = name;
        this.email = email;
        this.user = user;
        this.password = password;
        this.isChecked = isChecked;
    }

    public boolean isSelected() {
        return isChecked;
    }

    public void setSelected(boolean selected) {
        isChecked = selected;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account() {
        this.owner = "";
        this.name = "";
        this.email = "";
        this.user = "";
        this.password = "";
    }

    public String getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object object) {
        boolean bool = false;
        if (object != null && object instanceof Account) {
            bool = (((this.getOwner().equals((((Account) object).getOwner()))) && (this.getName().equals(((Account) object).getName()))));
        }
        return bool;
    }
}

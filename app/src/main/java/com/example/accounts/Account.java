package com.example.accounts;

import java.io.Serializable;

public class Account implements Serializable {
    private String owner;
    private String name;
    private String email;
    private String user;
    private String password;

    public Account(String owner, String name, String email, String user, String password) {
        this.owner = owner;
        this.name = name;
        this.email = email;
        this.user = user;
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

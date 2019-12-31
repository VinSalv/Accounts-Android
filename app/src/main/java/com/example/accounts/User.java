package com.example.accounts;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String password;
    private boolean finger;

    User(String user, String password, boolean finger) {
        this.user = user;
        this.password = password;
        this.finger = finger;
    }

    User() {
        this.user = "";
        this.password = "";
        this.finger = false;
    }

    public String getUser() {
        return this.user;
    }

    String getPassword() {
        return this.password;
    }


    boolean getFinger() {
        return this.finger;
    }

    @Override
    public boolean equals(Object object) {
        boolean bool = false;
        if (object instanceof User) {
            bool = (this.getUser().equals(((User) object).getUser()));
        }
        return bool;
    }
}

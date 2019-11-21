package com.example.accounts;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String password;
    private boolean finger;

    public User(String user, String password, boolean finger) {
        this.user = user;
        this.password = password;
        this.finger = finger;
    }

    public User() {
        this.user = "";
        this.password = "";
        this.finger = false;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }


    public boolean getFinger() {
        return this.finger;
    }


    public void setFinger(boolean finger) {
        this.finger = finger;
    }

    @Override
    public boolean equals(Object object) {
        boolean bool = false;
        if (object != null && object instanceof User) {
            bool = (this.getUser().equals(((User) object).getUser()));
        }
        return bool;
    }
}

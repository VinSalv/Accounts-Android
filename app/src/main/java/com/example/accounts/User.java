package com.example.accounts;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String password;
    private boolean priority;
    private boolean finger;

    public User(String user, String password, boolean priority, boolean finger) {
        this.user = user;
        this.password = password;
        this.priority = priority;
        this.finger = finger;
    }

    public User() {
        this.user = "";
        this.password = "";
        this.priority = false;
        this.finger = false;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean getPriority() {
        return this.priority;
    }

    public boolean getFinger() {
        return this.priority;
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

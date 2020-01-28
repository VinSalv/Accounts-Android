package com.example.accounts;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String password;
    private boolean finger;
    private int sort;

    public User(String user, String password, boolean finger, int sort) {
        this.user = user;
        this.password = password;
        this.finger = finger;
        this.sort = sort;
    }

    public User(String user, String password) {
        this.user = user;
        this.password = password;
        finger = false;
        sort = 0;
    }

    int getSort() {
        return sort;
    }

    void setSort(int sort) {
        this.sort = sort;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    boolean getFinger() {
        return this.finger;
    }

    void setFinger(boolean finger) {
        this.finger = finger;
    }

    @Override
    public boolean equals(Object object) {
        boolean bool = false;
        if (object instanceof User) {
            bool = (this.getUser().toLowerCase().equals(((User) object).getUser().toLowerCase()));
        }
        return bool;
    }
}

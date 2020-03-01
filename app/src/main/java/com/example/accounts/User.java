package com.example.accounts;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String password;
    private boolean finger;
    private int sort;
    private int colCat;
    private int colAcc;

    public User(String user, String password, boolean finger, int sort, int colCat, int colAcc) {
        this.user = user;
        this.password = password;
        this.finger = finger;
        this.sort = sort;
        this.colCat = colCat;
        this.colAcc = colAcc;
    }

    public User(String user, String password, boolean finger) {
        this.user = user;
        this.password = password;
        this.finger = finger;
        this.sort = 1;
        this.colCat = 2;
        this.colAcc = 1;
    }

    public User(String user, String password, boolean finger, int sort) {
        this.user = user;
        this.password = password;
        this.finger = finger;
        this.sort = sort;
        this.colCat = 2;
        this.colAcc = 1;
    }

    public User(String user, String password) {
        this.user = user;
        this.password = password;
        this.finger = false;
        this.sort = 1;
        this.colCat = 2;
        this.colAcc = 1;
    }

    public boolean isFinger() {
        return finger;
    }

    public int getColCat() {
        return colCat;
    }

    public void setColCat(int colCat) {
        this.colCat = colCat;
    }

    public int getColAcc() {
        return colAcc;
    }

    public void setColAcc(int colAcc) {
        this.colAcc = colAcc;
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

    String getPassword() {
        return this.password;
    }

    void setPassword(String password) {
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

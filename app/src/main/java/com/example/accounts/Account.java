package com.example.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private String owner;
    private String name;
    private List<AccountElement> list;

    public Account(String owner, String name, ArrayList<AccountElement> list) {
        this.owner = owner;
        this.name = name;
        this.list = list;
    }

    public Account() {
        this.owner = "";
        this.name = "";
        this.list = new ArrayList<AccountElement>();
    }

    public List<AccountElement> getList() {
        return list;
    }

    public void setList(List<AccountElement> list) {
        this.list = list;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object object) {
        boolean bool = false;
        if (object != null && object instanceof Account) {
            bool = (((this.getName().equals(((Account) object).getName()))));
        }
        return bool;
    }

    public boolean equals(String str) {
        if (this.getName().compareTo(str) == 0) {
            return true;
        }
        return false;
    }

}

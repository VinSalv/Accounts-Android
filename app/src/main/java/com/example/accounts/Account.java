package com.example.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private String owner;
    private String name;
    private List<AccountElement> list;

    Account(String owner, String name, ArrayList<AccountElement> list) {
        this.owner = owner;
        this.name = name;
        this.list = list;
    }

    Account() {
        this.owner = "";
        this.name = "";
        this.list = new ArrayList<>();
    }

    List<AccountElement> getList() {
        return list;
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
        if (object instanceof Account) {
            bool = (((this.getName().toLowerCase().equals(((Account) object).getName().toLowerCase()))));
        }
        return bool;
    }

    boolean equals(String str) {
        return this.getName().toLowerCase().compareTo(str.toLowerCase()) == 0;
    }
}

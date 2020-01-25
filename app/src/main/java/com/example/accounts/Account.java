package com.example.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private String name;
    private List<AccountElement> list;

    Account(String name, ArrayList<AccountElement> list) {
        this.name = name;
        this.list = list;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    List<AccountElement> getList() {
        return list;
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

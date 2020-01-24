package com.example.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Acc implements Serializable {
    private String name;
    private List<AccountElement> list;
    private boolean isSelected;

    Acc(String name, ArrayList<AccountElement> list) {
        this.name = name;
        this.list = list;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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
        if (object instanceof Acc) {
            bool = (((this.getName().toLowerCase().equals(((Acc) object).getName().toLowerCase()))));
        }
        return bool;
    }

    boolean equals(String str) {
        return this.getName().toLowerCase().compareTo(str.toLowerCase()) == 0;
    }
}

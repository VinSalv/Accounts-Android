package com.example.accounts;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
    private String cat;
    private ArrayList<Account> listAcc;
    private int sort;

    public Category(String cat, ArrayList<Account> listAcc, int sort) {
        this.cat = cat;
        this.listAcc = listAcc;
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public ArrayList<Account> getListAcc() {
        return listAcc;
    }

    public void setListAcc(ArrayList<Account> listAcc) {
        this.listAcc = listAcc;
    }
}

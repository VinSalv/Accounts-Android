package com.app.accounts;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
    private String cat;
    private ArrayList<Account> listAcc;
    private int sort;

    public Category(String cat, int sort) {
        this.cat = cat;
        listAcc = new ArrayList<>();
        this.sort = sort;
    }

    public Category(String cat, ArrayList<Account> listAcc, int sort) {
        this.cat = cat;
        this.listAcc = listAcc;
        this.sort = sort;
    }

    int getSort() {
        return sort;
    }

    void setSort(int sort) {
        this.sort = sort;
    }

    public String getCat() {
        return cat;
    }

    void setCat(String cat) {
        this.cat = cat;
    }

    ArrayList<Account> getListAcc() {
        return listAcc;
    }

    void setListAcc(ArrayList<Account> listAcc) {
        this.listAcc = listAcc;
    }
}

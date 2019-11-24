package com.example.accounts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ManageAccount implements Serializable {

    public ManageAccount() {
    }

    public boolean search(Account a, ArrayList<Account> list) {
        return list.contains(a);
    }

    public void serializationListAccount(ArrayList<Account> list, String path, String owner) {
        try {
            File file = new File(path + "/Accounts" + owner + ".txt");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Account> deserializationListAccount(String path, String owner) {
        try {
            File file = new File(path + "/Accounts" + owner + ".txt");
            FileInputStream door = new FileInputStream(file);
            ObjectInputStream reader = new ObjectInputStream(door);
            ArrayList<Account> x;
            x = (ArrayList<Account>) reader.readObject();
            return x;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<Account>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<Account>();
        }
    }
}
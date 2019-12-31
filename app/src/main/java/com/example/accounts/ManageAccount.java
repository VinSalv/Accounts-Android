package com.example.accounts;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

class ManageAccount implements Serializable {

    ManageAccount() {
    }

    boolean search(Account a, ArrayList<Account> list) {
        return list.contains(a);
    }

    void serializationListAccount(Context context, ArrayList<Account> list, String owner) {
        try {
            FileOutputStream fos = context.openFileOutput("Accounts" + owner + ".txt", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<Account> deserializationListAccount(Context context, String owner) {
        try {
            FileInputStream fis = context.openFileInput("Accounts" + owner + ".txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<Account> x = (ArrayList<Account>) is.readObject();
            is.close();
            fis.close();
            return x;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
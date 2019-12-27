package com.example.accounts;

import android.content.Context;

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

    public void serializationListAccount(Context context, ArrayList<Account> list, String owner) {
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

    public ArrayList<Account> deserializationListAccount(Context context, String owner) {
        try {
            FileInputStream fis = context.openFileInput("Accounts" + owner + ".txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<Account> x = (ArrayList<Account>) is.readObject();
            is.close();
            fis.close();
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
package com.example.accounts;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ManageUser implements Serializable {

    public ManageUser() {
    }

    public boolean search(User u, ArrayList<User> listUser) {
        for (User usr : listUser) {
            if (usr.getUser().equals(u.getUser())) return true;
        }
        return false;
    }

    public boolean login(User u, ArrayList<User> listUser) {
        for (User usr : listUser) {
            if (usr.getUser().equals(u.getUser()) && usr.getPassword().equals(u.getPassword()))
                return true;
        }
        return false;
    }

    public void serializationListUser(Context context, ArrayList<User> list) {
        try {
            FileOutputStream fos = context.openFileOutput("Users.txt", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> deserializationListUser(Context context) {
        try {
            FileInputStream fis = context.openFileInput("Users.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<User> x = (ArrayList<User>) is.readObject();
            is.close();
            fis.close();
            return x;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<User>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<User>();
        }
    }
}
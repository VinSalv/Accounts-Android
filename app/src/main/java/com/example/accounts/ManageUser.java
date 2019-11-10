package com.example.accounts;

import java.io.File;
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
        return listUser.contains(u);
    }

    public boolean login(User u, ArrayList<User> listUser) {
        for (User usr : listUser) {
            if (usr.getUser().equals(u.getUser()) && usr.getPassword().equals(u.getPassword()))
                return false;
        }
        return true;
    }

    public void serializationListUser(ArrayList<User> list, String path) {
        try {
            File file = new File(path + "/Users.txt");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> deserializationListUser(String path) {
        try {
            File file = new File(path + "/Users.txt");
            FileInputStream door = new FileInputStream(file);
            ObjectInputStream reader = new ObjectInputStream(door);
            ArrayList<User> x;
            x = (ArrayList<User>) reader.readObject();
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
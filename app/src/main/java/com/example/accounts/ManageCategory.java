package com.example.accounts;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ManageCategory {
    ManageCategory() {
    }

    void serializationListCategory(Context context, ArrayList<Category> list, String owner) {
        try {
            FileOutputStream fos = context.openFileOutput("Category" + owner + ".txt", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<Category> deserializationListCategory(Context context, String owner) {
        try {
            FileInputStream fis = context.openFileInput("Category" + owner + ".txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked") ArrayList<Category> x = (ArrayList<Category>) is.readObject();
            is.close();
            fis.close();
            return x;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    void removeFileCategory(Context context, String owner) {
        try {
            context.deleteFile("Category" + owner + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Category findCategory(ArrayList<Category> list, String sCat) {
        for (Category cat : list)
            if (cat.getCat().toLowerCase().equals(sCat.toLowerCase()))
                return cat;
        return null;
    }
}

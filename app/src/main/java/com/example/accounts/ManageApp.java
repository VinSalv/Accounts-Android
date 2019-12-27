package com.example.accounts;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ManageApp implements Serializable {

    public ManageApp() {
    }


    public void serializationFlag(Context context, LogApp logApp) {
        try {
            FileOutputStream fos = context.openFileOutput("LogApp.txt", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(logApp);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LogApp deserializationFlag(Context context) {
        try {
            FileInputStream fis = context.openFileInput("LogApp.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            LogApp logApp = (LogApp) is.readObject();
            is.close();
            fis.close();
            return logApp;
        } catch (IOException e) {
            e.printStackTrace();
            return new LogApp();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new LogApp();
        }
    }
}

package com.example.accounts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ManageApp implements Serializable {

    public ManageApp() {
    }


    public void serializationFlag(LogApp logApp, String path) {
        try {
            File file = new File(path + "/LogApp.txt");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(logApp);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LogApp deserializationFlag(String path) {
        try {
            File file = new File(path + "/LogApp.txt");
            FileInputStream door = new FileInputStream(file);
            ObjectInputStream reader = new ObjectInputStream(door);
            LogApp logApp;
            logApp = (LogApp) reader.readObject();
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

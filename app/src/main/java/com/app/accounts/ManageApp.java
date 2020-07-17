package com.app.accounts;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class ManageApp implements Serializable {

    ManageApp() {
    }

    void serializationFlag(Context context, LogApp logApp) {
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

    LogApp deserializationFlag(Context context) {
        try {
            FileInputStream fis = context.openFileInput("LogApp.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            LogApp logApp = (LogApp) is.readObject();
            is.close();
            fis.close();
            return logApp;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new LogApp();
        }
    }
}

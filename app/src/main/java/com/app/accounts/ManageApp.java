package com.app.accounts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("ResultOfMethodCallIgnored")
class ManageApp implements Serializable {

    ManageApp() {
    }

    private static void execCryptDecrypt(int cipherMode,
                                         File inputFile, File outputFile) {
        try {
            Key secretKey = new SecretKeySpec("dfgfdgdfgfdlwerknwkfjewh".getBytes(), "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);
            byte[] inputBytes;
            try (FileInputStream inputStream = new FileInputStream(inputFile)) {
                inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);
            }
            byte[] outputBytes = cipher.doFinal(inputBytes);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(outputBytes);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        }
    }

    void serializationFlag(Context context, LogApp logApp) {
        try {
            String rootPath = context.getExternalFilesDir(null) + "/Accounts/LogApp";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            File f = new File(rootPath + "/LogApp.txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(logApp);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            execCryptDecrypt(Cipher.ENCRYPT_MODE,
                    new File(context.getExternalFilesDir(null), "/Accounts/LogApp/LogApp.txt"),
                    new File(context.getExternalFilesDir(null), "/Accounts/LogApp/LogApp.txt"));
        }
    }

    LogApp deserializationFlag(Context context) {
        try {
            File f = new File(context.getExternalFilesDir(null) + "/Accounts/LogApp/LogApp.txt");
            execCryptDecrypt(Cipher.DECRYPT_MODE, f, f);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream is = new ObjectInputStream(fis);
            LogApp x = (LogApp) is.readObject();
            is.close();
            fis.close();
            return x;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new LogApp();
        } finally {
            execCryptDecrypt(Cipher.ENCRYPT_MODE,
                    new File(context.getExternalFilesDir(null), "/Accounts/LogApp/LogApp.txt"),
                    new File(context.getExternalFilesDir(null), "/Accounts/LogApp/LogApp.txt"));
        }
    }
}
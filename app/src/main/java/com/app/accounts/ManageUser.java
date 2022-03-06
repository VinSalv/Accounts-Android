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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("ResultOfMethodCallIgnored")
class ManageUser implements Serializable {

    public ManageUser() {
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

    boolean notFindUser(User u, ArrayList<User> listUser) {
        for (User usr : listUser) {
            if (usr.getUser().toLowerCase().equals(u.getUser().toLowerCase())) return false;
        }
        return true;
    }

    boolean login(User u, ArrayList<User> listUser) {
        for (User usr : listUser) {
            if (usr.getUser().toLowerCase().equals(u.getUser().toLowerCase()) && usr.getPassword().equals(u.getPassword()))
                return true;
        }
        return false;
    }

    void serializationListUser(Context context, ArrayList<User> list) {
        try {
            String rootPath = context.getExternalFilesDir(null) + "/Accounts/Utenti";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            File f = new File(rootPath + "/Users.txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(increasing(list));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            execCryptDecrypt(Cipher.ENCRYPT_MODE,
                    new File(context.getExternalFilesDir(null), "/Accounts/Utenti/Users.txt"),
                    new File(context.getExternalFilesDir(null), "/Accounts/Utenti/Users.txt"));
        }

    }

    public ArrayList<User> deserializationListUser(Context context) {
        try {
            File f = new File(context.getExternalFilesDir(null) + "/Accounts/Utenti/Users.txt");
            execCryptDecrypt(Cipher.DECRYPT_MODE, f, f);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream is = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked") ArrayList<User> x = (ArrayList<User>) is.readObject();
            is.close();
            fis.close();
            return x;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            execCryptDecrypt(Cipher.ENCRYPT_MODE,
                    new File(context.getExternalFilesDir(null), "/Accounts/Utenti/Users.txt"),
                    new File(context.getExternalFilesDir(null), "/Accounts/Utenti/Users.txt"));
        }
    }

    User findUser(ArrayList<User> list, String user) {
        for (User u : list)
            if (u.getUser().toLowerCase().equals(user.toLowerCase())) return u;
        return null;
    }

    public ArrayList<User> increasing(ArrayList<User> list) {
        Collections.sort(list, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getUser().toLowerCase().compareTo(rhs.getUser().toLowerCase());
            }
        });
        return list;
    }
}
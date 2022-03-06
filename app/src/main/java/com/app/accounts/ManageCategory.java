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
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ManageCategory {
    ManageCategory() {
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

    void serializationListCategory(Context context, ArrayList<Category> list, String owner) {
        try {
            String rootPath = context.getExternalFilesDir(null) + "/Accounts/Categorie";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            File f = new File(rootPath + "/" + owner + ".txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(list);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            execCryptDecrypt(Cipher.ENCRYPT_MODE,
                    new File(context.getExternalFilesDir(null), "/Accounts/Categorie/" + owner + ".txt"),
                    new File(context.getExternalFilesDir(null), "/Accounts/Categorie/" + owner + ".txt"));
        }
    }

    ArrayList<Category> deserializationListCategory(Context context, String owner) {
        try {
            File f = new File(context.getExternalFilesDir(null) + "/Accounts/Categorie/" + owner + ".txt");
            execCryptDecrypt(Cipher.DECRYPT_MODE, f, f);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream is = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked") ArrayList<Category> x = (ArrayList<Category>) is.readObject();
            is.close();
            fis.close();
            return x;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            execCryptDecrypt(Cipher.ENCRYPT_MODE,
                    new File(context.getExternalFilesDir(null), "/Accounts/Categorie/" + owner + ".txt"),
                    new File(context.getExternalFilesDir(null), "/Accounts/Categorie/" + owner + ".txt"));
        }
    }

    void removeFileCategory(Context context, String owner) {
        try {
            context.deleteFile("Category" + owner + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Boolean findCategory(ArrayList<Category> list, String sCat) {
        for (Category cat : list)
            if (cat.getCat().toLowerCase().equals(sCat.toLowerCase()))
                return true;
        return false;
    }

    Category findAndGetCategory(ArrayList<Category> list, String sCat) {
        for (Category cat : list)
            if (cat.getCat().toLowerCase().equals(sCat.toLowerCase()))
                return cat;
        return null;
    }

    Account findAndGetAccount(ArrayList<Account> list, String name) {
        for (Account a : list)
            if (a.getName().toLowerCase().equals(name.toLowerCase()))
                return a;
        return null;
    }

    boolean findAccount(ArrayList<Account> list, String name) {
        for (Account a : list)
            if (a.getName().toLowerCase().equals(name.toLowerCase()))
                return true;
        return false;
    }

    boolean accountNotFound(Account acc, ArrayList<Account> list) {
        for (Account a : list)
            if (a.getName().toLowerCase().equals(acc.getName().toLowerCase()))
                return false;
        return true;
    }
}
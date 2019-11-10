package com.example.accounts;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.security.KeyStore;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private KeyguardManager key;
    private FingerprintManager finger;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private String KEY_NAME = "somekeyname";
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private RelativeLayout lay;
    private EditText userApp;
    private ImageView userError;
    private EditText passApp;
    private ImageView passError;
    private Button login;
    private TextView sign;
    private Switch flagApp;
    private String path;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lay = findViewById(R.id.RelLayMain);
        path = getExternalStorageDirectory().getAbsolutePath();

        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
            }
        }

        userApp = findViewById(R.id.userApp);
        userError = findViewById(R.id.userError);
        passApp = findViewById(R.id.passApp);
        passError = findViewById(R.id.passError);
        login = findViewById(R.id.authButton);
        sign = findViewById(R.id.signText);
        flagApp = findViewById(R.id.flagApp);

        mngApp = new ManageApp();
        LogApp log = mngApp.deserializationFlag(path);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(path);

        if (log.getFlagApp() == true) {
            flagApp.setChecked(true);
            User usr = new User();
            for (User u : listUser) {
                if (u.getPriority() == true) usr = u;
            }
            if (usr.getFinger() == true) {
                if (checkLockScreen()) {
                    generateKey();
                    if (initCipher()) {
                        cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    }
                    CancellationSignal cancellationSignal = new CancellationSignal();
                    if (finger != null && cryptoObject != null) {
                        finger.authenticate(cryptoObject, cancellationSignal, 0, new AuthenticationHandler(this), null);
                    }
                }
                listUser = mngUsr.deserializationListUser(path);
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("owner", usr.getUser());
                startActivity(intent);
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userError.setVisibility(View.INVISIBLE);
                passError.setVisibility(View.INVISIBLE);

                if (!fieldCheck(new User(userApp.getText().toString(), passApp.getText().toString(), false, false)))
                    return;

                if (!mngUsr.login(new User(userApp.getText().toString(), passApp.getText().toString(), false, false), listUser)) {
                    listUser = mngUsr.deserializationListUser(path);
                    User usr = new User();
                    for (User u : listUser) {
                        if (u.getPriority() == true) usr = u;
                    }
                    if (usr.getFinger() == true) {
                        if (checkLockScreen()) {
                            generateKey();
                            if (initCipher()) {
                                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            }
                            CancellationSignal cancellationSignal = new CancellationSignal();
                            if (finger != null && cryptoObject != null) {
                                finger.authenticate(cryptoObject, cancellationSignal, 0, new AuthenticationHandler(MainActivity.this), null);
                            }
                        }
                    }
                    LogApp log = new LogApp(flagApp.isChecked());
                    mngApp.serializationFlag(log, path);
                    Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                    intent.putExtra("path", path);
                    intent.putExtra("owner", usr.getUser());
                    startActivity(intent);
                } else {
                    userError.setVisibility(View.VISIBLE);
                    passError.setVisibility(View.VISIBLE);
                    Snackbar.make(view, "Autenticazione errata", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean fieldCheck(User usr) {
        if (!isValidWord(usr.getUser()) && !isValidWord(usr.getPassword())) {
            userError.setVisibility(View.VISIBLE);
            passError.setVisibility(View.VISIBLE);
            Snackbar.make(lay, "Campi Utente e Password non validi !!!", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (!isValidWord(usr.getUser())) {
            userError.setVisibility(View.VISIBLE);
            Snackbar.make(lay, "Campo Utente non valido !!!", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (!isValidWord(usr.getPassword())) {
            passError.setVisibility(View.VISIBLE);
            Snackbar.make(lay, "Campo Password non valido !!!", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean isValidWord(String word) {
        return ((word.matches("[A-Za-z0-9?!_.-]*")) && (!word.isEmpty()));
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {
                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
    }

    private boolean checkLockScreen() {

        key = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        finger = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (!finger.isHardwareDetected()) {
            Snackbar.make(findViewById(R.id.RelLayMain), "Lettore impronta digitale assente.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(findViewById(R.id.RelLayMain), "Lettura impronta non autorizzata", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (key.isKeyguardSecure() == false) {
            Snackbar.make(findViewById(R.id.RelLayMain), "Lock screen security non abilitato", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (finger.hasEnrolledFingerprints() == false) {
            Snackbar.make(findViewById(R.id.RelLayMain), "Nessuna impronta registrata sul dispositivo", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.RelLayMain), e.getMessage(), Snackbar.LENGTH_LONG).show();
            return false;
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.RelLayMain), e.getMessage(), Snackbar.LENGTH_LONG).show();

            return false;
        }

        try {
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
            keyGenerator.generateKey();
            return true;
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.RelLayMain), e.getMessage(), Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES
                    + "/" + KeyProperties.BLOCK_MODE_CBC
                    + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.RelLayMain), e.getMessage(), Snackbar.LENGTH_LONG).show();
            return false;
        }

        try {
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.RelLayMain), e.getMessage(), Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}


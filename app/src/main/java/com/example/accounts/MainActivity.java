package com.example.accounts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private RelativeLayout lay;
    private EditText userApp;
    private EditText passApp;
    private Switch flagApp;
    private ManageApp mngApp;
    private LogApp log;
    private ManageUser mngUsr;
    private ArrayList<User> listUser;

    @RequiresApi(api = Build.VERSION_CODES.P)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
            }
        }
        lay = findViewById(R.id.mainActivityLay);
        userApp = findViewById(R.id.userApp);
        passApp = findViewById(R.id.passApp);
        flagApp = findViewById(R.id.flagApp);
        Button login = findViewById(R.id.authButton);
        TextView sign = findViewById(R.id.signButton);
        ImageButton showPass = findViewById(R.id.showPass);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        if (log.getFlagApp()) {
            flagApp.setChecked(true);
            User usr = mngUsr.findUser(listUser, log.getUser());
            if (usr != null) {
                userApp.setText(usr.getUser());
                if (usr.getFinger()) {
                    biometricAuthentication(lay);
                } else {
                    goToViewActivity(usr);
                }
            } else {
                notifyUser("Impossibile restare connesso");
            }
        }
        showPass(passApp, showPass);
        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                userApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                passApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));

                User usr = new User(userApp.getText().toString(), passApp.getText().toString(), false, 0);
                if (!fieldCheck(usr)) return;
                if (mngUsr.login(usr, listUser)) {
                    usr = mngUsr.findUser(listUser, userApp.getText().toString());
                    if (usr.getFinger()) {
                        biometricAuthentication(lay);
                    } else {
                        log = new LogApp(flagApp.isChecked(), fixName(userApp.getText().toString()));
                        mngApp.serializationFlag(MainActivity.this, log);
                        goToViewActivity(usr);
                    }
                } else {
                    userApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
                    passApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
                    notifyUser("Autenticazione errata");
                }
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignActivity();
            }
        });
    }

    public void goToViewActivity(User usr) {
        Intent intent = new Intent(MainActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void goToSignActivity() {
        Intent intent = new Intent(MainActivity.this, SignActivity.class);
        startActivity(intent);
    }

    public String fixName(String name) {
        name = name.toLowerCase();
        String name1 = name.substring(0, 1).toUpperCase();
        String name2 = name.substring(1).toLowerCase();
        return name1.concat(name2);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                notifyUser("Autenticazione errore: " + errString);
                super.onAuthenticationError(errorCode, errString);
                log = new LogApp();
                mngApp.serializationFlag(MainActivity.this, log);
                goToMainActivity();
                passApp.setText("");
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                notifyUser("Autenticazione effettuata");
                super.onAuthenticationSucceeded(result);
                User usr = new User(userApp.getText().toString(), passApp.getText().toString(), true, 1);
                if (flagApp.isChecked()) {
                    log = new LogApp(flagApp.isChecked(), userApp.getText().toString());
                    mngApp.serializationFlag(MainActivity.this, log);
                    goToViewActivity(usr);
                } else {
                    log = new LogApp();
                    mngApp.serializationFlag(MainActivity.this, log);
                    goToViewActivity(usr);
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void authenticateUser(View view) {
        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Lettura impronta digitale")
                .setSubtitle("Autenticazione richiesta per continuare")
                .setDescription("Account con autenticazione biometrica per proteggere i dati.")
                .setNegativeButton("Annulla", this.getMainExecutor(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notifyUser("Autenticazione annullata");
                                log = new LogApp();
                                mngApp.serializationFlag(MainActivity.this, log);
                                goToMainActivity();
                                passApp.setText("");
                            }
                        })
                .build();
        biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(), getAuthenticationCallback());
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void biometricAuthentication(RelativeLayout lay) {
        if (!checkBiometricSupport()) {
            return;
        }
        authenticateUser(lay);
    }

    public boolean fieldCheck(User usr) {
        if (isInvalidWord(usr.getUser()) && isInvalidWord(usr.getPassword())) {
            userApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
            passApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
            notifyUser("Campi Utente e Password non validi !!!");
            return false;
        } else if (isInvalidWord(usr.getUser())) {
            userApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
            notifyUser("Campo Utente non valido !!!");
            return false;
        } else if (isInvalidWord(usr.getPassword())) {
            passApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
            notifyUser("Campo Password non valido !!!");
            return false;
        }
        return true;
    }

    public boolean isInvalidWord(String word) {
        return ((!word.matches("[A-Za-z0-9?!_.-]*")) || (word.isEmpty()));
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
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
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
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

    private Boolean checkBiometricSupport() {
        KeyguardManager keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        PackageManager packageManager = this.getPackageManager();
        if (keyguardManager != null && !keyguardManager.isKeyguardSecure()) {
            notifyUser("Lock screen security non abilitato nelle impostazioni");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Permesso impronte digitali non abilitato");
            return false;
        }
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        }
        return true;
    }

    private CancellationSignal getCancellationSignal() {
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                notifyUser("Cancelled via signal");
            }
        });
        return cancellationSignal;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showPass(final EditText et, ImageButton showPass) {
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        et.setInputType(InputType.TYPE_NULL);
                        break;
                    case MotionEvent.ACTION_UP:
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }
}


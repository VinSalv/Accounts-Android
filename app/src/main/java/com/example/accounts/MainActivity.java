package com.example.accounts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private RelativeLayout layoutMainActivity;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ArrayList<User> listUser;
    private LogApp log;
    private User usr;
    private Spinner userApp;
    private EditText passApp;
    private Switch flagApp;
    private String u;
    private ArrayList<String> listUsr;
    private ArrayAdapter<String> adapterUser;

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
        layoutMainActivity = findViewById(R.id.mainActivityLay);
        userApp = findViewById(R.id.userApp);
        passApp = findViewById(R.id.passApp);
        flagApp = findViewById(R.id.flagApp);
        Button login = findViewById(R.id.authButton);
        TextView sign = findViewById(R.id.signButton);
        ImageButton showPass = findViewById(R.id.showPass);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        listUsr = new ArrayList<>();
        listUsr.add("Utente");
        for (User us : listUser)
            listUsr.add(us.getUser());
        adapterUser = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listUsr);
        adapterUser.setDropDownViewResource(R.layout.spinner_item);
        userApp.setAdapter(adapterUser);
        userApp.setSelection(0);
        userApp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                u = parent.getItemAtPosition(position).toString();
                if (!(u.toLowerCase().equals("utente")))
                    usr = new User(u, passApp.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        if (log.getFlagApp()) {
            flagApp.setChecked(true);
            usr = mngUsr.findUser(listUser, log.getUser());
            if (usr != null) {
                if (usr.getFinger()) {
                    biometricAuthentication(layoutMainActivity);
                } else {
                    goToCategoryActivity();
                }
            } else {
                notifyUser("Impossibile restare connesso.");
            }
        }
        showPass(passApp, showPass);
        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                passApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                usr = new User(u, passApp.getText().toString());
                if (!fieldCheck(usr)) return;
                if (mngUsr.login(usr, listUser)) {
                    usr = mngUsr.findUser(listUser, u);
                    if (usr.getFinger()) {
                        biometricAuthentication(layoutMainActivity);
                    } else {
                        log = new LogApp(flagApp.isChecked(), u);
                        mngApp.serializationFlag(MainActivity.this, log);
                        goToCategoryActivity();
                    }
                } else {
                    passApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
                    notifyUser("Autenticazione errata.");
                }
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignActivity();
            }
        });

        ImageButton about = findViewById(R.id.aboutButton);
        about.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                goToAboutActivity();
            }
        });

        ImageButton intro = findViewById(R.id.introText);
        intro.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                goToWelcomeActivity();
            }
        });

        TextView forgotPass = findViewById(R.id.forgotText);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewRecovery = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_recovery, (ViewGroup) findViewById(R.id.popupRecovery));
                final PopupWindow popupWindowRecovery = new PopupWindow(popupViewRecovery, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowRecovery.setFocusable(true);
                popupWindowRecovery.setOutsideTouchable(true);
                popupWindowRecovery.setBackgroundDrawable(new BitmapDrawable());
                View parent = layoutMainActivity.getRootView();
                popupWindowRecovery.showAtLocation(parent, Gravity.CENTER, 0, 0);
                final Spinner spinnerUser = popupViewRecovery.findViewById(R.id.userSpinner);
                spinnerUser.setAdapter(adapterUser);
                spinnerUser.setSelection(0);
                Button goOn = popupViewRecovery.findViewById(R.id.goOnButton);
                goOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupViewAnswer = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_answer, (ViewGroup) findViewById(R.id.popupAnswer));
                        final PopupWindow popupWindowAnswer = new PopupWindow(popupViewAnswer, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                        popupWindowAnswer.setFocusable(true);
                        popupWindowAnswer.setOutsideTouchable(true);
                        popupWindowAnswer.setBackgroundDrawable(new BitmapDrawable());
                        View parent = layoutMainActivity.getRootView();
                        popupWindowAnswer.showAtLocation(parent, Gravity.CENTER, 0, 0);
                        if ((spinnerUser.getSelectedItem()).equals("Utente")) {
                            popupWindowAnswer.dismiss();
                            return;
                        } else {
                            popupWindowRecovery.dismiss();
                        }
                        ((TextView) popupViewAnswer.findViewById(R.id.questionText)).setText((mngUsr.findUser(listUser, (String) (spinnerUser.getSelectedItem()))).getQuestion());
                        final EditText answer = popupViewAnswer.findViewById(R.id.answerEditText);
                        Button recovery = popupViewAnswer.findViewById(R.id.recoveryButton);
                        recovery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (answer.getText().toString().toLowerCase().equals((mngUsr.findUser(listUser, ((String) spinnerUser.getSelectedItem()))).getAnswer().toLowerCase())) {
                                    popupWindowAnswer.dismiss();
                                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View popupViewPassRec = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_pass_recovery, (ViewGroup) findViewById(R.id.popupPassRecovery));
                                    final PopupWindow popupWindowPassRec = new PopupWindow(popupViewPassRec, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                    popupWindowPassRec.setFocusable(true);
                                    popupWindowPassRec.setOutsideTouchable(true);
                                    popupWindowPassRec.setBackgroundDrawable(new BitmapDrawable());
                                    View parent = layoutMainActivity.getRootView();
                                    popupWindowPassRec.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                    ((TextView) popupViewPassRec.findViewById(R.id.passShowText)).setText((mngUsr.findUser(listUser, ((String) spinnerUser.getSelectedItem()))).getPassword());
                                    Button done = popupViewPassRec.findViewById(R.id.done);
                                    done.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupWindowPassRec.dismiss();
                                        }
                                    });
                                    showPass((TextView) popupViewPassRec.findViewById(R.id.passShowText), (ImageButton) popupViewPassRec.findViewById((R.id.showPass)));
                                } else {
                                    answer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void goToMainActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    public void goToWelcomeActivity() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.putExtra("string", "go");
        startActivity(intent);
    }

    public void goToCategoryActivity() {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", mngUsr.findUser(listUser, usr.getUser()));
        startActivity(intent);
        finish();
    }

    public void goToAboutActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void goToSignActivity() {
        Intent intent = new Intent(MainActivity.this, SignActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                notifyUser("Autenticazione errore: " + errString + ".");
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
                notifyUserShortWay("Autenticazione effettuata.");
                super.onAuthenticationSucceeded(result);
                if (flagApp.isChecked()) {
                    log = new LogApp(flagApp.isChecked(), usr.getUser());
                    mngApp.serializationFlag(MainActivity.this, log);
                    goToCategoryActivity();
                } else {
                    log = new LogApp();
                    mngApp.serializationFlag(MainActivity.this, log);
                    goToCategoryActivity();
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
                                notifyUserShortWay("Autenticazione annullata.");
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
            notifyUser("Campi Utente e Password non validi. Caratteri validi: A-Z a-z 0-9 @#$%^&+=!?._");
            return false;
        } else if (isInvalidWord(usr.getUser())) {
            userApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
            notifyUser("Campo Utente non valido. Caratteri validi: A-Z a-z 0-9 @#$%^&+=!?._");
            return false;
        } else if (isInvalidWord(usr.getPassword())) {
            passApp.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.errorEditText)));
            notifyUser("Campo Password non valido. Caratteri validi: A-Z a-z 0-9 @#$%^&+=!?._");
            return false;
        }
        return true;
    }

    public boolean isInvalidWord(String word) {
        return ((!word.matches("[A-Za-z0-9@#$%^&+=!?._-]*")) || (word.isEmpty()));
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    private void notifyUserShortWay(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_SHORT).show();
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
                        break;
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
            notifyUser("Lock screen security non abilitato nelle impostazioni.");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Permesso impronte digitali non abilitato.");
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
                notifyUserShortWay("Cancelled via signal");
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

    @SuppressLint("ClickableViewAccessibility")
    public void showPass(final TextView tv, ImageButton showPass) {
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.rightText));
                        break;
                    case MotionEvent.ACTION_UP:
                        tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.transparent));
                        break;
                }
                return true;
            }
        });
    }
}


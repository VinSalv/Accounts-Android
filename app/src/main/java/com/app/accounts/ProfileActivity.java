package com.app.accounts;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class ProfileActivity extends AppCompatActivity {
    private CoordinatorLayout layoutProfileActivity;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ManageCategory mngCat;
    private LogApp log;
    private User usr;
    private Category category;
    private ArrayList<User> listUser;
    private ArrayList<Category> listCategory;
    private TextView profile;
    private TextView profileToolbar;
    private ImageButton showPass;
    private ImageButton showPass2;
    private Spinner questionSpinner;
    private EditText questionEdit;
    private EditText answerEdit;
    private ArrayAdapter<String> adapterQuestion;
    private Switch flagProfApp;
    private int attempts;
    private boolean blockBack;
    private boolean doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layoutProfileActivity = findViewById(R.id.profileActivityLay);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            blockBack = true;
            attempts = 3;
            ArrayList<String> listQuestion = new ArrayList<>();
            listQuestion.add("Altro");
            listQuestion.add("Qual è il tuo colore preferito?");
            listQuestion.add("Qual era il tuo soprannome da bambino?");
            listQuestion.add("Qual è il nome del tuo primo animale domestico?");
            adapterQuestion = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listQuestion);
            mngCat = new ManageCategory();
            if (usr.getFinger()) {
                flagProfApp.setChecked(true);
            }
            flagProfApp = findViewById(R.id.flagProfApp);
            flagProfApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("SwitchIntDef")
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        BiometricManager biometricManager = BiometricManager.from(ProfileActivity.this);
                        switch (biometricManager.canAuthenticate()) {
                            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                                notifyUserShortWay("Il dispositivo non dispone del sensore biometrico.");
                                flagProfApp.setChecked(false);
                                listUser.remove(usr);
                                usr.setFinger(false);
                                listUser.add(usr);
                                mngUsr.serializationListUser(ProfileActivity.this, listUser);
                                return;
                            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                                notifyUserShortWay("Il sensore biometrico non è attualmente disponibile.");
                                flagProfApp.setChecked(false);
                                listUser.remove(usr);
                                usr.setFinger(false);
                                listUser.add(usr);
                                mngUsr.serializationListUser(ProfileActivity.this, listUser);
                                return;
                            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                                notifyUserShortWay("Imposta la tua impronta nelle impostazioni del dispositivo.");
                                flagProfApp.setChecked(false);
                                listUser.remove(usr);
                                usr.setFinger(false);
                                listUser.add(usr);
                                mngUsr.serializationListUser(ProfileActivity.this, listUser);
                                return;
                        }
                        listUser.remove(usr);
                        usr.setFinger(true);
                        listUser.add(usr);
                        mngUsr.serializationListUser(ProfileActivity.this, listUser);
                        notifyUserShortWay("Autenticazione biometrica abilitata.");
                    } else {
                        listUser.remove(usr);
                        usr.setFinger(false);
                        listUser.add(usr);
                        mngUsr.serializationListUser(ProfileActivity.this, listUser);
                        notifyUserShortWay("Autenticazione biometrica disabilitata.");
                    }
                }
            });
            profile = findViewById(R.id.profileText);
            profileToolbar = findViewById(R.id.profileTextToolbar);
            profileToolbar.setVisibility(View.INVISIBLE);
            AppBarLayout appBar = findViewById(R.id.profileBarToolbar);
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    profile.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true;
                        profileToolbar.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        profileToolbar.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Button editUser = findViewById(R.id.editProfUsername);
            editUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewUser = Objects.requireNonNull(layoutInflater).inflate(R.layout.edit_user, (ViewGroup) findViewById(R.id.editUserLayout));
                    final PopupWindow popupWindowUser = new PopupWindow(popupViewUser, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowUser.setOutsideTouchable(true);
                    popupWindowUser.setFocusable(true);
                    popupWindowUser.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutProfileActivity.getRootView();
                    popupWindowUser.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText popupText = popupViewUser.findViewById(R.id.userEditText);
                    Button save = popupViewUser.findViewById(R.id.saveUsrEditButton);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            User usrApp = usr;
                            popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                            if (notFieldCheck(popupText.getText().toString())) {
                                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                return;
                            }
                            if (mngUsr.notFindUser(new User(popupText.getText().toString(), "", false, 0), listUser)) {
                                listCategory = mngCat.deserializationListCategory(ProfileActivity.this, usrApp.getUser());
                                mngCat.removeFileCategory(ProfileActivity.this, usrApp.getUser());
                                listUser.remove(usrApp);
                                usr.setUser(fixName(popupText.getText().toString()));
                                mngCat.serializationListCategory(ProfileActivity.this, listCategory, usr.getUser());
                                listUser.add(usr);
                                mngUsr.serializationListUser(ProfileActivity.this, listUser);
                                mngApp = new ManageApp();
                                log = mngApp.deserializationFlag(ProfileActivity.this);
                                if (log.getFlagApp()) {
                                    log.setUser(usr.getUser());
                                    mngApp.serializationFlag(ProfileActivity.this, log);
                                }
                                notifyUserShortWay("Username cambiato con successo.");
                                popupWindowUser.dismiss();
                            } else {
                                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                notifyUser("Username gia esistente.");
                            }
                        }
                    });
                }
            });
            Button editPass = findViewById(R.id.editProfPassword);
            editPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewPassword = Objects.requireNonNull(layoutInflater).inflate(R.layout.edit_password, (ViewGroup) findViewById(R.id.edit_password_layout));
                    final PopupWindow popupWindowPassword = new PopupWindow(popupViewPassword, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowPassword.setOutsideTouchable(true);
                    popupWindowPassword.setFocusable(true);
                    popupWindowPassword.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutProfileActivity.getRootView();
                    popupWindowPassword.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText password = popupViewPassword.findViewById(R.id.passEditText);
                    final EditText password2 = popupViewPassword.findViewById(R.id.passEditText2);
                    Button save = popupViewPassword.findViewById(R.id.savePassEditButton);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            password.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                            password2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                            if (notFieldCheck(password.getText().toString())) {
                                password.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                return;
                            }
                            if (!password.getText().toString().equals(password2.getText().toString())) {
                                password.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                password2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                notifyUser("Le password non corrispondono.");
                                return;
                            }
                            listUser.remove(usr);
                            usr.setPassword(password.getText().toString());
                            listUser.add(usr);
                            mngUsr.serializationListUser(ProfileActivity.this, listUser);
                            notifyUserShortWay("Password cambiata con successo.");
                            popupWindowPassword.dismiss();
                        }
                    });
                    showPass = popupViewPassword.findViewById(R.id.showPass);
                    showPass(password, showPass);
                    showPass2 = popupViewPassword.findViewById(R.id.showPass2);
                    showPass(password2, showPass2);
                }
            });
            Button editQuestion = findViewById(R.id.editProfQuestion);
            editQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewQuestion = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_question, (ViewGroup) findViewById(R.id.questionPopup));
                    final PopupWindow popupWindowQuestion = new PopupWindow(popupViewQuestion, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowQuestion.setOutsideTouchable(true);
                    popupWindowQuestion.setFocusable(true);
                    popupWindowQuestion.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutProfileActivity.getRootView();
                    popupWindowQuestion.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    questionEdit = popupViewQuestion.findViewById(R.id.questionEdit);
                    answerEdit = popupViewQuestion.findViewById(R.id.answerEdit);
                    questionSpinner = popupViewQuestion.findViewById(R.id.questionSpinner);
                    adapterQuestion.setDropDownViewResource(R.layout.spinner_item_question);
                    questionSpinner.setAdapter(adapterQuestion);
                    questionSpinner.setSelection(0);
                    questionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (parent.getItemAtPosition(position).toString().equals("Altro"))
                                questionEdit.animate()
                                        .alpha(1.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                questionEdit.setVisibility(View.VISIBLE);
                                            }
                                        });
                            else
                                questionEdit.animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                questionEdit.setVisibility(View.GONE);
                                            }
                                        });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    Button set = popupViewQuestion.findViewById(R.id.setButton);
                    set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listUser.remove(usr);
                            if (questionSpinner.getSelectedItemPosition() == 0) {
                                if (questionEdit.getText().toString().isEmpty() && answerEdit.getText().toString().isEmpty()) {
                                    questionEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                                    answerEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                                } else if (questionEdit.getText().toString().isEmpty()) {
                                    questionEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                                } else if (answerEdit.getText().toString().isEmpty()) {
                                    answerEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                                }
                            } else {
                                answerEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                            }
                            if (questionSpinner.getSelectedItemPosition() == 0) {
                                if (questionEdit.getText().toString().isEmpty() && answerEdit.getText().toString().isEmpty()) {
                                    questionEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                    answerEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                    return;
                                } else if (questionEdit.getText().toString().isEmpty()) {
                                    questionEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                    return;
                                } else if (answerEdit.getText().toString().isEmpty()) {
                                    answerEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                    return;
                                }
                                usr.setQuestion(questionEdit.getText().toString());
                                usr.setAnswer(answerEdit.getText().toString());
                            } else {
                                if (answerEdit.getText().toString().isEmpty()) {
                                    answerEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                    return;
                                }
                                usr.setQuestion(questionSpinner.getSelectedItem().toString());
                                usr.setAnswer(answerEdit.getText().toString());
                            }
                            listUser.add(usr);
                            mngUsr.serializationListUser(ProfileActivity.this, listUser);
                            notifyUserShortWay("Domanda di sicurezza cambiata con successo.");
                            popupWindowQuestion.dismiss();
                        }
                    });
                }
            });
        } else {
            notifyUser("Credenziali non rilevate. Impossibile settare i parametri di autenticazione.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToSettingActivity() {
        Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        if (((String) getIntent().getExtras().get("cat")).equals(""))
            intent.putExtra("cat", "");
        else {
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            intent.putExtra("category", category);
            intent.putExtra("cat", category.getCat());
        }
        startActivity(intent);
    }

    public String fixName(String name) {
        if (name.isEmpty()) return name;
        else {
            name = name.toLowerCase();
            String name1 = name.substring(0, 1).toUpperCase();
            String name2 = name.substring(1).toLowerCase();
            return name1.concat(name2);
        }
    }

    public boolean notFieldCheck(String s) {
        if (isInvalidWord(s)) {
            notifyUser("Campo non valido. Caratteri validi: A-Z SelectedStateRadioButton-z 0-9 @#$%^&+=!?._");
            return true;
        }
        return false;
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

    public void onBackPressed() {
        if (blockBack) goToSettingActivity();
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                goToMainActivity();
            }
            this.doubleBackToExitPressedOnce = true;
            notifyUser(Html.fromHtml("Premi nuovamente <b>INDIETRO</b> per tornare alla schermata principale.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRestart() {
        super.onRestart();
        layoutProfileActivity.setVisibility(View.INVISIBLE);
        BiometricManager biometricManager = BiometricManager.from(ProfileActivity.this);
        if (usr.getFinger() && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
            biometricAuthentication(layoutProfileActivity);
        else {
            recheckPass();
        }
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

    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                notifyUser("Autenticazione errore: " + errString + ".");
                super.onAuthenticationError(errorCode, errString);
                recheckPass();
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
                layoutProfileActivity.setVisibility(View.VISIBLE);
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
                                recheckPass();
                            }
                        })
                .build();
        biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(), getAuthenticationCallback());
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void biometricAuthentication(CoordinatorLayout lay) {
        if (!checkBiometricSupport()) {
            return;
        }
        authenticateUser(lay);
    }

    private Boolean checkBiometricSupport() {
        KeyguardManager keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        PackageManager packageManager = this.getPackageManager();
        if (keyguardManager != null && !keyguardManager.isKeyguardSecure()) {
            notifyUser("Lock screen security non abilitato nelle impostazioni.");
            recheckPass();
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Permesso impronte digitali non abilitato.");
            recheckPass();
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

    public void recheckPass() {
        blockBack = false;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupViewCheck = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password_match_parent, (ViewGroup) findViewById(R.id.passSecurityPopupMatchParent));
        final PopupWindow popupWindowCheck = new PopupWindow(popupViewCheck, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindowCheck.setBackgroundDrawable(new BitmapDrawable());
        View parent = layoutProfileActivity.getRootView();
        popupWindowCheck.showAtLocation(parent, Gravity.CENTER, 0, 0);
        final EditText popupText = popupViewCheck.findViewById(R.id.passSecurityEditText);
        Button conf = popupViewCheck.findViewById(R.id.confirmation);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                if (popupText.getText().toString().isEmpty()) {
                    popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                    notifyUser("Il campo password è vuoto");
                } else {
                    if (popupText.getText().toString().equals(usr.getPassword())) {
                        layoutProfileActivity.setVisibility(View.VISIBLE);
                        blockBack = true;
                        attempts = 3;
                        popupWindowCheck.dismiss();
                    } else {
                        popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                        attempts--;
                        if (attempts == 2)
                            notifyUserShortWay("Password errata. Hai altri " + attempts + " tentativi");
                        else if (attempts == 1)
                            notifyUserShortWay("Password errata. Hai un ultimo tenativo");
                        else {
                            notifyUserShortWay("Password errata");
                            goToMainActivity();
                        }
                    }
                }
            }
        });
        showPass = popupViewCheck.findViewById(R.id.showPass);
        showPass(popupText, showPass);
    }
}

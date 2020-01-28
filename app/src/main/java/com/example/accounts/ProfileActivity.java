package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private CoordinatorLayout cl;
    private ManageUser mngUsr;
    private User usr;
    private ManageAccount mngAcc;
    private ArrayList<User> listUser;
    private ManageApp mngApp;
    private LogApp log;
    private TextView profile;
    private TextView profile2;
    private ImageButton showPass;
    private ImageButton showPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        cl = findViewById(R.id.profileActivityLay);
        Button editUser = findViewById(R.id.editProfUsername);
        Button editPass = findViewById(R.id.editProfPassword);
        Switch flagProfApp = findViewById(R.id.flagProfApp);
        profile = findViewById(R.id.profileText);
        profile2 = findViewById(R.id.profileTextToolbar);
        profile2.setVisibility(View.INVISIBLE);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            if (usr.getFinger()) {
                flagProfApp.setChecked(true);
            }
            flagProfApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listUser.remove(usr);
                        usr.setFinger(true);
                        listUser.add(usr);
                        mngUsr.serializationListUser(ProfileActivity.this, listUser);
                        notifyUser("Autenticazione biometrica abilitata");
                    } else {
                        listUser.remove(usr);
                        usr.setFinger(false);
                        listUser.add(usr);
                        mngUsr.serializationListUser(ProfileActivity.this, listUser);
                        notifyUser("Autenticazione biometrica disabilitata");
                    }
                }
            });
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
                        profile2.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        profile2.setVisibility(View.INVISIBLE);
                    }
                }
            });
            editUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.edit_user, (ViewGroup) findViewById(R.id.editUserLayout));
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    //noinspection deprecation
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    View parent = cl.getRootView();
                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText username = popupView.findViewById(R.id.userEditText);
                    Button save = popupView.findViewById(R.id.saveUsrEditButton);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            User usrApp = usr;
                            username.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                            if (notFieldCheck(username.getText().toString())) {
                                username.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                return;
                            }
                            if (mngUsr.notFindUser(new User(username.getText().toString(), "", false, 0), listUser)) {
                                mngAcc = new ManageAccount();
                                ArrayList<Account> list = mngAcc.deserializationListAccount(ProfileActivity.this, usrApp.getUser());
                                mngAcc.removeFileAccount(ProfileActivity.this, usrApp.getUser());
                                listUser.remove(usrApp);
                                usr.setUser(fixName(username.getText().toString()));
                                mngAcc.serializationListAccount(ProfileActivity.this, list, usr.getUser());
                                listUser.add(usr);
                                mngUsr.serializationListUser(ProfileActivity.this, listUser);
                                mngApp = new ManageApp();
                                log = mngApp.deserializationFlag(ProfileActivity.this);
                                if (log.getFlagApp()) {
                                    log.setUser(usr.getUser());
                                    mngApp.serializationFlag(ProfileActivity.this, log);
                                }
                                notifyUser("Username cambiato con successo");
                                popupWindow.dismiss();
                            } else {
                                username.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.errorEditText)));
                                notifyUser("Username gia esistente");
                            }
                        }
                    });
                }
            });
            editPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.edit_password, (ViewGroup) findViewById(R.id.edit_password_layout));
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    //noinspection deprecation
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    View parent = cl.getRootView();
                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText password = popupView.findViewById(R.id.passEditText);
                    final EditText password2 = popupView.findViewById(R.id.passEditText2);
                    Button save = popupView.findViewById(R.id.savePassEditButton);
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
                                notifyUser("Le password non corrispondono");
                                return;
                            }
                            listUser.remove(usr);
                            usr.setPassword(password.getText().toString());
                            listUser.add(usr);
                            mngUsr.serializationListUser(ProfileActivity.this, listUser);
                            notifyUser("Password cambiata con successo");
                            popupWindow.dismiss();
                        }
                    });
                    showPass = popupView.findViewById(R.id.showPass);
                    showPass(password, showPass);
                    showPass2 = popupView.findViewById(R.id.showPass2);
                    showPass(password2, showPass2);
                }
            });
        } else {
            notifyUser("Utente non rilevato. Impossibile settare i parametri di autenticazione.");
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
            notifyUser("Campo non valido !!!");
            return true;
        }
        return false;
    }

    public boolean isInvalidWord(String word) {
        return ((!word.matches("[A-Za-z0-9?!_.-]*")) || (word.isEmpty()));
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
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

    public void onBackPressed() {
        goToSettingActivity();
    }
}

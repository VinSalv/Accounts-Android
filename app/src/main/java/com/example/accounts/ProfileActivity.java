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

@SuppressWarnings("deprecation")
public class ProfileActivity extends AppCompatActivity {
    private CoordinatorLayout layoutProfileActivity;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ManageCategory mngCat;
    private LogApp log;
    private User usr;
    private ArrayList<User> listUser;
    private ArrayList<Category> listCategory;
    private TextView profile;
    private TextView profileToolbar;
    private ImageButton showPass;
    private ImageButton showPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layoutProfileActivity = findViewById(R.id.profileActivityLay);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            Switch flagProfApp = findViewById(R.id.flagProfApp);
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
                        notifyUser("Autenticazione biometrica abilitata.");
                    } else {
                        listUser.remove(usr);
                        usr.setFinger(false);
                        listUser.add(usr);
                        mngUsr.serializationListUser(ProfileActivity.this, listUser);
                        notifyUser("Autenticazione biometrica disabilitata.");
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
                                notifyUser("Username cambiato con successo.");
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
                            notifyUser("Password cambiata con successo.");
                            popupWindowPassword.dismiss();
                        }
                    });
                    showPass = popupViewPassword.findViewById(R.id.showPass);
                    showPass(password, showPass);
                    showPass2 = popupViewPassword.findViewById(R.id.showPass2);
                    showPass(password2, showPass2);
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
            notifyUser("Campo non valido.");
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

package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

public class SignActivity extends AppCompatActivity {
    private TextView signText;
    private TextView signTextToolbar;
    private EditText userEdit;
    private EditText passEdit;
    private EditText passEdit2;
    private Switch flagFinger;
    private ManageUser mngUsr;
    private ArrayList<User> listUser = new ArrayList<>();
    private ManageCategory mngCat;
    private ArrayList<Category> listCategory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        final Toolbar toolbar = findViewById(R.id.toolbarSign);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        userEdit = findViewById(R.id.usernameEdit);
        passEdit = findViewById(R.id.passwordEdit);
        passEdit2 = findViewById(R.id.passwordEdit2);
        flagFinger = findViewById(R.id.flagFinger);
        Button sign = findViewById(R.id.signButton);
        ImageButton showPass = findViewById(R.id.showPass);
        ImageButton showPass2 = findViewById(R.id.showPass2);
        mngUsr = new ManageUser();
        mngCat = new ManageCategory();
        listUser = mngUsr.deserializationListUser(this);
        signText = findViewById(R.id.signText);
        signTextToolbar = findViewById(R.id.signTextToolbar);
        signTextToolbar.setVisibility(View.INVISIBLE);
        AppBarLayout appBar = findViewById(R.id.app_bar_sign);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                signText.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    signTextToolbar.setVisibility(View.VISIBLE);
                } else if (isShow) {
                    isShow = false;
                    signTextToolbar.setVisibility(View.INVISIBLE);
                }
            }
        });
        showPass(passEdit, showPass);
        showPass(passEdit2, showPass2);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.colorAccent)));
                passEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.colorAccent)));
                passEdit2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.colorAccent)));
                User usr = new User(fixName(userEdit.getText().toString()), passEdit.getText().toString(), flagFinger.isChecked(), 1);
                if (!fieldCheck(usr)) return;
                if (!passEdit.getText().toString().equals(passEdit2.getText().toString())) {
                    passEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.errorEditText)));
                    passEdit2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.errorEditText)));
                    notifyUser("Le password non corrispondono");
                    return;
                }
                if (mngUsr.notFindUser(usr, listUser)) {
                    listUser.add(usr);
                    mngUsr.serializationListUser(SignActivity.this, listUser);
                    listCategory.add(new Category("Siti", 1));
                    listCategory.add(new Category("Social", 1));
                    listCategory.add(new Category("Giochi", 1));
                    mngCat.serializationListCategory(SignActivity.this, listCategory, usr.getUser());
                    goToCategoryActivity(usr);
                } else {
                    userEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.errorEditText)));
                    notifyUser("User già esistente");
                }
            }
        });

    }

    public void goToCategoryActivity(User usr) {
        Intent intent = new Intent(SignActivity.this, CategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
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

    public boolean fieldCheck(User usr) {
        if (isInvalidWord(usr.getUser()) && isInvalidWord(usr.getPassword())) {
            userEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.errorEditText)));
            passEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.errorEditText)));
            notifyUser("Campi Utente e Password non validi !!!");
            return false;
        } else if (isInvalidWord(usr.getUser())) {
            userEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.errorEditText)));
            notifyUser("Campo Utente non valido !!!");
            return false;
        } else if (isInvalidWord(usr.getPassword())) {
            passEdit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SignActivity.this, R.color.errorEditText)));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
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

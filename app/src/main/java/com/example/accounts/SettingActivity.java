package com.example.accounts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    AppBarLayout appBar;
    ManageUser mngUsr;
    ArrayList<User> listUser = new ArrayList<>();
    private TextView setting;
    private TextView setting2;
    private User owner;
    private ManageApp mngApp;
    private LogApp log;
    private Button settingsButton;
    private Button searchButton;
    private ArrayList<Account> listAccount;
    private ManageAccount mngAcc;
    private User us;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final Toolbar toolbar = findViewById(R.id.toolbarSetting);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        owner = (User) (getIntent().getExtras()).get("owner");

        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);

        mngAcc = new ManageAccount();
        listAccount = mngAcc.deserializationListAccount(this, owner.getUser());

        us = new User();
        for (User u : listUser)
            if (u.getUser().toLowerCase().equals(owner.getUser().toLowerCase()))
                us = u;

        listAccount = mngAcc.deserializationListAccount(this, owner.getUser());

        setting = findViewById(R.id.wellcome);
        setting.setText("Impostazioni");

        setting2 = findViewById(R.id.wellcomeToolbar);
        setting2.setText("Impostazioni");
        setting2.setVisibility(View.INVISIBLE);


        appBar = findViewById(R.id.app_bar_setting);

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                setting.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    setting2.setVisibility(View.VISIBLE);
                } else if (isShow) {
                    isShow = false;
                    setting2.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

}
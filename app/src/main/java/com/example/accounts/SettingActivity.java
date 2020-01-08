package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    AppBarLayout appBar;
    ManageUser mngUsr;
    ArrayList<User> listUser = new ArrayList<>();
    private TextView setting;
    private TextView setting2;
    private ManageApp mngApp;
    private LogApp log;
    private ArrayList<Account> listAccount;
    private ManageAccount mngAcc;
    private User usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final Toolbar toolbar = findViewById(R.id.toolbarSetting);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");

        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);

        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngAcc = new ManageAccount();
            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());

            Button prof = findViewById(R.id.profile);
            Button pdf = findViewById(R.id.pdf);
            Button esportAcc = findViewById(R.id.esportAccount);
            Button importAcc = findViewById(R.id.importAccount);
            Button delProf = findViewById(R.id.deleteProfile);

            setting = findViewById(R.id.setting);
            setting.setText(getResources().getString(R.string.impostazioni));

            setting2 = findViewById(R.id.settingToolbar);
            setting2.setText(getResources().getString(R.string.impostazioni));
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

            prof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToProfileActivity(usr);
                }
            });

            pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            esportAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            importAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            delProf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    log = new LogApp();
                    mngApp.serializationFlag(SettingActivity.this, log);
                    listUser.remove(usr);
                    mngUsr.serializationListUser(SettingActivity.this, listUser);
                    mngAcc.removeFileAccount(SettingActivity.this, usr.getUser());
                    goToMainActivity();
                }
            });

        } else {
            notifyUser("Utente non rilevato. Impossibile aprire le impostazioni.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToProfileActivity(User usr) {
        Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }
}
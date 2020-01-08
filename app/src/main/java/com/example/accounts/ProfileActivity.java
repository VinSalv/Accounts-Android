package com.example.accounts;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private User owner;
    private TextView profile;
    private TextView profile2;
    private CoordinatorLayout cl;
    private ManageUser mngUsr;
    private ArrayList<User> listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");

        cl = findViewById(R.id.coordinatorProfile);
        Button editUser = findViewById(R.id.editProfUsername);
        Button editPass = findViewById(R.id.editProfPassword);

        Switch flagProfApp = findViewById(R.id.flagProfApp);
        profile = findViewById(R.id.profile);
        profile2 = findViewById(R.id.profileToolbar);
        profile2.setVisibility(View.INVISIBLE);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);

        if (owner.getFinger()) {
            flagProfApp.setChecked(true);
        }

        flagProfApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listUser.remove(owner);
                    owner.setFinger(true);
                    listUser.add(owner);
                    mngUsr.serializationListUser(ProfileActivity.this, listUser);
                } else {
                    listUser.remove(owner);
                    owner.setFinger(false);
                    listUser.add(owner);
                    mngUsr.serializationListUser(ProfileActivity.this, listUser);
                }
            }
        });

        AppBarLayout appBar = findViewById(R.id.app_bar_profile);
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
                @SuppressLint("InflateParams") final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.edit_user, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //noinspection deprecation
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                View parent = cl.getRootView();
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

            }
        });

        editPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.edit_password, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //noinspection deprecation
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                View parent = cl.getRootView();
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
            }
        });
    }

}

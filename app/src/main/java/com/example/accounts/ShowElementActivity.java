package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class ShowElementActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private Account account;
    private String owner;
    ManageApp mngApp;
    LogApp log;
    ManageUser mngUsr;
    ArrayList<User> listUser;
    ManageAccount mngAcc;
    ArrayList<Account> listAccount;
    TabLayout tabs;
    TabAdapter tabAdapter;
    private ViewPager viewPager;
    private TextView name;
    private TextView name2;
    private Button optionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_element);

        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);
        account = (Account) Objects.requireNonNull(getIntent().getExtras()).get("account");
        owner = getIntent().getExtras().getString("owner");

        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);

        mngAcc = new ManageAccount();
        listAccount = mngAcc.deserializationListAccount(this, owner);

        name = findViewById(R.id.name);
        name.setText(account.getName());

        name2=findViewById(R.id.nameToolbar);
        name2.setText(account.getName());
        name2.setVisibility(View.INVISIBLE);

        optionButton = findViewById(R.id.optionsButton);

        AppBarLayout appBar = findViewById(R.id.app_bar_show);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                name.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                       name2.setVisibility(View.VISIBLE);
                } else if (isShow) {
                    isShow = false;
                       name2.setVisibility(View.INVISIBLE);
                }
            }
        });

        int k = 1;
        for (AccountElement ae : account.getList()) {
            tabs.addTab(tabs.newTab().setText(account.getName() + "(" + k + ")"));
            k++;
        }
        if (tabs.getTabCount() >= 2) {
            tabs.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        tabAdapter = new TabAdapter(getSupportFragmentManager(), tabs.getTabCount(), (ArrayList<AccountElement>) account.getList());
        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ShowElementActivity.this, v, Gravity.END, 0, R.style.rounded_menu_style_toolbar);
                popup.setOnMenuItemClickListener(ShowElementActivity.this);
                popup.inflate(R.menu.option_popup);
                popup.show();
            }
        });

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.edit:
                intent = new Intent(ShowElementActivity.this, EditActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("owner", owner);
                startActivity(intent);
                return true;
            case R.id.delete:
                listAccount.remove(account);
                mngAcc.serializationListAccount(ShowElementActivity.this, listAccount, owner);
                intent = new Intent(ShowElementActivity.this, ViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("owner", owner);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

}
package com.example.accounts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ShowElementActivity extends AppCompatActivity {
    private Account account;
    private String owner;
    private ManageApp mngApp;
    private LogApp log;
    private ManageUser mngUsr;
    private ArrayList<User> listUser;
    private ManageAccount mngAcc;
    private ArrayList<Account> listAccount;
    private ArrayList<AccountElement> listElem;
    private TabLayout tabs;
    private TabAdapter tabAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_element);

        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);
        account = (Account) getIntent().getExtras().get("account");
        owner = getIntent().getExtras().getString("owner");

        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);

        mngAcc = new ManageAccount();
        listAccount = mngAcc.deserializationListAccount(this, owner);

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

    }
}
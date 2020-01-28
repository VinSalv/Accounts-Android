package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class ShowElementActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private CoordinatorLayout cl;
    private Account account;
    private User owner;
    private ManageAccount mngAcc;
    private ArrayList<Account> listAccount;
    private Account acc;
    private ViewPager viewPager;
    private TextView name;
    private TextView name2;
    private User usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_element);
        cl = findViewById(R.id.coordinatorLayShow);
        viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        account = (Account) Objects.requireNonNull(getIntent().getExtras()).get("account");
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUsr = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUsr, owner.getUser());
        if (usr != null) {
            mngAcc = new ManageAccount();
            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());
            acc = mngAcc.findAccount(listAccount, account.getName());
            if (acc != null) {
                name = findViewById(R.id.name);
                name.setText(account.getName());
                name2 = findViewById(R.id.nameToolbar);
                name2.setText(account.getName());
                name2.setVisibility(View.INVISIBLE);
                Button optionButton = findViewById(R.id.optionsButton);
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
                for (AccountElement ignored : acc.getList()) {
                    tabs.addTab(tabs.newTab().setText(acc.getName() + "(" + k + ")"));
                    k++;
                }
                if (tabs.getTabCount() >= 2) {
                    tabs.setTabMode(TabLayout.MODE_FIXED);
                } else {
                    tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
                TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), tabs.getTabCount(), (ArrayList<AccountElement>) acc.getList());
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
                        popupMenu(R.style.rounded_menu_style_toolbar, R.menu.option_popup, v);
                    }
                });
            } else {
                notifyUser("Account non rilevato. Impossibile mostrare i dati.");
                goToViewActivity(usr);
            }
        } else {
            notifyUser("Utente non rilevato. Impossibile mostrare l'account.");
            goToMainActivity();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                goToEditActivity(usr, acc);
                return true;
            case R.id.delete:
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //noinspection deprecation
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                View parent = cl.getRootView();
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                TextView et = popupView.findViewById(R.id.securityText);
                et.setText("Sei sicuro di voler eliminare " + account.getName() + " dai tuoi account?");
                Button yes = popupView.findViewById(R.id.yes);
                Button no = popupView.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listAccount.remove(account);
                        mngAcc.serializationListAccount(ShowElementActivity.this, listAccount, owner.getUser());
                        notifyUser(account.getName() + " è stato rimosso con successo!");
                        goToViewActivity(usr);
                        popupWindow.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyUser(account.getName() + " non è stato rimosso.");
                        popupWindow.dismiss();
                    }
                });
                return true;
            default:
                return false;
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(ShowElementActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity(User usr) {
        Intent intent = new Intent(ShowElementActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
    }

    public void goToEditActivity(User usr, Account acc) {
        Intent intent = new Intent(ShowElementActivity.this, EditActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("account", acc);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public void popupMenu(int style, int menu, View v) {
        PopupMenu popup = new PopupMenu(ShowElementActivity.this, v, Gravity.END, 0, style);
        popup.setOnMenuItemClickListener(ShowElementActivity.this);
        popup.inflate(menu);
        popup.show();
    }
}
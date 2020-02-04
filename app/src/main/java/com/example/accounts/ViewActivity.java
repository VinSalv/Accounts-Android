package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ActionMode.Callback {
    boolean doubleBackToExitPressedOnce;
    private ManageUser mngUsr;
    private CoordinatorLayout cl;
    private ArrayList<User> listUser;
    private ManageApp mngApp;
    private LogApp log;
    private Button settingsButton;
    private Button searchButton;
    private ArrayList<Account> listAccount;
    private ManageAccount mngAcc;
    private User usr;
    private TextView wellcome;
    private TextView wellcomeMini;
    private TextView wellcome2;
    private FloatingActionButton setting;
    private FloatingActionButton search;
    private FloatingActionButton add;
    private RadioButton rb1;
    private List<String> selectedIds;
    private MyAdapter adapter;
    private ActionMode actionMode;
    private boolean isMultiSelect;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        final Toolbar toolbar = findViewById(R.id.viewToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        cl = findViewById(R.id.viewActivityLay);
        listUser = new ArrayList<>();
        isMultiSelect = false;
        doubleBackToExitPressedOnce = false;
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngAcc = new ManageAccount();
            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());
            if (usr.getSort() == 1)
                mngAcc.serializationListAccount(this, increasing(listAccount), usr.getUser());
            else
                mngAcc.serializationListAccount(this, decreasing(listAccount), usr.getUser());
            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());
            wellcome = findViewById(R.id.wellcomeText);
            wellcome.setText("Benvenuto " + usr.getUser());
            wellcomeMini = findViewById(R.id.wellcomeMiniText);
            wellcomeMini.setText("Numero account: " + listAccount.size());
            wellcome2 = findViewById(R.id.wellcomeTextToolbar);
            wellcome2.setText("Lista di " + usr.getUser());
            wellcome2.setVisibility(View.INVISIBLE);
            settingsButton = findViewById(R.id.settingsButton);
            settingsButton.setVisibility(View.INVISIBLE);
            searchButton = findViewById(R.id.searchButton);
            searchButton.setVisibility(View.INVISIBLE);
            AppBarLayout appBar = findViewById(R.id.viewBarToolbar);
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    wellcome.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    wellcomeMini.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true;
                        wellcome2.setVisibility(View.VISIBLE);
                        settingsButton.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        wellcome2.setVisibility(View.INVISIBLE);
                        settingsButton.setVisibility(View.INVISIBLE);
                        searchButton.setVisibility(View.INVISIBLE);
                    }
                }
            });
            setting = findViewById(R.id.settingsFloatingButton);
            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu(R.style.rounded_menu_style, R.menu.popup, v);
                }
            });
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu(R.style.rounded_menu_style_toolbar, R.menu.popup, v);
                }
            });
            search = findViewById(R.id.searchFloatingButton);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSearchActivity(usr);
                }
            });
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSearchActivity(usr);
                }
            });
            add = findViewById(R.id.addFloatingButton);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToAddActivity(usr);
                }
            });
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            adapter = new MyAdapter(this, listAccount);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isMultiSelect) {
                        multiSelect(position);
                    } else {
                        goToShowElementActivity(usr, adapter.getItem(position));
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    if (!isMultiSelect) {
                        selectedIds = new ArrayList<>();
                        isMultiSelect = true;
                        if (actionMode == null) {
                            actionMode = startActionMode(ViewActivity.this);
                        }
                    }
                    multiSelect(position);
                }
            }));
        } else {
            notifyUser("Utente non rilevato. Impossibile visualizzare la lista degli account.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToAddActivity(User usr) {
        Intent intent = new Intent(ViewActivity.this, AddActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);

    }

    public void goToShowElementActivity(User usr, Account acc) {
        Intent intent = new Intent(ViewActivity.this, ShowElementActivity.class);
        intent.putExtra("account", acc);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    public void goToSettingActivity(User usr) {
        Intent intent = new Intent(ViewActivity.this, SettingActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    public void goToSearchActivity(User usr) {
        Intent intent = new Intent(ViewActivity.this, SearchActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    public void refresh() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, final Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.my_context_menu, menu);
        return true;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        actionMode = startActionMode(ViewActivity.this);
                    }
                }
                multiSelect(-1);
                return true;
            case R.id.sort:
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //noinspection deprecation
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                View parent = cl.getRootView();
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                RadioGroup rg = popupView.findViewById(R.id.radioGroupSorter);
                rb1 = popupView.findViewById(R.id.increasing);
                RadioButton rb2 = popupView.findViewById(R.id.decreasing);
                if (usr.getSort() == 1)
                    rg.check(rb1.getId());
                else
                    rg.check(rb2.getId());
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == rb1.getId()) {
                            listUser.remove(usr);
                            usr.setSort(1);
                            listUser.add(usr);
                            mngUsr.serializationListUser(ViewActivity.this, listUser);
                            mngAcc.serializationListAccount(ViewActivity.this, increasing(listAccount), usr.getUser());
                        } else {
                            listUser.remove(usr);
                            usr.setSort(2);
                            listUser.add(usr);
                            mngUsr.serializationListUser(ViewActivity.this, listUser);
                            mngAcc.serializationListAccount(ViewActivity.this, decreasing(listAccount), usr.getUser());
                        }
                        popupWindow.dismiss();
                        refresh();
                    }
                });
                return true;
            case R.id.setting:
                goToSettingActivity(usr);
                return true;
            case R.id.exit:
                log = new LogApp();
                mngApp.serializationFlag(this, log);
                goToMainActivity();
                return true;
            default:
                return false;
        }
    }

    public void popupMenu(int style, int menu, View v) {
        PopupMenu popup = new PopupMenu(ViewActivity.this, v, Gravity.END, 0, style);
        popup.setOnMenuItemClickListener(ViewActivity.this);
        popup.inflate(menu);
        popup.show();
    }

    @SuppressLint("RestrictedApi")
    private void multiSelect(int position) {
        settingsButton.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        setting.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        Account data = adapter.getItem(position);
        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(data.getName()))
                    selectedIds.remove(data.getName());
                else
                    selectedIds.add(data.getName());
                adapter.setSelectedIds(selectedIds);
            }
        }
        if (selectedIds.size() == listAccount.size()) {
            Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Deseleziona tutto");
        } else Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Seleziona tutto");

        Objects.requireNonNull(actionMode).setTitle("Elem: " + selectedIds.size());
    }

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_id) {
            if (!selectedIds.isEmpty()) {
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
                if (selectedIds.size() == 1)
                    et.setText("Sei sicuro di voler eliminare " + selectedIds.get(0) + " dalla lista dei tuoi account?");
                else
                    et.setText("Sei sicuro di voler eliminare questi " + selectedIds.size() + " account selezionati?");
                Button yes = popupView.findViewById(R.id.yes);
                Button no = popupView.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String data : selectedIds) {
                            Account a = mngAcc.findAccount(listAccount, data);
                            if (a != null)
                                listAccount.remove(a);
                            else
                                notifyUser("Account " + data + " non rimosso. Non è stato rilevato nella lista!");
                        }
                        if (selectedIds.size() == 1)
                            notifyUser(selectedIds.get(0) + " è stato rimosso con successo!");
                        else
                            notifyUser(selectedIds.size() + " account sono stati rimossi con successo!");
                        popupWindow.dismiss();
                        mngAcc.serializationListAccount(ViewActivity.this, listAccount, usr.getUser());
                        startActivity(getIntent());
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedIds.size() == 1)
                            notifyUser(selectedIds.get(0) + " non è stato rimosso.");
                        else
                            notifyUser("Nessun account è stato rimosso.");
                        popupWindow.dismiss();
                    }
                });
            } else notifyUser("Nessun account è stato selezionato per la rimozione.");
            settingsButton.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            setting.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            return true;
        }
        if (menuItem.getItemId() == R.id.select_all) {
            String s = "Seleziona tutto";
            if (menuItem.getTitle().toString().toLowerCase().equals(s.toLowerCase())) {
                menuItem.setTitle("Deseleziona tutto");
                selectedIds.clear();
                for (Account a : listAccount) {
                    selectedIds.add(a.getName());
                }
                adapter.setSelectedIds(selectedIds);
            } else {
                menuItem.setTitle("Seleziona tutto");
                selectedIds.clear();
                adapter.setSelectedIds(selectedIds);
            }
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        isMultiSelect = false;
        selectedIds = new ArrayList<>();
        adapter.setSelectedIds(new ArrayList<String>());
        settingsButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        setting.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public ArrayList<Account> increasing(ArrayList<Account> list) {
        Collections.sort(list, new Comparator<Account>() {
            @Override
            public int compare(Account lhs, Account rhs) {
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        });
        return list;
    }

    public ArrayList<Account> decreasing(ArrayList<Account> list) {
        Collections.sort(list, new Comparator<Account>() {
            @Override
            public int compare(Account lhs, Account rhs) {
                return rhs.getName().toLowerCase().compareTo(lhs.getName().toLowerCase());
            }
        });
        return list;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Clicca di nuovo BACK per uscire", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
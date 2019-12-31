package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ActionMode.Callback {
    boolean doubleBackToExitPressedOnce = false;
    private TextView wellcome;
    private TextView wellcome2;
    AppBarLayout appBar;
    private String owner;
    ManageUser mngUsr;
    ArrayList<User> listUser = new ArrayList<>();
    private ManageApp mngApp;
    private LogApp log;
    private Button settingsButton;
    private Button searchButton;
    private ArrayList<Account> listAccount;
    private ManageAccount mngAcc;
    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    private MyAdapter adapter;
    private List<String> selectedIds = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        owner = Objects.requireNonNull(getIntent().getExtras()).getString("owner");

        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);

        mngAcc = new ManageAccount();
        listAccount = mngAcc.deserializationListAccount(this, owner);

        wellcome = findViewById(R.id.wellcome);
        wellcome.setText("Benvenuto " + owner);

        wellcome2 = findViewById(R.id.wellcomeToolbar);
        wellcome2.setText("Lista di " + owner);
        wellcome2.setVisibility(View.INVISIBLE);

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setVisibility(View.INVISIBLE);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setVisibility(View.INVISIBLE);

        appBar = findViewById(R.id.app_bar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                wellcome.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
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

        final FloatingActionButton setting = findViewById(R.id.settingsFloatingButton);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ViewActivity.this, v, Gravity.END, 0, R.style.rounded_menu_style);
                popup.setOnMenuItemClickListener(ViewActivity.this);
                popup.inflate(R.menu.popup);
                popup.show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ViewActivity.this, v, Gravity.END, 0, R.style.rounded_menu_style_toolbar);
                popup.setOnMenuItemClickListener(ViewActivity.this);
                popup.inflate(R.menu.popup);
                popup.show();
            }
        });

        final FloatingActionButton search = findViewById(R.id.searchFloatingButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewActivity.this, "Pulsante: search", Toast.LENGTH_SHORT).show();

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewActivity.this, "Pulsante: search", Toast.LENGTH_SHORT).show();

            }
        });

        final FloatingActionButton add = findViewById(R.id.addFloatingButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, AddActivity.class);
                intent.putExtra("owner", owner);
                startActivity(intent);
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
                    Intent intent = new Intent(ViewActivity.this, ShowElementActivity.class);
                    intent.putExtra("account", adapter.getItem(position));
                    intent.putExtra("owner", owner);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        actionMode = startActionMode(ViewActivity.this); //show ActionMode.
                    }
                }
                multiSelect(position);
            }
        }));
    }

    private void multiSelect(int position) {
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
        selectedIds.size();
        Objects.requireNonNull(actionMode).setTitle(String.valueOf(selectedIds.size())); //show selected item count on action mode.
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.my_context_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_id) {
            for (String data : selectedIds) {
                Account a = new Account();
                for (Account a2 : listAccount) {
                    if (a2.getName().equals(data)) {
                        a = a2;
                        break;
                    }
                }
                listAccount.remove(a);
            }
            if (!selectedIds.isEmpty()) {
                if (selectedIds.size() == 1) {
                    Toast.makeText(this, "Un elemento è stato rimosso", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, selectedIds.size() + " elementi sono stati rimossi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, " Nessun elemento è stato rimosso", Toast.LENGTH_SHORT).show();
            }
            mngAcc.serializationListAccount(this, listAccount, owner);
            finish();
            startActivity(getIntent());
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        isMultiSelect = false;
        selectedIds = new ArrayList<>();
        adapter.setSelectedIds(new ArrayList<String>());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        actionMode = startActionMode(ViewActivity.this); //show ActionMode.
                    }
                }
                multiSelect(-1);
                return true;
            case R.id.sort:
                return true;
            case R.id.setting:
                return true;
            case R.id.exit:
                log = new LogApp();
                mngApp.serializationFlag(this, log);
                Intent intent = new Intent(ViewActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("owner", "");
                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
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
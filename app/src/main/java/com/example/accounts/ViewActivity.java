package com.example.accounts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    boolean doubleBackToExitPressedOnce = false;
    private TextView wellcome;
    private AppBarLayout appBar;
    private String path;
    private String owner;
    private ManageUser mngUsr;
    private ArrayList<User> listUser = new ArrayList<>();
    private ManageApp mngApp;
    private LogApp log;
    private Button settingsButton;
    private Button searchButton;
    private ArrayList<Account> listAccount;
    private ManageAccount mngAcc;
    private ListView list;
    private ListViewAdapter listviewadapter;
    private List<Account> accounts = new ArrayList<Account>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        path = getIntent().getExtras().getString("path");
        owner = getIntent().getExtras().getString("owner");

        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(path);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(path);

        mngAcc = new ManageAccount();
        listAccount = mngAcc.deserializationListAccount(path, owner);

        wellcome = findViewById(R.id.wellcome);
        wellcome.setText("Benvenuto " + owner);

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setVisibility(View.INVISIBLE);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setVisibility(View.INVISIBLE);

        appBar = (AppBarLayout) findViewById(R.id.app_bar);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                wellcome.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                collapsingToolbarLayout.setTitle("Lista Account di " + owner);
                collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transperent));
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.rgb(255, 255, 255));
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    settingsButton.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.VISIBLE);
                } else if (isShow) {
                    isShow = false;
                    settingsButton.setVisibility(View.INVISIBLE);
                    searchButton.setVisibility(View.INVISIBLE);


                }
            }
        });

        final FloatingActionButton setting = (FloatingActionButton) findViewById(R.id.settingsFloatingButton);
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

        final FloatingActionButton search = (FloatingActionButton) findViewById(R.id.searchFloatingButton);
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

        final FloatingActionButton add = (FloatingActionButton) findViewById(R.id.addFloatingButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, AddActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("owner", log.getUser());
                startActivity(intent);
            }
        });



        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.accountListView);

        // Pass results to ListViewAdapter Class
        listviewadapter = new ListViewAdapter(this, R.layout.list_account,
                accounts);

        // Binds the Adapter to the ListView
        list.setAdapter(listviewadapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        // Capture ListView item click
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = list.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                listviewadapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = listviewadapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Account selecteditem = listviewadapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                listviewadapter.remove(selecteditem);
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.my_context_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                listviewadapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                // do your code
                return true;
            case R.id.sort:
                // do your code
                return true;
            case R.id.setting:
                // do your code
                return true;
            case R.id.exit:
                log = new LogApp();
                mngApp.serializationFlag(log, path);
                Intent intent = new Intent(ViewActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("path", path);
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
        Toast.makeText(this, "Per favore clicca di nuovo BACK per uscire", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
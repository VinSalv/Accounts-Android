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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class CategoryActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ActionMode.Callback {
    boolean doubleBackToExitPressedOnce;
    private ManageUser mngUsr;
    private CoordinatorLayout cl;
    private ArrayList<User> listUser;
    private ManageApp mngApp;
    private LogApp log;
    private Button settingsButton;
    private Button searchButton;
    private ArrayList<Category> listCategory;
    private ManageCategory mngCat;
    private User usr;
    private TextView wellcome;
    private TextView wellcomeMini;
    private TextView wellcome2;
    private RecyclerCategoryAdapter mAdapter;
    private boolean isMultiSelect;
    private ArrayList<String> selectedIds;
    private ActionMode actionMode;
    private FloatingActionButton setting;
    private FloatingActionButton search;
    private FloatingActionButton add;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        final Toolbar toolbar = findViewById(R.id.catToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        cl = findViewById(R.id.catActivityLay);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        isMultiSelect = false;
        listUser = new ArrayList<>();
        listCategory = new ArrayList<>();
        doubleBackToExitPressedOnce = false;
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            if (usr.getSort() == 1)
                mngCat.serializationListCategory(this, increasing(listCategory), usr.getUser());
            else if (usr.getSort() == 2)
                mngCat.serializationListCategory(this, decreasing(listCategory), usr.getUser());
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            wellcome = findViewById(R.id.wellcomeText);
            wellcome.setText("Benvenuto " + usr.getUser());
            wellcomeMini = findViewById(R.id.wellcomeMiniText);
            wellcomeMini.setText("Numero categorie: " + listCategory.size());
            wellcome2 = findViewById(R.id.wellcomeTextToolbar);
            wellcome2.setText("Lista categorie di " + usr.getUser());
            wellcome2.setVisibility(View.INVISIBLE);
            settingsButton = findViewById(R.id.settingsButton);
            settingsButton.setVisibility(View.INVISIBLE);
            searchButton = findViewById(R.id.searchButton);
            searchButton.setVisibility(View.INVISIBLE);
            AppBarLayout appBar = findViewById(R.id.catBarToolbar);
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

            mAdapter = new RecyclerCategoryAdapter(this, listCategory, usr);
            ItemTouchHelper.Callback callback = new ItemMoveCategoryCallback(mAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
            GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isMultiSelect) {
                        multiSelect(position);
                    } else {
                        // goToShowElementActivity(usr, mAdapter.getItem(position));
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            }));
        } else {
            notifyUser("Utente non rilevato. Impossibile visualizzare la lista degli account.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToAddActivity(User usr) {
        Intent intent = new Intent(CategoryActivity.this, AddActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);

    }

    public void goToShowElementActivity(User usr, Account acc) {
        Intent intent = new Intent(CategoryActivity.this, ShowElementActivity.class);
        intent.putExtra("account", acc);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    public void goToSettingActivity(User usr) {
        Intent intent = new Intent(CategoryActivity.this, SettingActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    public void goToSearchActivity(User usr) {
        Intent intent = new Intent(CategoryActivity.this, SearchActivity.class);
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

    @SuppressLint("RestrictedApi")
    private void multiSelect(int position) {
        settingsButton.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        setting.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        Category data = mAdapter.getItem(position);
        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(data.getCat()))
                    selectedIds.remove(data.getCat());
                else
                    selectedIds.add(data.getCat());
                mAdapter.setSelectedIds(selectedIds);
            }
        }
        if (selectedIds.size() == listCategory.size()) {
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
                    et.setText("Sei sicuro di voler eliminare " + selectedIds.get(0) + " dalla lista delle tue categorie? (Verrano eliminati anche i relativi account)");
                else
                    et.setText("Sei sicuro di voler eliminare queste " + selectedIds.size() + " categorie selezionate? (Verrano eliminati anche i relativi account)");
                Button yes = popupView.findViewById(R.id.yes);
                Button no = popupView.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String data : selectedIds) {
                            Category cat = mngCat.findCategory(listCategory, data);
                            if (cat != null)
                                listCategory.remove(cat);
                            else
                                notifyUser("Categoria " + data + " non rimossa. Non è stato rilevato nella lista delle tue categorie!");
                        }
                        if (selectedIds.size() == 1)
                            notifyUser(selectedIds.get(0) + " è stato rimossa con successo!");
                        else
                            notifyUser(selectedIds.size() + " categorie sono state rimosse con successo!");
                        popupWindow.dismiss();
                        mngCat.serializationListCategory(CategoryActivity.this, listCategory, usr.getUser());
                        startActivity(getIntent());
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedIds.size() == 1)
                            notifyUser(selectedIds.get(0) + " non è stato rimossa.");
                        else
                            notifyUser("Nessuna categoria è stata rimossa.");
                        popupWindow.dismiss();
                    }
                });
            } else notifyUser("Nessuna categoria è stata selezionata per la rimozione.");
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
                for (Category cat : listCategory) {
                    selectedIds.add(cat.getCat());
                }
                mAdapter.setSelectedIds(selectedIds);
                Objects.requireNonNull(actionMode).setTitle("Elem: " + selectedIds.size());
            } else {
                menuItem.setTitle("Seleziona tutto");
                selectedIds.clear();
                mAdapter.setSelectedIds(selectedIds);
                Objects.requireNonNull(actionMode).setTitle("Elem: " + selectedIds.size());
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
        mAdapter.setSelectedIds(new ArrayList<String>());
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


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        actionMode = startActionMode(CategoryActivity.this);
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
                final RadioButton rb1 = popupView.findViewById(R.id.increasing);
                final RadioButton rb2 = popupView.findViewById(R.id.decreasing);
                final RadioButton rb3 = popupView.findViewById(R.id.customized);
                if (usr.getSort() == 1)
                    rg.check(rb1.getId());
                else if (usr.getSort() == 2)
                    rg.check(rb2.getId());
                else
                    rg.check(rb3.getId());

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == rb1.getId()) {
                            listUser.remove(usr);
                            usr.setSort(1);
                            listUser.add(usr);
                            mngUsr.serializationListUser(CategoryActivity.this, listUser);
                            mngCat.serializationListCategory(CategoryActivity.this, increasing(listCategory), usr.getUser());
                        } else if (checkedId == rb2.getId()) {
                            listUser.remove(usr);
                            usr.setSort(2);
                            listUser.add(usr);
                            mngUsr.serializationListUser(CategoryActivity.this, listUser);
                            mngCat.serializationListCategory(CategoryActivity.this, decreasing(listCategory), usr.getUser());
                        } else {
                            listUser.remove(usr);
                            usr.setSort(3);
                            listUser.add(usr);
                            mngUsr.serializationListUser(CategoryActivity.this, listUser);
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
        PopupMenu popup = new PopupMenu(CategoryActivity.this, v, Gravity.END, 0, style);
        popup.setOnMenuItemClickListener(CategoryActivity.this);
        popup.inflate(menu);
        popup.show();
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public ArrayList<Category> increasing(ArrayList<Category> list) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category lhs, Category rhs) {
                return lhs.getCat().toLowerCase().compareTo(rhs.getCat().toLowerCase());
            }
        });
        return list;
    }

    public ArrayList<Category> decreasing(ArrayList<Category> list) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category lhs, Category rhs) {
                return rhs.getCat().toLowerCase().compareTo(lhs.getCat().toLowerCase());
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}



package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

public class ViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ActionMode.Callback {
    boolean doubleBackToExitPressedOnce;
    private ManageUser mngUsr;
    private CoordinatorLayout cl;
    private ArrayList<User> listUser;
    private ManageApp mngApp;
    private LogApp log;
    private Button settingsButton;
    private Button searchButton;
    private User usr;
    private TextView wellcome;
    private TextView wellcomeMini;
    private TextView wellcome2;
    private RecyclerViewAdapter mAdapter;
    private boolean isMultiSelect;
    private ArrayList<String> selectedIds;
    private ActionMode actionMode;
    private FloatingActionButton setting;
    private FloatingActionButton search;
    private FloatingActionButton add;
    private ManageCategory mngCat;
    private ArrayList<Category> listCategory;
    private Category cat;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        final Toolbar toolbar = findViewById(R.id.viewToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        cat = (Category) (Objects.requireNonNull(getIntent().getExtras())).get("category");
        cl = findViewById(R.id.viewActivityLay);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        isMultiSelect = false;
        doubleBackToExitPressedOnce = false;
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            cat = mngCat.findAndGetCategory(listCategory, Objects.requireNonNull(cat).getCat());
            if (cat != null) {
                if (cat.getSort() == 1) {
                    if (cat.getListAcc() != null) {
                        listCategory.remove(cat);
                        cat.setListAcc(increasing(cat.getListAcc()));
                        listCategory.add(cat);
                        mngCat.serializationListCategory(this, listCategory, cat.getCat());
                    }
                } else if (usr.getSort() == 2) {
                    if (cat.getListAcc() != null) {
                        listCategory.remove(cat);
                        cat.setListAcc(decreasing(cat.getListAcc()));
                        listCategory.add(cat);
                        mngCat.serializationListCategory(this, listCategory, cat.getCat());
                    }
                }
                wellcome = findViewById(R.id.wellcomeText);
                wellcome.setText(cat.getCat());
                wellcomeMini = findViewById(R.id.wellcomeMiniText);
                if (cat.getListAcc() != null)
                    wellcomeMini.setText("Numero account: " + cat.getListAcc().size());
                else
                    wellcomeMini.setText("Numero account: 0");
                wellcome2 = findViewById(R.id.wellcomeTextToolbar);
                wellcome2.setText("Lista della categoria " + cat.getCat());
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

                mAdapter = new RecyclerViewAdapter(this, usr, cat);
                ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(recyclerView);
                GridLayoutManager manager = new GridLayoutManager(this, usr.getColAcc(), GridLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(mAdapter);
                recyclerView.addItemDecoration(new SpacesItemDecoration(20, usr.getColAcc()));
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (isMultiSelect) {
                            multiSelect(position);
                        } else {
                            goToShowElementActivity(usr, mAdapter.getItem(position), cat);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

            } else {
                notifyUser("Categoria non rilevata. Impossibile visualizzare la lista degli account.");
                goToMainActivity();
            }
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
        intent.putExtra("category", cat);
        startActivity(intent);
    }

    public void goToCategoryActivity() {
        Intent intent = new Intent(ViewActivity.this, CategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
    }

    public void goToCategoryChoseActivity(String opt) {
        Intent intent = new Intent(ViewActivity.this, CategoryChoseActivity.class);
        ArrayList<Account> acc = new ArrayList<>();
        for (String s : selectedIds)
            acc.add(mngCat.findAccount(cat.getListAcc(), s));
        intent.putExtra("owner", usr);
        intent.putExtra("category", cat);
        intent.putExtra("listAccount", acc);
        intent.putExtra("option", opt);
        startActivity(intent);
        finish();
    }

    public void goToShowElementActivity(User usr, Account acc, Category cat) {
        Intent intent = new Intent(ViewActivity.this, ShowElementActivity.class);
        intent.putExtra("account", acc);
        intent.putExtra("owner", usr);
        intent.putExtra("category", cat);
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
        startActivity(getIntent());
        finish();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, final Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.my_context_menu_account, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void multiSelect(int position) {
        Account data = mAdapter.getItem(position);
        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(data.getName()))
                    selectedIds.remove(data.getName());
                else
                    selectedIds.add(data.getName());
                mAdapter.setSelectedIds(selectedIds);
            }
        }
        if (selectedIds.size() == cat.getListAcc().size()) {
            Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Deseleziona tutto");
        } else Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Seleziona tutto");
        Objects.requireNonNull(actionMode).setTitle("Elem: " + selectedIds.size());
    }

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.edit) {
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
                    et.setText("Sei sicuro di voler eliminare " + selectedIds.get(0) + " nella lista " + cat.getCat() + "?");
                else
                    et.setText("Sei sicuro di voler eliminare questi " + selectedIds.size() + " account selezionati della lista " + cat.getCat() + "?");
                Button yes = popupView.findViewById(R.id.yes);
                Button no = popupView.findViewById(R.id.no);
                final Account acc;
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listCategory.remove(cat);
                        ArrayList<Account> listToRemove = cat.getListAcc();
                        for (String data : selectedIds) {
                            for (Account a : cat.getListAcc()) {
                                if (a.getName().toLowerCase().equals(data.toLowerCase())) {
                                    listToRemove.remove(a);
                                    break;
                                }
                            }
                        }
                        cat.setListAcc(listToRemove);
                        listCategory.add(cat);
                        if (selectedIds.size() == 1)
                            notifyUser(selectedIds.get(0) + " è stato rimosso con successo!");
                        else
                            notifyUser(selectedIds.size() + " account sono stati rimossi con successo!");
                        popupWindow.dismiss();
                        mngCat.serializationListCategory(ViewActivity.this, listCategory, usr.getUser());
                        refresh();
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
            return true;
        }
        if (menuItem.getItemId() == R.id.select_all) {
            String s = "Seleziona tutto";
            if (menuItem.getTitle().toString().toLowerCase().equals(s.toLowerCase())) {
                menuItem.setTitle("Deseleziona tutto");
                selectedIds.clear();
                for (Account a : cat.getListAcc()) {
                    selectedIds.add(a.getName());
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
        if (menuItem.getItemId() == R.id.copy) {
            if (!selectedIds.isEmpty()) {
                goToCategoryChoseActivity("copy");
            } else notifyUser("Nessun account è stato selezionato per la copia.");
            return true;
        }
        if (menuItem.getItemId() == R.id.cut) {
            if (!selectedIds.isEmpty()) {
                goToCategoryChoseActivity("cut");
            } else notifyUser("Nessun account è stato selezionato per lo spostamento.");
            return true;
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        settingsButton.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        setting.setVisibility(View.GONE);
                        search.setVisibility(View.GONE);
                        add.setVisibility(View.GONE);
                        actionMode = startActionMode(ViewActivity.this);
                    }
                }
                multiSelect(-1);
                return true;
            case R.id.sort:
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_sort, null);
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
                            listCategory.remove(cat);
                            cat.setSort(1);
                            cat.setListAcc(increasing(cat.getListAcc()));
                            listCategory.add(cat);
                            mngCat.serializationListCategory(ViewActivity.this, listCategory, usr.getUser());
                        } else if (checkedId == rb2.getId()) {
                            listCategory.remove(cat);
                            cat.setSort(2);
                            cat.setListAcc(decreasing(cat.getListAcc()));
                            listCategory.add(cat);
                            mngCat.serializationListCategory(ViewActivity.this, listCategory, usr.getUser());
                        } else {
                            listCategory.remove(cat);
                            cat.setSort(3);
                            listCategory.add(cat);
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
        super.onBackPressed();
        goToCategoryActivity();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
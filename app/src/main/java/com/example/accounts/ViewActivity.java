package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"deprecation", "CollectionAddAllCanBeReplacedWithConstructor"})
public class ViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ActionMode.Callback {
    private CoordinatorLayout layoutViewActivity;
    private ManageApp mngApp;
    private ManageCategory mngCat;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<Category> listCategory;
    private ArrayList<String> selectedIds;
    private TextView textView;
    private TextView textViewMini;
    private TextView textViewToolbar;
    private LogApp log;
    private User usr;
    private Category category;
    private Button settingsButton;
    private Button searchButton;
    private FloatingActionButton floatingButtonSetting;
    private FloatingActionButton floatingButtonSearch;
    private FloatingActionButton floatingButtonAddNewAccount;
    private ActionMode actionMode;
    private boolean isMultiSelect;
    private boolean b, a;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        final Toolbar toolbar = findViewById(R.id.viewToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layoutViewActivity = findViewById(R.id.viewActivityLay);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        isMultiSelect = false;
        a = true;
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            b = false;
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            if (category != null) {
                if (category.getSort() == 1) {
                    if (category.getListAcc() != null) {
                        listCategory.remove(category);
                        category.setListAcc(increasing(category.getListAcc()));
                        listCategory.add(category);
                        mngCat.serializationListCategory(this, listCategory, category.getCat());
                    }
                } else if (usr.getSort() == 2) {
                    if (category.getListAcc() != null) {
                        listCategory.remove(category);
                        category.setListAcc(decreasing(category.getListAcc()));
                        listCategory.add(category);
                        mngCat.serializationListCategory(this, listCategory, category.getCat());
                    }
                }
                textView = findViewById(R.id.welcomeText);
                textView.setText(Html.fromHtml("<b>" + category.getCat() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                textViewMini = findViewById(R.id.wellcomeMiniText);
                if (category.getListAcc() != null)
                    textViewMini.setText(Html.fromHtml("Numero account: <b>" + category.getListAcc().size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                else
                    textViewMini.setText(Html.fromHtml("Numero account: <b>0</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                textViewToolbar = findViewById(R.id.wellcomeTextToolbar);
                textViewToolbar.setText(Html.fromHtml("Lista della categoria <b>" + category.getCat() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                textViewToolbar.setVisibility(View.INVISIBLE);
                AppBarLayout appBar = findViewById(R.id.viewBarToolbar);
                appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = false;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        textView.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                        textViewMini.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            isShow = true;
                            textViewToolbar.setVisibility(View.VISIBLE);
                            if (a) {
                                settingsButton.setVisibility(View.VISIBLE);
                                searchButton.setVisibility(View.VISIBLE);
                            }
                        } else if (isShow) {
                            isShow = false;
                            textViewToolbar.setVisibility(View.INVISIBLE);
                            settingsButton.setVisibility(View.INVISIBLE);
                            searchButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                searchButton = findViewById(R.id.searchButton);
                searchButton.setVisibility(View.INVISIBLE);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToSearchActivity();
                    }
                });
                settingsButton = findViewById(R.id.settingsButton);
                settingsButton.setVisibility(View.INVISIBLE);
                settingsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMenu(R.style.rounded_menu_style_toolbar, R.menu.popup_account, v);
                    }
                });
                floatingButtonSearch = findViewById(R.id.searchFloatingButton);
                floatingButtonSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToSearchActivity();
                    }
                });
                floatingButtonSetting = findViewById(R.id.settingsFloatingButton);
                floatingButtonSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMenu(R.style.rounded_menu_style, R.menu.popup_account, v);
                    }
                });
                floatingButtonAddNewAccount = findViewById(R.id.addFloatingButton);
                floatingButtonAddNewAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToAddActivity();
                    }
                });
                mAdapter = new RecyclerViewAdapter(this, usr, category);
                ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(recyclerView);
                GridLayoutManager manager = new GridLayoutManager(this, usr.getColAcc(), GridLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(mAdapter);
                recyclerView.addItemDecoration(new SpacesItemDecoration(20));
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (isMultiSelect) {
                            multiSelect(position);
                        } else {
                            goToShowElementActivity(mAdapter.getItem(position), category);
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
            notifyUser("Credenziali non rilevate. Impossibile visualizzare la lista degli account.");
            goToMainActivity();
        }
    }

    public void refresh() {
        Intent intent = new Intent(ViewActivity.this, ViewActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToAddActivity() {
        Intent intent = new Intent(ViewActivity.this, AddActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
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
            acc.add(mngCat.findAndGetAccount(category.getListAcc(), s));
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        intent.putExtra("listAccount", acc);
        intent.putExtra("option", opt);
        startActivity(intent);
    }

    public void goToShowElementActivity(Account account, Category category) {
        Intent intent = new Intent(ViewActivity.this, ShowElementActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void goToSettingActivity() {
        Intent intent = new Intent(ViewActivity.this, SettingActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    public void goToSearchActivity() {
        Intent intent = new Intent(ViewActivity.this, SearchActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, final Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        if (b)
            inflater.inflate(R.menu.my_context_menu_account, menu);
        else
            inflater.inflate(R.menu.my_context_menu_account_delete, menu);
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
        if (selectedIds.size() == category.getListAcc().size()) {
            Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Nessuno");
        } else Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Tutti");
        Objects.requireNonNull(actionMode).setTitle(Html.fromHtml("Sel: <b>" + selectedIds.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_id) {
            if (!selectedIds.isEmpty()) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewSecurity = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                final PopupWindow popupWindowSecurity = new PopupWindow(popupViewSecurity, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowSecurity.setOutsideTouchable(true);
                popupWindowSecurity.setFocusable(true);
                popupWindowSecurity.setBackgroundDrawable(new BitmapDrawable());
                View parent = layoutViewActivity.getRootView();
                popupWindowSecurity.showAtLocation(parent, Gravity.CENTER, 0, 0);
                TextView popupText = popupViewSecurity.findViewById(R.id.securityText);
                if (selectedIds.size() == 1)
                    popupText.setText(Html.fromHtml("Sei sicuro di voler eliminare <b>" + selectedIds.get(0) + "</b> nella lista <b>" + category.getCat() + "</b>?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                else
                    popupText.setText(Html.fromHtml("Sei sicuro di voler eliminare questi <b>" + selectedIds.size() + "</b> account selezionati della lista <b>" + category.getCat() + "</b>?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                Button yes = popupViewSecurity.findViewById(R.id.yes);
                Button no = popupViewSecurity.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listCategory.remove(category);
                        ArrayList<Account> listToRemove = category.getListAcc();
                        for (String singleAccountString : selectedIds) {
                            for (Account singleAccount : category.getListAcc()) {
                                if (singleAccount.getName().toLowerCase().equals(singleAccountString.toLowerCase())) {
                                    listToRemove.remove(singleAccount);
                                    break;
                                }
                            }
                        }
                        category.setListAcc(listToRemove);
                        listCategory.add(category);
                        popupWindowSecurity.dismiss();
                        mngCat.serializationListCategory(ViewActivity.this, listCategory, usr.getUser());
                        refresh();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowSecurity.dismiss();
                    }
                });
            } else notifyUser("Nessun account è stato selezionato per la rimozione.");
            return true;
        }
        if (menuItem.getItemId() == R.id.select_all) {
            if (menuItem.getTitle().toString().toLowerCase().equals("tutti")) {
                menuItem.setTitle("Nessuno");
                selectedIds.clear();
                for (Account singleAccount : category.getListAcc()) {
                    selectedIds.add(singleAccount.getName());
                }
                mAdapter.setSelectedIds(selectedIds);
                Objects.requireNonNull(actionMode).setTitle(Html.fromHtml("Sel: <b>" + selectedIds.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                menuItem.setTitle("Tutti");
                selectedIds.clear();
                mAdapter.setSelectedIds(selectedIds);
                Objects.requireNonNull(actionMode).setTitle(Html.fromHtml("Sel: <b>" + selectedIds.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
        }
        if (menuItem.getItemId() == R.id.copy_id) {
            if (!selectedIds.isEmpty()) {
                goToCategoryChoseActivity("copy");
            } else notifyUser("Nessun account è stato selezionato per la copia.");
            return true;
        }
        if (menuItem.getItemId() == R.id.cut_id) {
            if (!selectedIds.isEmpty()) {
                if (listCategory.size() <= 1)
                    notifyUser("Non ci sono categorie su cui effetturare lo spostamento.");
                else
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
        floatingButtonSetting.setVisibility(View.VISIBLE);
        floatingButtonSearch.setVisibility(View.VISIBLE);
        floatingButtonAddNewAccount.setVisibility(View.VISIBLE);
        a = true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.rename):
                LayoutInflater layoutInflaterRename = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewRenameCategory = Objects.requireNonNull(layoutInflaterRename).inflate(R.layout.popup_rename_category, (ViewGroup) findViewById(R.id.categoryRenamePopup));
                final PopupWindow popupWindowRenameCategory = new PopupWindow(popupViewRenameCategory, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowRenameCategory.setOutsideTouchable(true);
                popupWindowRenameCategory.setFocusable(true);
                popupWindowRenameCategory.setBackgroundDrawable(new BitmapDrawable());
                View parent = layoutViewActivity.getRootView();
                popupWindowRenameCategory.showAtLocation(parent, Gravity.CENTER, 0, 0);
                ((TextView) popupViewRenameCategory.findViewById(R.id.categoryRenameText)).setText(Html.fromHtml("Come vuoi rinominare la categoria <b>" + category.getCat() + "</b>?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                final EditText popupRenameCategoryText = popupViewRenameCategory.findViewById(R.id.categoryRenameEditText);
                Button conf = popupViewRenameCategory.findViewById(R.id.confirmation);
                conf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupRenameCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewActivity.this, R.color.colorAccent)));
                        if (checkGapError(popupRenameCategoryText.getText().toString())) {
                            popupRenameCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewActivity.this, R.color.errorEditText)));
                            return;
                        }
                        ArrayList<Category> listCategoryApp = new ArrayList<>();
                        listCategoryApp.addAll(listCategory);
                        listCategoryApp.remove(category);
                        if (!mngCat.findCategory(listCategoryApp, popupRenameCategoryText.getText().toString())) {
                            category.setCat(fixName(popupRenameCategoryText.getText().toString()));
                            listCategoryApp.add(category);
                            mngCat.serializationListCategory(ViewActivity.this, listCategoryApp, usr.getUser());
                            refresh();
                            popupWindowRenameCategory.dismiss();
                        } else {
                            popupRenameCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewActivity.this, R.color.errorEditText)));
                            notifyUser(Html.fromHtml("Categoria <b>" + popupRenameCategoryText.getText().toString() + "</b> già esistente.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                        }
                    }
                });
                return true;
            case (R.id.delete):
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        settingsButton.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        floatingButtonSetting.setVisibility(View.GONE);
                        floatingButtonSearch.setVisibility(View.GONE);
                        floatingButtonAddNewAccount.setVisibility(View.GONE);
                        b = false;
                        a = false;
                        actionMode = startActionMode(ViewActivity.this);
                    }
                }
                multiSelect(-1);
                return true;
            case R.id.copy_cut:
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        settingsButton.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        floatingButtonSetting.setVisibility(View.GONE);
                        floatingButtonSearch.setVisibility(View.GONE);
                        floatingButtonAddNewAccount.setVisibility(View.GONE);
                        b = true;
                        a = false;
                        actionMode = startActionMode(ViewActivity.this);
                    }
                }
                multiSelect(-1);
                return true;
            case R.id.sort:
                Category categoryApp = mngCat.findAndGetCategory(mngCat.deserializationListCategory(this, usr.getUser()), category.getCat());
                LayoutInflater layoutInflaterSort = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewSort = Objects.requireNonNull(layoutInflaterSort).inflate(R.layout.popup_sort, (ViewGroup) findViewById(R.id.popupSort));
                final PopupWindow popupWindowSort = new PopupWindow(popupViewSort, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowSort.setOutsideTouchable(true);
                popupWindowSort.setFocusable(true);
                popupWindowSort.setBackgroundDrawable(new BitmapDrawable());
                View parentSort = layoutViewActivity.getRootView();
                popupWindowSort.showAtLocation(parentSort, Gravity.CENTER, 0, 0);
                RadioGroup radioGroupSort = popupViewSort.findViewById(R.id.radioGroupSorter);
                final RadioButton radioButtonIncreasing = popupViewSort.findViewById(R.id.increasing);
                final RadioButton radioButtonDecreasing = popupViewSort.findViewById(R.id.decreasing);
                final RadioButton radioButtonCostumized = popupViewSort.findViewById(R.id.customized);
                if (categoryApp.getSort() == 1)
                    radioGroupSort.check(radioButtonIncreasing.getId());
                else if (categoryApp.getSort() == 2)
                    radioGroupSort.check(radioButtonDecreasing.getId());
                else
                    radioGroupSort.check(radioButtonCostumized.getId());
                radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == radioButtonIncreasing.getId()) {
                            listCategory.remove(category);
                            category.setSort(1);
                            category.setListAcc(increasing(category.getListAcc()));
                            listCategory.add(category);
                        } else if (checkedId == radioButtonDecreasing.getId()) {
                            listCategory.remove(category);
                            category.setSort(2);
                            category.setListAcc(decreasing(category.getListAcc()));
                            listCategory.add(category);
                        } else {
                            listCategory.remove(category);
                            category.setSort(3);
                            listCategory.add(category);
                        }
                        mngCat.serializationListCategory(ViewActivity.this, listCategory, usr.getUser());
                        popupWindowSort.dismiss();
                        refresh();
                    }
                });
                return true;
            case R.id.setting:
                goToSettingActivity();
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

    public boolean checkGapError(String s) {
        if (isInvalidWord(s)) {
            notifyUser("Campo non valido.");
            return true;
        }
        return false;
    }

    public boolean isInvalidWord(String word) {
        return ((!word.matches("[A-Za-z0-9@#$%^&+=!?._-]*")) || (word.isEmpty()));
    }

    public String fixName(String name) {
        if (name.isEmpty()) return name;
        else {
            name = name.toLowerCase();
            String name1 = name.substring(0, 1).toUpperCase();
            String name2 = name.substring(1).toLowerCase();
            return name1.concat(name2);
        }
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
        goToCategoryActivity();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
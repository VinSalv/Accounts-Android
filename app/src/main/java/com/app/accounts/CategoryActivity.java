package com.app.accounts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
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

@SuppressWarnings({"deprecation", "SameParameterValue"})
public class CategoryActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ActionMode.Callback {
    private CoordinatorLayout layoutCategoryActivity;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ManageCategory mngCat;
    private ArrayList<User> listUser;
    private ArrayList<Category> listCategory;
    private ArrayList<String> selectedIds;
    private TextView welcome;
    private TextView welcomeMini;
    private TextView welcomeToolbar;
    private RecyclerCategoryAdapter categoryAdapter;
    private Button settingsButton;
    private Button searchButton;
    private FloatingActionButton floatingButtonSettings;
    private FloatingActionButton floatingButtonSearch;
    private FloatingActionButton floatingButtonAddNewCategory;
    private LogApp logApp;
    private User usr;
    private boolean isMultiSelect;
    private ActionMode actionMode;
    private boolean doubleBackToExitPressedOnce;
    private boolean b;
    private int attempts;
    private boolean blockBack;
    private PopupWindow popupWindowCheck;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_category);
        final Toolbar toolbar = findViewById(R.id.catToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layoutCategoryActivity = findViewById(R.id.catActivityLay);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        isMultiSelect = false;
        doubleBackToExitPressedOnce = false;
        b = true;
        mngApp = new ManageApp();
        logApp = mngApp.deserializationFlag(CategoryActivity.this);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(CategoryActivity.this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            blockBack = true;
            attempts = 3;
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(CategoryActivity.this,usr.getUser());
            if (usr.getSort() == 1)
                mngCat.serializationListCategory(CategoryActivity.this,increasing(listCategory), usr.getUser());
            else if (usr.getSort() == 2)
                mngCat.serializationListCategory(CategoryActivity.this,decreasing(listCategory), usr.getUser());
            listCategory = mngCat.deserializationListCategory(CategoryActivity.this,usr.getUser());
            welcome = findViewById(R.id.welcomeText);
            welcome.setText(Html.fromHtml("Benvenuto <b>" + usr.getUser() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            welcomeMini = findViewById(R.id.wellcomeMiniText);
            welcomeMini.setText(Html.fromHtml("Numero categorie: <b>" + listCategory.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            welcomeToolbar = findViewById(R.id.wellcomeTextToolbar);
            welcomeToolbar.setText(Html.fromHtml("Lista categorie di <b>" + usr.getUser() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            welcomeToolbar.setVisibility(View.INVISIBLE);
            settingsButton = findViewById(R.id.settingsButton);
            settingsButton.setVisibility(View.INVISIBLE);
            searchButton = findViewById(R.id.searchButton);
            searchButton.setVisibility(View.INVISIBLE);
            floatingButtonSettings = findViewById(R.id.settingsFloatingButton);
            floatingButtonSearch = findViewById(R.id.searchFloatingButton);
            floatingButtonAddNewCategory = findViewById(R.id.addFloatingButton);
            AppBarLayout appBar = findViewById(R.id.catBarToolbar);
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    welcome.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    welcomeMini.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true;
                        welcomeToolbar.setVisibility(View.VISIBLE);
                        if (b) {
                            settingsButton.setVisibility(View.VISIBLE);
                            searchButton.setVisibility(View.VISIBLE);
                        }
                    } else if (isShow) {
                        isShow = false;
                        welcomeToolbar.setVisibility(View.INVISIBLE);
                        settingsButton.setVisibility(View.INVISIBLE);
                        searchButton.setVisibility(View.INVISIBLE);
                    }
                }
            });
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu(R.style.rounded_menu_style_toolbar, R.menu.popup_category, v);
                }
            });
            floatingButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu(R.style.rounded_menu_style, R.menu.popup_category, v);
                }
            });
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSearchActivity();
                }
            });
            floatingButtonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSearchActivity();
                }
            });
            floatingButtonAddNewCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupViewAddNewCategory = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_category, (ViewGroup) findViewById(R.id.categoryPopup));
                    final PopupWindow popupWindowAddNewCategory = new PopupWindow(popupViewAddNewCategory, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowAddNewCategory.setOutsideTouchable(true);
                    popupWindowAddNewCategory.setFocusable(true);
                    popupWindowAddNewCategory.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutCategoryActivity.getRootView();
                    popupWindowAddNewCategory.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText popupNewCategoryText = popupViewAddNewCategory.findViewById(R.id.categoryEditText);
                    Button conf = popupViewAddNewCategory.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupNewCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryActivity.this, R.color.colorAccent)));
                            if (checkGapError(popupNewCategoryText.getText().toString())) {
                                popupNewCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryActivity.this, R.color.errorEditText)));
                                return;
                            }
                            if (!mngCat.findCategory(listCategory, popupNewCategoryText.getText().toString())) {
                                listCategory.add(new Category(fixName(popupNewCategoryText.getText().toString()), 1));
                                mngCat.serializationListCategory(CategoryActivity.this,listCategory, usr.getUser());
                                refresh();
                                popupWindowAddNewCategory.dismiss();
                            } else {
                                popupNewCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryActivity.this, R.color.errorEditText)));
                                notifyUser(Html.fromHtml("Categoria <b>" + popupNewCategoryText.getText().toString() + "</b> già esistente.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                            }
                        }
                    });
                }
            });
            categoryAdapter = new RecyclerCategoryAdapter(this, listCategory, usr);
            ItemTouchHelper.Callback callback = new ItemMoveCategoryCallback(categoryAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
            GridLayoutManager manager = new GridLayoutManager(this, usr.getColCat(), GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(categoryAdapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration(20));
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isMultiSelect) {
                        multiSelect(position);
                    } else {
                        goToViewActivity(categoryAdapter.getItem(position));
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));
        } else {
            notifyUser("Credenziali non rilevate. Impossibile visualizzare la lista degli account.");
            goToMainActivity();
        }
    }

    public void refresh() {
        startActivity(getIntent());
        finish();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity(Category cat) {
        Intent intent = new Intent(CategoryActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("category", cat);
        startActivity(intent);
    }

    public void goToSettingActivity() {
        Intent intent = new Intent(CategoryActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("cat", "");
        startActivity(intent);
    }

    public void goToSearchActivity() {
        Intent intent = new Intent(CategoryActivity.this, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("cat", "");
        startActivity(intent);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, final Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.my_context_menu_category, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void multiSelect(int position) {
        Category categorySelected = categoryAdapter.getItem(position);
        if (categorySelected != null) {
            if (actionMode != null) {
                if (selectedIds.contains(categorySelected.getCat()))
                    selectedIds.remove(categorySelected.getCat());
                else
                    selectedIds.add(categorySelected.getCat());
                categoryAdapter.setSelectedIds(selectedIds);
            }
        }
        if (selectedIds.size() == listCategory.size()) {
            Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Nessuno");
        } else
            Objects.requireNonNull(actionMode).getMenu().getItem(0).setTitle("Tutti");
        Objects.requireNonNull(actionMode).setTitle(Html.fromHtml("Sel: <b>" + selectedIds.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_id) {
            if (!selectedIds.isEmpty()) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewSecurityToDeleteCategory = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                final PopupWindow popupWindowSecurityToDeleteCategory = new PopupWindow(popupViewSecurityToDeleteCategory, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowSecurityToDeleteCategory.setOutsideTouchable(true);
                popupWindowSecurityToDeleteCategory.setFocusable(true);
                popupWindowSecurityToDeleteCategory.setBackgroundDrawable(new BitmapDrawable());
                View parent = layoutCategoryActivity.getRootView();
                popupWindowSecurityToDeleteCategory.showAtLocation(parent, Gravity.CENTER, 0, 0);
                TextView popupSecurityToDeleteCategoryViewByIdText = popupViewSecurityToDeleteCategory.findViewById(R.id.securityText);
                if (selectedIds.size() == 1)
                    popupSecurityToDeleteCategoryViewByIdText.setText(Html.fromHtml("Sei sicuro di voler eliminare <b>" + selectedIds.get(0) + "</b> dalla lista delle tue categorie? (Verrano eliminati anche i relativi account)", HtmlCompat.FROM_HTML_MODE_LEGACY));
                else
                    popupSecurityToDeleteCategoryViewByIdText.setText(Html.fromHtml("Sei sicuro di voler eliminare queste <b>" + selectedIds.size() + "</b> categorie selezionate? (Verrano eliminati anche i relativi account)", HtmlCompat.FROM_HTML_MODE_LEGACY));
                Button yes = popupViewSecurityToDeleteCategory.findViewById(R.id.yes);
                Button no = popupViewSecurityToDeleteCategory.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String singleCategoryString : selectedIds) {
                            Category category = mngCat.findAndGetCategory(listCategory, singleCategoryString);
                            if (category != null)
                                listCategory.remove(category);
                            else
                                notifyUser(Html.fromHtml("Categoria <b>" + singleCategoryString + "</b> non rimossa. Non è stato rilevato nella lista delle tue categorie.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                        }
                        popupWindowSecurityToDeleteCategory.dismiss();
                        mngCat.serializationListCategory(CategoryActivity.this,listCategory, usr.getUser());
                        startActivity(getIntent());
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowSecurityToDeleteCategory.dismiss();
                    }
                });
            } else notifyUserShortWay("Nessuna categoria è stata selezionata per la rimozione.");
            return true;
        }
        if (menuItem.getItemId() == R.id.select_all) {
            if (menuItem.getTitle().toString().toLowerCase().equals("tutti")) {
                menuItem.setTitle("Nessuno");
                selectedIds.clear();
                for (Category singleCategory : listCategory) {
                    selectedIds.add(singleCategory.getCat());
                }
                categoryAdapter.setSelectedIds(selectedIds);
                Objects.requireNonNull(actionMode).setTitle(Html.fromHtml("Sel: <b>" + selectedIds.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                menuItem.setTitle("Tutti");
                selectedIds.clear();
                categoryAdapter.setSelectedIds(selectedIds);
                Objects.requireNonNull(actionMode).setTitle(Html.fromHtml("Sel: <b>" + selectedIds.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
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
        categoryAdapter.setSelectedIds(new ArrayList<String>());
        settingsButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        floatingButtonSettings.setVisibility(View.VISIBLE);
        floatingButtonSearch.setVisibility(View.VISIBLE);
        floatingButtonAddNewCategory.setVisibility(View.VISIBLE);
        b = true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        settingsButton.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        floatingButtonSettings.setVisibility(View.GONE);
                        floatingButtonSearch.setVisibility(View.GONE);
                        floatingButtonAddNewCategory.setVisibility(View.GONE);
                        b = false;
                        actionMode = startActionMode(CategoryActivity.this);
                    }
                }
                multiSelect(-1);
                return true;
            case R.id.sort:
                User usrApp = mngUsr.findUser(listUser = mngUsr.deserializationListUser(CategoryActivity.this), usr.getUser());
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View popupViewSort = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_sort, null);
                final PopupWindow popupWindowSort = new PopupWindow(popupViewSort, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowSort.setOutsideTouchable(true);
                popupWindowSort.setFocusable(true);
                popupWindowSort.setBackgroundDrawable(new BitmapDrawable());
                View parent = layoutCategoryActivity.getRootView();
                popupWindowSort.showAtLocation(parent, Gravity.CENTER, 0, 0);
                RadioGroup radioGroup = popupViewSort.findViewById(R.id.radioGroupSorter);
                final RadioButton increasingRadioButton = popupViewSort.findViewById(R.id.increasing);
                final RadioButton decreasingRadioButton = popupViewSort.findViewById(R.id.decreasing);
                final RadioButton customizedRadioButton = popupViewSort.findViewById(R.id.customized);
                if (usrApp.getSort() == 1)
                    radioGroup.check(increasingRadioButton.getId());
                else if (usrApp.getSort() == 2)
                    radioGroup.check(decreasingRadioButton.getId());
                else
                    radioGroup.check(customizedRadioButton.getId());
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == increasingRadioButton.getId()) {
                            listUser.remove(usr);
                            usr.setSort(1);
                            listUser.add(usr);
                            mngUsr.serializationListUser(CategoryActivity.this, listUser);
                            mngCat.serializationListCategory(CategoryActivity.this,increasing(listCategory), usr.getUser());
                        } else if (checkedId == decreasingRadioButton.getId()) {
                            listUser.remove(usr);
                            usr.setSort(2);
                            listUser.add(usr);
                            mngUsr.serializationListUser(CategoryActivity.this, listUser);
                            mngCat.serializationListCategory(CategoryActivity.this,decreasing(listCategory), usr.getUser());
                        } else {
                            listUser.remove(usr);
                            usr.setSort(3);
                            listUser.add(usr);
                            mngUsr.serializationListUser(CategoryActivity.this, listUser);
                        }
                        popupWindowSort.dismiss();
                        refresh();
                    }
                });
                return true;
            case R.id.setting:
                goToSettingActivity();
                return true;
            case R.id.exit:
                logApp = new LogApp();
                mngApp.serializationFlag(CategoryActivity.this,logApp);
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

    private void notifyUserShortWay(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_SHORT).show();
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
        if (blockBack) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                System.exit(0);
            }
            this.doubleBackToExitPressedOnce = true;
            notifyUser(Html.fromHtml("Premi nuovamente <b>INDIETRO</b> per uscire dall'applicazione.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                goToMainActivity();
            }
            this.doubleBackToExitPressedOnce = true;
            notifyUser(Html.fromHtml("Premi nuovamente <b>INDIETRO</b> per tornare alla schermata principale.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRestart() {
        super.onRestart();
        if (!blockBack)
            popupWindowCheck.dismiss();
        layoutCategoryActivity.setVisibility(View.INVISIBLE);
        BiometricManager biometricManager = BiometricManager.from(CategoryActivity.this);
        if (usr.getFinger() && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
            biometricAuthentication(layoutCategoryActivity);
        else {
            recheckPass();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showPass(final EditText et, ImageButton showPass) {
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        et.setInputType(InputType.TYPE_NULL);
                        break;
                    case MotionEvent.ACTION_UP:
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                notifyUser("Autenticazione errore: " + errString + ".");
                super.onAuthenticationError(errorCode, errString);
                recheckPass();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                notifyUserShortWay("Autenticazione effettuata.");
                super.onAuthenticationSucceeded(result);
                layoutCategoryActivity.setVisibility(View.VISIBLE);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void authenticateUser(View view) {
        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Lettura impronta digitale")
                .setSubtitle("Autenticazione richiesta per continuare")
                .setDescription("Account con autenticazione biometrica per proteggere i dati.")
                .setNegativeButton("Annulla", this.getMainExecutor(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notifyUserShortWay("Autenticazione annullata.");
                                recheckPass();
                            }
                        })
                .build();
        biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(), getAuthenticationCallback());
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void biometricAuthentication(CoordinatorLayout lay) {
        if (!checkBiometricSupport()) {
            return;
        }
        authenticateUser(lay);
    }

    private Boolean checkBiometricSupport() {
        KeyguardManager keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        PackageManager packageManager = this.getPackageManager();
        if (keyguardManager != null && !keyguardManager.isKeyguardSecure()) {
            notifyUser("Lock screen security non abilitato nelle impostazioni.");
            recheckPass();
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Permesso impronte digitali non abilitato.");
            recheckPass();
            return false;
        }
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        }
        return true;
    }

    private CancellationSignal getCancellationSignal() {
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                notifyUserShortWay("Cancelled via signal");
            }
        });
        return cancellationSignal;
    }

    public void recheckPass() {
        blockBack = false;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupViewCheck = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password_match_parent, (ViewGroup) findViewById(R.id.passSecurityPopupMatchParent));
        popupWindowCheck = new PopupWindow(popupViewCheck, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindowCheck.setBackgroundDrawable(new BitmapDrawable());
        View parent = layoutCategoryActivity.getRootView();
        popupWindowCheck.showAtLocation(parent, Gravity.CENTER, 0, 0);
        final EditText popupText = popupViewCheck.findViewById(R.id.passSecurityEditText);
        Button conf = popupViewCheck.findViewById(R.id.confirmation);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryActivity.this, R.color.colorAccent)));
                if (popupText.getText().toString().isEmpty()) {
                    popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryActivity.this, R.color.errorEditText)));
                    notifyUser("Il campo password è vuoto");
                } else {
                    if (popupText.getText().toString().equals(usr.getPassword())) {
                        layoutCategoryActivity.setVisibility(View.VISIBLE);
                        blockBack = true;
                        attempts = 3;
                        popupWindowCheck.dismiss();
                    } else {
                        popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryActivity.this, R.color.errorEditText)));
                        attempts--;
                        if (attempts == 2)
                            notifyUserShortWay("Password errata. Hai altri " + attempts + " tentativi");
                        else if (attempts == 1)
                            notifyUserShortWay("Password errata. Hai un ultimo tenativo");
                        else {
                            notifyUserShortWay("Password errata");
                            mngApp.serializationFlag(CategoryActivity.this,new LogApp());
                            goToMainActivity();
                        }
                    }
                }
            }
        });
        ImageButton showPass = popupViewCheck.findViewById(R.id.showPass);
        showPass(popupText, showPass);
        ImageButton cancel = popupViewCheck.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mngApp.serializationFlag(CategoryActivity.this,new LogApp());
                goToMainActivity();
            }
        });
    }
}
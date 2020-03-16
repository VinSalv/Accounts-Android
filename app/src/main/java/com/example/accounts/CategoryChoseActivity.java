package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;


@SuppressWarnings("ALL")
public class CategoryChoseActivity extends AppCompatActivity {
    private CoordinatorLayout layoutCategoryChoseActivity;
    private RecyclerView recyclerView;
    private ManageCategory mngCat;
    private TextView choseText;
    private TextView choseTextMini;
    private TextView choseTextToolbar;
    private ArrayList<Category> listCategory;
    private ArrayList<Account> listAccountTake;
    private ArrayList<Account> listAccountTakeApp;
    private ArrayList<Account> listAccountToSet;
    private ArrayList<String> listCategoryString;
    private RecyclerCategoryChoseAdapter mAdapter;
    private FloatingActionButton addNewCategory;
    private User usr;
    private Category category;
    private Category categoryAdapterPosition;
    private String opt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chose);
        Toolbar toolbar = findViewById(R.id.catChoseToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layoutCategoryChoseActivity = findViewById(R.id.catChoseActivityLay);
        listAccountTake = (ArrayList<Account>) (getIntent().getExtras()).get("listAccount");
        listAccountTakeApp = listAccountTake;
        opt = (String) (Objects.requireNonNull(getIntent().getExtras())).get("option");
        recyclerView = findViewById(R.id.recyclerView);
        ManageUser mngUsr = new ManageUser();
        final ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) (Objects.requireNonNull(getIntent().getExtras())).get("owner")).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            listCategoryString = new ArrayList<>();
            for (Category singleCategory : listCategory)
                listCategoryString.add(singleCategory.getCat());
            categoryAdapterPosition = null;
            choseText = findViewById(R.id.welcomeChoseText);
            choseText.setText("Scegli la categoria");
            choseTextMini = findViewById(R.id.welcomeChoseMiniText);
            if (opt.toLowerCase().equals("cut")) {
                choseTextMini.setText(Html.fromHtml("Numero categorie: <b>" + (listCategory.size() - 1) + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else
                choseTextMini.setText(Html.fromHtml("Numero categorie: <b>" + listCategory.size() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            choseTextToolbar = findViewById(R.id.welcomeChoseTextToolbar);
            choseTextToolbar.setText("Scegli la categoria");
            choseTextToolbar.setVisibility(View.INVISIBLE);
            AppBarLayout appBar = findViewById(R.id.catChoseBarToolbar);
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    choseText.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    choseTextMini.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true;
                        choseTextToolbar.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        choseTextToolbar.setVisibility(View.INVISIBLE);
                    }
                }
            });
            if (opt.toLowerCase().equals("cut")) {
                ArrayList<Category> listCategoryApp = listCategory;
                listCategoryApp.remove(category);
                mAdapter = new RecyclerCategoryChoseAdapter(this, listCategoryApp);
            } else
                mAdapter = new RecyclerCategoryChoseAdapter(this, listCategory);
            GridLayoutManager manager = new GridLayoutManager(this, usr.getColCat(), GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration(20));
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    addNewCategory.setVisibility(View.GONE);
                    categoryAdapterPosition = mAdapter.getItem(position);
                    LayoutInflater layoutInflaterDone = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewDone = Objects.requireNonNull(layoutInflaterDone).inflate(R.layout.popup_done, (ViewGroup) findViewById(R.id.popupDone));
                    final PopupWindow popupWindowDone = new PopupWindow(popupViewDone, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowDone.setOutsideTouchable(false);
                    popupWindowDone.setFocusable(false);
                    popupWindowDone.setBackgroundDrawable(new BitmapDrawable());
                    View parentDone = layoutCategoryChoseActivity.getRootView();
                    popupWindowDone.showAtLocation(parentDone, Gravity.CENTER, 0, 0);
                    Button d = popupViewDone.findViewById(R.id.doneButton);
                    d.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listCategory.remove(mngCat.findAndGetCategory(listCategory, categoryAdapterPosition.getCat()));
                            mngCat.removeFileCategory(CategoryChoseActivity.this, usr.getUser());
                            if (opt.toLowerCase().equals("cut")) {
                                listCategory.remove(mngCat.findAndGetCategory(listCategory, category.getCat()));
                                ArrayList<Account> listAccountCategoryApp = category.getListAcc();
                                listAccountCategoryApp.removeAll(listAccountTakeApp);
                                category.setListAcc(listAccountCategoryApp);
                                listCategory.add(category);
                            }
                            ArrayList<Account> listAccountApp = categoryAdapterPosition.getListAcc();
                            listAccountApp.addAll(listAccountToSet);
                            categoryAdapterPosition.setListAcc(listAccountApp);
                            listCategory.add(categoryAdapterPosition);
                            ArrayList<Category> listCategoryApp = new ArrayList<>();
                            for (String singleStringCategory : listCategoryString) {
                                listCategoryApp.add(mngCat.findAndGetCategory(listCategory, singleStringCategory));
                            }
                            mngCat.serializationListCategory(CategoryChoseActivity.this, listCategoryApp, usr.getUser());
                            goToCategoryActivity();
                            popupWindowDone.dismiss();
                        }
                    });
                    popupViewDone.setVisibility(View.INVISIBLE);
                    listCategory = mngCat.deserializationListCategory(CategoryChoseActivity.this, usr.getUser());
                    listCategory.remove(categoryAdapterPosition);
                    listAccountToSet = new ArrayList<>();
                    for (final Account singleAccountTake : listAccountTake) {
                        if (mngCat.findAccount(categoryAdapterPosition.getListAcc(), singleAccountTake.getName())) {
                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupViewChose = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_equals, (ViewGroup) findViewById(R.id.popupEquals));
                            final PopupWindow popupWindowChose = new PopupWindow(popupViewChose, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                            popupWindowChose.setFocusable(false);
                            popupWindowChose.setOutsideTouchable(false);
                            popupWindowChose.setBackgroundDrawable(new BitmapDrawable());
                            View parent = layoutCategoryChoseActivity.getRootView();
                            popupWindowChose.showAtLocation(parent, Gravity.CENTER, 0, 0);
                            ((TextView) popupViewChose.findViewById(R.id.equalsText)).setText(Html.fromHtml("Esiste già un account di nome <b>" + singleAccountTake.getName() + "</b> nella categoria " + "<b>" + categoryAdapterPosition.getCat() + "</b>. Cosa vuoi fare?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                            Button overWrite = popupViewChose.findViewById(R.id.overWrite);
                            Button rename = popupViewChose.findViewById(R.id.rename);
                            Button append = popupViewChose.findViewById(R.id.append);
                            Button ignore = popupViewChose.findViewById(R.id.ignore);
                            overWrite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listAccountToSet.add(singleAccountTake);
                                    ArrayList<Account> listAccountApp = categoryAdapterPosition.getListAcc();
                                    listAccountApp.remove(mngCat.findAndGetAccount(categoryAdapterPosition.getListAcc(), singleAccountTake.getName()));
                                    categoryAdapterPosition.setListAcc(listAccountApp);
                                    popupWindowChose.dismiss();
                                }
                            });
                            rename.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View popupViewRename = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_rename, (ViewGroup) findViewById(R.id.popupRename));
                                    final PopupWindow popupWindowRename = new PopupWindow(popupViewRename, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                    popupWindowRename.setOutsideTouchable(true);
                                    popupWindowRename.setFocusable(true);
                                    popupWindowRename.setBackgroundDrawable(new BitmapDrawable());
                                    View parent = layoutCategoryChoseActivity.getRootView();
                                    popupWindowRename.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                    ((TextView) popupViewRename.findViewById(R.id.renameText)).setText(Html.fromHtml("Rinomina account " + "<b>" + singleAccountTake.getName() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                                    final EditText newAccountName = popupViewRename.findViewById(R.id.renameEditText);
                                    Button conf = popupViewRename.findViewById(R.id.confirmation);
                                    conf.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            newAccountName.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.colorAccent)));
                                            if (newAccountName.getText().toString().isEmpty()) {
                                                newAccountName.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                                return;
                                            }
                                            if (!mngCat.findAccount(mngCat.findAndGetCategory(listCategory, categoryAdapterPosition.getCat()).getListAcc(), newAccountName.getText().toString())
                                                    && !mngCat.findAccount(listAccountToSet, newAccountName.getText().toString())) {
                                                Account accountApp = new Account(singleAccountTake.getName(), singleAccountTake.getCategory(), singleAccountTake.getList());
                                                accountApp.setName(newAccountName.getText().toString());
                                                listAccountToSet.add(accountApp);
                                                popupWindowRename.dismiss();
                                                popupWindowChose.dismiss();
                                            } else {
                                                newAccountName.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                                notifyUser(Html.fromHtml("Il nome <b>" + newAccountName.getText().toString() + "</b> è già stato utilizzato nella categoria <b>" + categoryAdapterPosition.getCat() + "</b>.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                                            }
                                        }
                                    });
                                }
                            });
                            append.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<AccountElement> listAccountElementApp = new ArrayList<>();
                                    Account accountApp = mngCat.findAndGetAccount(categoryAdapterPosition.getListAcc(), singleAccountTake.getName());
                                    listAccountElementApp.addAll(accountApp.getList());
                                    listAccountElementApp.addAll(singleAccountTake.getList());
                                    accountApp.setList(listAccountElementApp);
                                    listAccountToSet.add(accountApp);
                                    ArrayList<Account> listAccountApp = categoryAdapterPosition.getListAcc();
                                    listAccountApp.remove(mngCat.findAndGetAccount(categoryAdapterPosition.getListAcc(), singleAccountTake.getName()));
                                    categoryAdapterPosition.setListAcc(listAccountApp);
                                    popupWindowChose.dismiss();
                                }
                            });
                            ignore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listAccountTakeApp.remove(singleAccountTake);
                                    popupWindowChose.dismiss();
                                }
                            });
                        } else
                            listAccountToSet.add(singleAccountTake);
                    }
                    popupViewDone.setVisibility(View.VISIBLE);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));
            addNewCategory = findViewById(R.id.addFloatingButton);
            addNewCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NestedScrollView) findViewById(R.id.catChoseNestedScroll)).setVisibility(View.GONE);
                    addNewCategory.setVisibility(View.GONE);
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupViewAddNewCategory = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_category, (ViewGroup) findViewById(R.id.categoryPopup));
                    final PopupWindow popupWindowAddNewCategory = new PopupWindow(popupViewAddNewCategory, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowAddNewCategory.setOutsideTouchable(true);
                    popupWindowAddNewCategory.setFocusable(true);
                    popupWindowAddNewCategory.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutCategoryChoseActivity.getRootView();
                    popupWindowAddNewCategory.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    if (opt.toLowerCase().equals("cut"))
                        ((TextView) popupViewAddNewCategory.findViewById(R.id.categoryText)).setText("Crea la nuova categoria su cui effettuare lo spostamento.");
                    else
                        ((TextView) popupViewAddNewCategory.findViewById(R.id.categoryText)).setText("Crea la nuova categoria su cui effettuare la copia.");
                    final EditText popupNewCategoryText = popupViewAddNewCategory.findViewById(R.id.categoryEditText);
                    Button conf = popupViewAddNewCategory.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupNewCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.colorAccent)));
                            if (checkGapError(popupNewCategoryText.getText().toString())) {
                                popupNewCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                return;
                            }
                            if ((!mngCat.findCategory(listCategory, popupNewCategoryText.getText().toString())) && (!popupNewCategoryText.getText().toString().toLowerCase().equals(category.getCat().toLowerCase()))) {
                                mngCat.removeFileCategory(CategoryChoseActivity.this, usr.getUser());
                                if (opt.toLowerCase().equals("cut")) {
                                    listCategory.remove(mngCat.findAndGetCategory(listCategory, category.getCat()));
                                    ArrayList<Account> listAccountCategoryApp = category.getListAcc();
                                    listAccountCategoryApp.removeAll(listAccountTakeApp);
                                    category.setListAcc(listAccountCategoryApp);
                                    listCategory.add(category);
                                }
                                ArrayList<Category> listCategoryApp = new ArrayList<>();
                                for (String singleStringCategory : listCategoryString) {
                                    listCategoryApp.add(mngCat.findAndGetCategory(listCategory, singleStringCategory));
                                }
                                listCategoryApp.add(new Category(fixName(popupNewCategoryText.getText().toString()), listAccountTake, 1));
                                if (usr.getSort() == 1)
                                    listCategoryApp = increasing(listCategoryApp);
                                else if (usr.getSort() == 2)
                                    listCategoryApp = decreasing(listCategoryApp);
                                mngCat.serializationListCategory(CategoryChoseActivity.this, listCategoryApp, usr.getUser());
                                goToCategoryActivity();
                                popupWindowAddNewCategory.dismiss();
                            } else {
                                popupNewCategoryText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                notifyUser(Html.fromHtml("Categoria <b>" + popupNewCategoryText.getText().toString() + "</b> già esistente.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                            }
                        }
                    });
                    popupWindowAddNewCategory.setOnDismissListener(new PopupWindow.OnDismissListener() {

                        @Override
                        public void onDismiss() {
                            ((NestedScrollView) findViewById(R.id.catChoseNestedScroll)).setVisibility(View.VISIBLE);
                            addNewCategory.setVisibility(View.VISIBLE);

                        }
                    });
                }
            });
        } else {
            notifyUser("Credenziali non rilevate. Impossibile visualizzare la lista degli account.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(CategoryChoseActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToCategoryActivity() {
        Intent intent = new Intent(CategoryChoseActivity.this, CategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
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
        return ((!word.matches("[A-Za-z0-9?!_.-]*")) || (word.isEmpty()));
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
}





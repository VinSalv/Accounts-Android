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
    private ManageCategory mngCat;
    private TextView choseText;
    private TextView choseTextMini;
    private TextView choseTextToolbar;
    private ArrayList<Category> listCategory;
    private ArrayList<Account> listAccountTake;
    private ArrayList<Account> listAccountToSet;
    private RecyclerCategoryAdapter mAdapter;
    private FloatingActionButton cancel;
    private User usr;
    private Category category;
    private Category categoryAdapter;
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
        opt = (String) (Objects.requireNonNull(getIntent().getExtras())).get("option");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) (Objects.requireNonNull(getIntent().getExtras())).get("owner")).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            categoryAdapterPosition = null;
            if (usr.getSort() == 1)
                listCategory = increasing(listCategory);
            else if (usr.getSort() == 2)
                listCategory = decreasing(listCategory);
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
                mAdapter = new RecyclerCategoryAdapter(this, listCategoryApp, usr);
            } else
                mAdapter = new RecyclerCategoryAdapter(this, listCategory, usr);
            GridLayoutManager manager = new GridLayoutManager(this, usr.getColCat(), GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration(20));
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    categoryAdapterPosition = mAdapter.getItem(position);
                    LayoutInflater layoutInflaterDone = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewDone = Objects.requireNonNull(layoutInflaterDone).inflate(R.layout.popup_done, (ViewGroup) findViewById(R.id.popupDone));
                    final PopupWindow popupWindowDone = new PopupWindow(popupViewDone, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowDone.setOutsideTouchable(false);
                    popupWindowDone.setFocusable(false);
                    //noinspection deprecation
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
                                ArrayList<Account> listAccountCategoryApp = category.getListAcc();
                                listAccountCategoryApp.removeAll(listAccountTake);
                                category.setListAcc(listAccountCategoryApp);
                                listCategory.remove(mngCat.findAndGetCategory(listCategory, category.getCat()));
                                listCategory.add(category);
                            }
                            ArrayList<Account> listAccountApp = categoryAdapter.getListAcc();
                            listAccountApp.addAll(listAccountToSet);
                            categoryAdapter.setListAcc(listAccountApp);
                            listCategory.add(categoryAdapter);
                            mngCat.serializationListCategory(CategoryChoseActivity.this, listCategory, usr.getUser());
                            goToCategoryActivity();
                            popupWindowDone.dismiss();
                        }
                    });
                    popupViewDone.setVisibility(View.INVISIBLE);
                    categoryAdapter = mAdapter.getItem(position);
                    listCategory = mngCat.deserializationListCategory(CategoryChoseActivity.this, usr.getUser());
                    listCategory.remove(categoryAdapter);
                    listAccountToSet = new ArrayList<>();
                    for (final Account accTake : listAccountTake) {
                        if (mngCat.findAccount(mAdapter.getItem(position).getListAcc(), accTake.getName())) {
                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupViewChose = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_equals, (ViewGroup) findViewById(R.id.popupEquals));
                            final PopupWindow popupWindowChose = new PopupWindow(popupViewChose, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                            popupWindowChose.setFocusable(false);
                            popupWindowChose.setOutsideTouchable(false);
                            popupWindowChose.setBackgroundDrawable(new BitmapDrawable());
                            View parent = layoutCategoryChoseActivity.getRootView();
                            popupWindowChose.showAtLocation(parent, Gravity.CENTER, 0, 0);
                            ((TextView) popupViewChose.findViewById(R.id.equalsText)).setText(Html.fromHtml("Esiste già un account di nome " + "<b>" + accTake.getName() + "</b>" + " nella categoria " + "<b>" + categoryAdapterPosition.getCat() + "</b>" + ". Cosa vuoi fare?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                            Button overWrite = popupViewChose.findViewById(R.id.overWrite);
                            Button rename = popupViewChose.findViewById(R.id.rename);
                            Button append = popupViewChose.findViewById(R.id.append);
                            Button ignore = popupViewChose.findViewById(R.id.ignore);
                            overWrite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listAccountToSet.add(accTake);
                                    ArrayList<Account> listAccountApp = categoryAdapter.getListAcc();
                                    listAccountApp.remove(mngCat.findAndGetAccount(mAdapter.getItem(position).getListAcc(), accTake.getName()));
                                    categoryAdapter.setListAcc(listAccountApp);
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
                                    //noinspection deprecation
                                    popupWindowRename.setBackgroundDrawable(new BitmapDrawable());
                                    View parent = layoutCategoryChoseActivity.getRootView();
                                    popupWindowRename.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                    ((TextView) popupViewRename.findViewById(R.id.renameText)).setText(Html.fromHtml("Rinomina account " + "<b>" + accTake.getName() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                                    final EditText newAccountName = popupViewRename.findViewById(R.id.renameEditText);
                                    Button conf = popupViewRename.findViewById(R.id.confirmation);
                                    conf.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            newAccountName.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.colorAccent)));
                                            if (notFieldCheck(newAccountName.getText().toString())) {
                                                newAccountName.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                                return;
                                            }
                                            if (!mngCat.findAccount(mngCat.findAndGetCategory(listCategory, mAdapter.getItem(position).getCat()).getListAcc(), newAccountName.getText().toString())
                                                    && !mngCat.findAccount(listAccountToSet, newAccountName.getText().toString())) {
                                                accTake.setName(newAccountName.getText().toString());
                                                listAccountToSet.add(accTake);
                                                popupWindowRename.dismiss();
                                                popupWindowChose.dismiss();

                                            } else {
                                                newAccountName.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                                notifyUser(Html.fromHtml("Il nome <b>" + newAccountName.getText().toString() + "</b> è già stato utilizzato nella categoria <b>" + categoryAdapter.getCat() + "</b>.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                                            }
                                        }
                                    });
                                }
                            });
                            append.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<AccountElement> listAccountElementApp = new ArrayList<>();
                                    Account accountApp = mngCat.findAndGetAccount(mAdapter.getItem(position).getListAcc(), accTake.getName());
                                    listAccountElementApp.addAll(accountApp.getList());
                                    listAccountElementApp.addAll(accTake.getList());
                                    accountApp.setList(listAccountElementApp);
                                    listAccountToSet.add(accountApp);
                                    ArrayList<Account> listAccountApp = categoryAdapter.getListAcc();
                                    listAccountApp.remove(mngCat.findAndGetAccount(mAdapter.getItem(position).getListAcc(), accTake.getName()));
                                    categoryAdapter.setListAcc(listAccountApp);
                                    popupWindowChose.dismiss();
                                }
                            });
                            ignore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listAccountTake.remove(accTake);
                                    popupWindowChose.dismiss();
                                }
                            });
                        } else
                            listAccountToSet.add(accTake);
                    }
                    popupViewDone.setVisibility(View.VISIBLE);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));
            cancel = findViewById(R.id.cancelFloatingButton);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToCategoryActivity();
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

    public boolean notFieldCheck(String s) {
        if (isInvalidWord(s)) {
            notifyUser("Campo non valido.");
            return true;
        }
        return false;
    }

    public boolean isInvalidWord(String word) {
        return ((!word.matches("[A-Za-z0-9?!_.-]*")) || (word.isEmpty()));
    }
}





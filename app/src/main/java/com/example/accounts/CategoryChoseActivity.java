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
    private CoordinatorLayout cl;
    private ArrayList<Category> listCategory;
    private ManageCategory mngCat;
    private User usr;
    private TextView wellcome;
    private TextView wellcomeMini;
    private TextView wellcome2;
    private RecyclerCategoryAdapter mAdapter;
    private String opt;
    private ArrayList<Account> listAccTake;
    private ArrayList<Account> listAccSet;
    private Category catAdapter;
    private Category catAdapterPosition;
    private Category cat;
    private FloatingActionButton cancel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chose);
        final Toolbar toolbar = findViewById(R.id.catChoseToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        listAccTake = (ArrayList<Account>) (getIntent().getExtras()).get("listAccount");
        opt = (String) (Objects.requireNonNull(getIntent().getExtras())).get("option");
        cl = findViewById(R.id.catChoseActivityLay);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            cat = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            catAdapterPosition = null;
            if (usr.getSort() == 1)
                listCategory = increasing(listCategory);
            else if (usr.getSort() == 2)
                listCategory = decreasing(listCategory);
            wellcome = findViewById(R.id.wellcomeChoseText);
            wellcome.setText("Scegli la categoria");
            wellcomeMini = findViewById(R.id.wellcomeChoseMiniText);
            if (opt.toLowerCase().equals("cut")) {
                wellcomeMini.setText("Numero categorie: " + (listCategory.size() - 1));
            } else
                wellcomeMini.setText("Numero categorie: " + listCategory.size());
            wellcome2 = findViewById(R.id.wellcomeChoseTextToolbar);
            wellcome2.setText("Scegli la categoria");
            wellcome2.setVisibility(View.INVISIBLE);
            AppBarLayout appBar = findViewById(R.id.catChoseBarToolbar);
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
                    } else if (isShow) {
                        isShow = false;
                        wellcome2.setVisibility(View.INVISIBLE);
                    }
                }
            });
            if (opt.toLowerCase().equals("cut")) {
                ArrayList<Category> listCat = listCategory;
                listCat.remove(cat);
                mAdapter = new RecyclerCategoryAdapter(this, listCat, usr);
            } else
                mAdapter = new RecyclerCategoryAdapter(this, listCategory, usr);
            GridLayoutManager manager = new GridLayoutManager(this, usr.getColCat(), GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration(20));
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    catAdapterPosition = mAdapter.getItem(position);
                    LayoutInflater layoutInflaterDone = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewDone = Objects.requireNonNull(layoutInflaterDone).inflate(R.layout.popup_done, (ViewGroup) findViewById(R.id.popupDone));
                    final PopupWindow popupWindowDone = new PopupWindow(popupViewDone, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowDone.setOutsideTouchable(false);
                    popupWindowDone.setFocusable(false);
                    //noinspection deprecation
                    popupWindowDone.setBackgroundDrawable(new BitmapDrawable());
                    View parentDone = cl.getRootView();
                    popupWindowDone.showAtLocation(parentDone, Gravity.CENTER, 0, 0);
                    Button d = popupViewDone.findViewById(R.id.doneButton);
                    d.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listCategory.remove(mngCat.findAndGetCategory(listCategory, catAdapterPosition.getCat()));
                            mngCat.removeFileCategory(CategoryChoseActivity.this, usr.getUser());
                            if (opt.toLowerCase().equals("cut")) {
                                ArrayList<Account> listAccCatApp = cat.getListAcc();
                                listAccCatApp.removeAll(listAccTake);
                                cat.setListAcc(listAccCatApp);
                                listCategory.remove(mngCat.findAndGetCategory(listCategory, cat.getCat()));
                                listCategory.add(cat);
                            }
                            ArrayList<Account> listAccApp = catAdapter.getListAcc();
                            listAccApp.addAll(listAccSet);
                            catAdapter.setListAcc(listAccApp);
                            listCategory.add(catAdapter);
                            mngCat.serializationListCategory(CategoryChoseActivity.this, listCategory, usr.getUser());
                            goToCategoryActivity();
                            popupWindowDone.dismiss();
                        }
                    });
                    popupViewDone.setVisibility(View.INVISIBLE);
                    catAdapter = mAdapter.getItem(position);
                    listCategory = mngCat.deserializationListCategory(CategoryChoseActivity.this, usr.getUser());
                    mngCat.removeFileCategory(CategoryChoseActivity.this, usr.getUser());
                    listCategory.remove(catAdapter);
                    listAccSet = new ArrayList<>();
                    for (final Account accTake : listAccTake) {
                        if (mngCat.findAccount(mAdapter.getItem(position).getListAcc(), accTake.getName())) {
                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            final View popupViewChose = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_equals, (ViewGroup) findViewById(R.id.popupEquals));
                            final PopupWindow popupWindowChose = new PopupWindow(popupViewChose, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                            popupWindowChose.setFocusable(false);
                            popupWindowChose.setOutsideTouchable(false);
                            //noinspection deprecation
                            popupWindowChose.setBackgroundDrawable(new BitmapDrawable());
                            View parent = cl.getRootView();
                            popupWindowChose.showAtLocation(parent, Gravity.CENTER, 0, 0);
                            String s = "Esiste gia un account di nome " + "<b>" + accTake.getName() + "</b>" + " nella categoria " + "<b>" + catAdapterPosition.getCat() + "</b>" + ". Cosa vuoi fare?";
                            ((TextView) popupViewChose.findViewById(R.id.equalsText)).setText(Html.fromHtml(s, HtmlCompat.FROM_HTML_MODE_LEGACY));
                            Button oW = popupViewChose.findViewById(R.id.overWrite);
                            Button r = popupViewChose.findViewById(R.id.rename);
                            Button a = popupViewChose.findViewById(R.id.append);
                            Button i = popupViewChose.findViewById(R.id.ignore);
                            oW.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listAccSet.add(accTake);
                                    ArrayList<Account> listAccApp = catAdapter.getListAcc();
                                    listAccApp.remove(mngCat.findAndGetAccount(mAdapter.getItem(position).getListAcc(), accTake.getName()));
                                    catAdapter.setListAcc(listAccApp);
                                    popupWindowChose.dismiss();
                                }
                            });
                            r.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_rename, (ViewGroup) findViewById(R.id.popupRename));
                                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                    popupWindow.setOutsideTouchable(true);
                                    popupWindow.setFocusable(true);
                                    //noinspection deprecation
                                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                                    View parent = cl.getRootView();
                                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                    String s = "Rinomina account " + "<b>" + accTake.getName() + "</b>";
                                    ((TextView) popupView.findViewById(R.id.renameText)).setText(Html.fromHtml(s, HtmlCompat.FROM_HTML_MODE_LEGACY));
                                    final EditText et = popupView.findViewById(R.id.renameEditText);
                                    Button conf = popupView.findViewById(R.id.confirmation);
                                    conf.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.colorAccent)));
                                            if (notFieldCheck(et.getText().toString())) {
                                                et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                                return;
                                            }
                                            if (!mngCat.findAccount(mngCat.findAndGetCategory(listCategory, mAdapter.getItem(position).getCat()).getListAcc(), et.getText().toString())
                                                    && !mngCat.findAccount(listAccSet, et.getText().toString())) {
                                                accTake.setName(et.getText().toString());
                                                listAccSet.add(accTake);
                                                popupWindow.dismiss();
                                                popupWindowChose.dismiss();

                                            } else {
                                                et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CategoryChoseActivity.this, R.color.errorEditText)));
                                                notifyUser("Account con questo nome gia esistente nella categoria " + catAdapter.getCat());
                                            }
                                        }
                                    });
                                }
                            });
                            a.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<AccountElement> listAccElem = new ArrayList<>();
                                    Account acc = mngCat.findAndGetAccount(mAdapter.getItem(position).getListAcc(), accTake.getName());
                                    listAccElem.addAll(acc.getList());
                                    listAccElem.addAll(accTake.getList());
                                    acc.setList(listAccElem);
                                    listAccSet.add(acc);
                                    ArrayList<Account> listAccApp = catAdapter.getListAcc();
                                    listAccApp.remove(mngCat.findAndGetAccount(mAdapter.getItem(position).getListAcc(), accTake.getName()));
                                    catAdapter.setListAcc(listAccApp);
                                    popupWindowChose.dismiss();
                                }
                            });

                            i.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listAccTake.remove(accTake);
                                    popupWindowChose.dismiss();
                                }
                            });
                        } else
                            listAccSet.add(accTake);
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
            notifyUser("Utente non rilevato. Impossibile visualizzare la lista degli account.");
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
            notifyUser("Campo non valido !!!");
            return true;
        }
        return false;
    }

    public boolean isInvalidWord(String word) {
        return ((!word.matches("[A-Za-z0-9?!_.-]*")) || (word.isEmpty()));
    }

    @Override
    public void onBackPressed() {
    }
}





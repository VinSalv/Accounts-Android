package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class CategoryChoseActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce;
    private ManageUser mngUsr;
    private CoordinatorLayout cl;
    private ArrayList<User> listUser;
    private ArrayList<Category> listCategory;
    private ManageCategory mngCat;
    private User usr;
    private TextView wellcome;
    private TextView wellcomeMini;
    private TextView wellcome2;
    private RecyclerCategoryAdapter mAdapter;
    private Category cat;
    private String opt;
    private ArrayList<Account> acc;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chose);
        final Toolbar toolbar = findViewById(R.id.catChoseToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        acc = (ArrayList<Account>) Objects.requireNonNull(getIntent().getExtras()).get("listAccount");
        opt = (String) (Objects.requireNonNull(getIntent().getExtras())).get("option");
        cl = findViewById(R.id.catChoseActivityLay);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        doubleBackToExitPressedOnce = false;
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            cat = mngCat.findAndGetCategory(listCategory, Objects.requireNonNull(cat).getCat());
            if (opt.toLowerCase().equals(("cut").toLowerCase())) {
                listCategory.remove(cat);
            }
            if (usr.getSort() == 1)
                mngCat.serializationListCategory(this, increasing(listCategory), usr.getUser());
            else if (usr.getSort() == 2)
                mngCat.serializationListCategory(this, decreasing(listCategory), usr.getUser());
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            wellcome = findViewById(R.id.wellcomeChoseText);
            wellcome.setText("Scegli la categoria");
            wellcomeMini = findViewById(R.id.wellcomeChoseMiniText);
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
            mAdapter = new RecyclerCategoryAdapter(this, listCategory, usr);
            GridLayoutManager manager = new GridLayoutManager(this, usr.getColCat(), GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration(20, usr.getColCat()));
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    if (opt.toLowerCase().equals(("cut").toLowerCase())) {

                        ArrayList<Account> listAcc = mngCat.findAndGetCategory(listCategory, cat.getCat()).getListAcc();
                        for (Account a : acc) {
                            listAcc.remove(a);
                        }
                        listCategory.remove(cat);
                        cat.setListAcc(listAcc);
                        listCategory.add(cat);
                        mngCat.serializationListCategory(CategoryChoseActivity.this, listCategory, usr.getUser());

                        listAcc = mAdapter.getItem(position).getListAcc();
                        for (Account a : acc) {
                            for (Account ac : listAcc)
                                if (a.getName().toLowerCase().equals(ac.getName().toLowerCase())) ;


                        }
                    } else if (opt.toLowerCase().equals(("copy").toLowerCase())) {

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

    public void refresh() {
        startActivity(getIntent());
        finish();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(CategoryChoseActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goViewActivity(User usr, Category cat) {
        Intent intent = new Intent(CategoryChoseActivity.this, ViewActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("category", cat);
        startActivity(intent);
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


}



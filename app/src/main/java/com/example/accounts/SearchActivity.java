package com.example.accounts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public class SearchActivity extends AppCompatActivity {
    private LinearLayout layoutSearchActivity;
    private ManageCategory mngCat;
    private User usr;
    private ArrayList<Category> listCategory;
    private TextView accountFound;
    private int i;
    private int j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        layoutSearchActivity = findViewById(R.id.contentSearchLayout);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            accountFound = findViewById(R.id.elemFind);
            EditText name = findViewById(R.id.nameSerched);
            name.addTextChangedListener(new TextWatcher() {
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {
                    layoutSearchActivity.removeAllViews();
                    i = 0;
                    if (!s.toString().equals("")) {
                        for (final Category singleCategory : listCategory) {
                            LayoutInflater layoutInflater = LayoutInflater.from(SearchActivity.this);
                            final View viewLayoutCategory = layoutInflater.inflate(R.layout.list_search, (ViewGroup) findViewById(R.id.searchLay), false);
                            LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lpp.setMargins(0, 0, 0, 20);
                            TextView categoryFound = viewLayoutCategory.findViewById(R.id.categorySearch);
                            categoryFound.setText(singleCategory.getCat());
                            TextView numberAccountCategoryFound = viewLayoutCategory.findViewById(R.id.findSearchCategory);
                            layoutSearchActivity.addView(viewLayoutCategory, lpp);
                            j = 0;
                            for (final Account singleAccount : increasing(singleCategory.getListAcc())) {
                                if (singleAccount.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                    i++;
                                    j++;
                                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    param.setMargins(20, 10, 20, 10);
                                    Button accountFound = new Button(SearchActivity.this);
                                    accountFound.setText(singleAccount.getName());
                                    accountFound.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    accountFound.setBackground(getDrawable(R.drawable.rounded_color));
                                    ((LinearLayout) viewLayoutCategory.findViewById(R.id.elmSearchLay)).addView(accountFound, param);
                                    accountFound.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mngCat = new ManageCategory();
                                            goToShowElementActivity(singleAccount, singleCategory);
                                        }
                                    });
                                }
                            }
                            if (j == 0) layoutSearchActivity.removeView(viewLayoutCategory);
                            else
                                numberAccountCategoryFound.setText(Html.fromHtml("Trovati: <b>" + j + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                            (viewLayoutCategory.findViewById(R.id.elmSearchLay)).setVisibility(View.VISIBLE);
                            final ImageButton show = viewLayoutCategory.findViewById(R.id.showCategory);
                            final Boolean[] bool = {true};
                            show.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bool[0]) {
                                        bool[0] = false;
                                        show.setImageResource(android.R.drawable.arrow_up_float);
                                        (viewLayoutCategory.findViewById(R.id.elmSearchLay)).animate()
                                                .alpha(0.0f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                        super.onAnimationStart(animation);
                                                        (viewLayoutCategory.findViewById(R.id.elmSearchLay)).setVisibility(View.GONE);
                                                    }
                                                });
                                    } else {
                                        bool[0] = true;
                                        show.setImageResource(android.R.drawable.arrow_down_float);
                                        (viewLayoutCategory.findViewById(R.id.elmSearchLay)).animate()
                                                .alpha(1.0f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                        super.onAnimationStart(animation);
                                                        (viewLayoutCategory.findViewById(R.id.elmSearchLay)).setVisibility(View.VISIBLE);
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    }
                    accountFound.setText(Html.fromHtml("Numero account totali trovati: <b>" + i + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

        } else {
            notifyUser("Credenziati non rilevate. Impossibile visualizzare la lista degli account.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToShowElementActivity(Account account, Category category) {
        Intent intent = new Intent(SearchActivity.this, ShowElementActivity.class);
        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", account.getName() + category.getCat());
        intent.putExtra("account", account);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
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
}

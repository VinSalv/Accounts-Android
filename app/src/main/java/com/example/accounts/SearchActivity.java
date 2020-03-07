package com.example.accounts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public class SearchActivity extends AppCompatActivity {
    private User usr;
    private LinearLayout lay;
    private TextView elemFind;
    private int i;
    private int j;
    private ManageCategory mngCat;
    private ArrayList<Category> listCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        lay = findViewById(R.id.contentSearchLayout);
        EditText name = findViewById(R.id.nameSerched);
        elemFind = findViewById(R.id.elemFind);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            name.addTextChangedListener(new TextWatcher() {
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {
                    lay.removeAllViews();
                    i = 0;
                    if (!s.toString().equals("")) {
                        for (Category c : listCategory) {
                            LayoutInflater layoutInflater = LayoutInflater.from(SearchActivity.this);
                            final View view = layoutInflater.inflate(R.layout.list_search, (ViewGroup) findViewById(R.id.searchLay), false);
                            LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lpp.setMargins(0, 0, 0, 20);
                            TextView tv = view.findViewById(R.id.categorySearch);
                            tv.setText(c.getCat());
                            TextView tv2 = view.findViewById(R.id.findSearchCategory);
                            (view.findViewById(R.id.elmSearchLay)).setVisibility(View.VISIBLE);
                            lay.addView(view, lpp);
                            j = 0;
                            for (final Account a : increasing(c.getListAcc())) {
                                if (a.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                    i++;
                                    j++;
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(20, 10, 20, 10);
                                    Button myButton = new Button(SearchActivity.this);
                                    myButton.setText(a.getName());
                                    myButton.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    myButton.setBackground(getDrawable(R.drawable.rounded_color));
                                    LinearLayout ll = view.findViewById(R.id.elmSearchLay);
                                    ll.addView(myButton, lp);
                                    myButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mngCat = new ManageCategory();
                                            Category cat = mngCat.findAndGetCategory(mngCat.deserializationListCategory(SearchActivity.this, usr.getUser()), a.getCategory());
                                            goToShowElementActivity(usr, a, cat);
                                        }
                                    });
                                }
                            }
                            if (j == 0) lay.removeView(view);
                            else tv2.setText("Trovati: " + j);
                            final ImageButton show = view.findViewById(R.id.showCategory);
                            final Boolean[] bool = {true};
                            show.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bool[0]) {
                                        bool[0] = false;
                                        show.setImageResource(android.R.drawable.arrow_up_float);
                                        (view.findViewById(R.id.elmSearchLay)).animate()
                                                .alpha(0.0f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                        super.onAnimationStart(animation);
                                                        (view.findViewById(R.id.elmSearchLay)).setVisibility(View.GONE);
                                                    }
                                                });
                                    } else {
                                        bool[0] = true;
                                        show.setImageResource(android.R.drawable.arrow_down_float);
                                        (view.findViewById(R.id.elmSearchLay)).animate()
                                                .alpha(1.0f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                        super.onAnimationStart(animation);
                                                        (view.findViewById(R.id.elmSearchLay)).setVisibility(View.VISIBLE);
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    }
                    elemFind.setText("Numero account totali trovati: " + i);

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

        } else {
            notifyUser("Utente non rilevato. Impossibile visualizzare la lista degli account.");
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

    public void goToShowElementActivity(User usr, Account acc, Category cat) {
        Intent intent = new Intent(SearchActivity.this, ShowElementActivity.class);
        intent.putExtra("account", acc);
        intent.putExtra("owner", usr);
        intent.putExtra("category", cat);
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

package com.example.accounts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class ShowElementActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private CoordinatorLayout layoutShowElementActivity;
    private ManageCategory mngCat;
    private ArrayList<Account> listAccount;
    private ArrayList<Category> listCategory;
    private User usr;
    private Category category;
    private Account account;
    private TextView name;
    private TextView nameToolbar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_element);
        Toolbar showToolbar = findViewById(R.id.showToolbar);
        showToolbar.setTitle("");
        setSupportActionBar(showToolbar);
        layoutShowElementActivity = findViewById(R.id.coordinatorLayShow);
        LinearLayout layoutShowElement = findViewById(R.id.linearLayoutShowElements);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUsr = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUsr, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            listAccount = category.getListAcc();
            account = mngCat.findAndGetAccount(listAccount, ((Account) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("account"))).getName());
            if (account != null) {
                name = findViewById(R.id.name);
                name.setText(account.getName());
                nameToolbar = findViewById(R.id.nameToolbar);
                nameToolbar.setText(account.getName());
                nameToolbar.setVisibility(View.INVISIBLE);
                Button optionButton = findViewById(R.id.optionsButton);
                AppBarLayout appBar = findViewById(R.id.app_bar_show);
                appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = false;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        name.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            isShow = true;
                            nameToolbar.setVisibility(View.VISIBLE);
                        } else if (isShow) {
                            isShow = false;
                            nameToolbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                int i = 0;
                for (final AccountElement singleAccountElement : account.getList()) {
                    i++;
                    final String nameText = i + "." + account.getName();
                    LayoutInflater inflater = LayoutInflater.from(ShowElementActivity.this);
                    @SuppressLint("InflateParams") final RelativeLayout layoutToAdd = (RelativeLayout) inflater.inflate(R.layout.fragment_show_element, null);
                    final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 15);
                    layoutToAdd.setLayoutParams(params);
                    ((TextView) layoutToAdd.findViewById(R.id.cardinalityElements)).setText(nameText);
                    (layoutToAdd.findViewById(R.id.emailShowImage)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.emailShowText)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.userShowImage)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.userShowText)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.passShowImage)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.passShowText)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.showPass)).setVisibility(View.GONE);
                    ((TextView) layoutToAdd.findViewById(R.id.descriptionShowText)).setText(singleAccountElement.getDescription());
                    layoutShowElement.addView(layoutToAdd);
                    final ImageButton showLayout = layoutToAdd.findViewById(R.id.showButton);
                    final Boolean[] bool = {true};
                    showLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bool[0]) {
                                bool[0] = false;
                                showLayout.setImageResource(android.R.drawable.arrow_down_float);
                                if (!singleAccountElement.getEmail().isEmpty()) {
                                    ((TextView) layoutToAdd.findViewById(R.id.emailShowText)).setText(singleAccountElement.getEmail());
                                    (layoutToAdd.findViewById(R.id.emailShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.emailShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.emailShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.emailShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!singleAccountElement.getUser().isEmpty()) {
                                    ((TextView) layoutToAdd.findViewById(R.id.userShowText)).setText(singleAccountElement.getUser());
                                    (layoutToAdd.findViewById(R.id.userShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.userShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.userShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.userShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!singleAccountElement.getPassword().isEmpty()) {
                                    ((TextView) layoutToAdd.findViewById(R.id.passShowText)).setText(singleAccountElement.getPassword());
                                    (layoutToAdd.findViewById(R.id.passShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.passShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.showPass)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.showPass)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.passShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.passShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            } else {
                                bool[0] = true;
                                showLayout.setImageResource(android.R.drawable.arrow_up_float);
                                (layoutToAdd.findViewById(R.id.emailShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.emailShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.emailShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.emailShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.userShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.userShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.userShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.userShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.passShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.passShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.passShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.passShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.showPass)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.showPass)).setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    });
                    (layoutToAdd.findViewById(R.id.cardinalityElements)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bool[0]) {
                                bool[0] = false;
                                showLayout.setImageResource(android.R.drawable.arrow_down_float);
                                if (!singleAccountElement.getEmail().isEmpty()) {
                                    ((TextView) layoutToAdd.findViewById(R.id.emailShowText)).setText(singleAccountElement.getEmail());
                                    (layoutToAdd.findViewById(R.id.emailShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.emailShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.emailShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.emailShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!singleAccountElement.getUser().isEmpty()) {
                                    ((TextView) layoutToAdd.findViewById(R.id.userShowText)).setText(singleAccountElement.getUser());
                                    (layoutToAdd.findViewById(R.id.userShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.userShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.userShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.userShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!singleAccountElement.getPassword().isEmpty()) {
                                    ((TextView) layoutToAdd.findViewById(R.id.passShowText)).setText(singleAccountElement.getPassword());
                                    (layoutToAdd.findViewById(R.id.passShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.passShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.showPass)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.showPass)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (layoutToAdd.findViewById(R.id.passShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (layoutToAdd.findViewById(R.id.passShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            } else {
                                bool[0] = true;
                                showLayout.setImageResource(android.R.drawable.arrow_up_float);
                                (layoutToAdd.findViewById(R.id.emailShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.emailShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.emailShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.emailShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.userShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.userShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.userShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.userShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.passShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.passShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.passShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.passShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (layoutToAdd.findViewById(R.id.showPass)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (layoutToAdd.findViewById(R.id.showPass)).setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    });
                    ImageButton edit = layoutToAdd.findViewById(R.id.editButton);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToEditActivity(account, singleAccountElement, category);
                        }
                    });
                    ImageButton delete = layoutToAdd.findViewById(R.id.deleteButton);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupViewSecurity = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                            final PopupWindow popupWindowSecurity = new PopupWindow(popupViewSecurity, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                            popupWindowSecurity.setOutsideTouchable(true);
                            popupWindowSecurity.setFocusable(true);
                            popupWindowSecurity.setBackgroundDrawable(new BitmapDrawable());
                            View parent = layoutShowElementActivity.getRootView();
                            popupWindowSecurity.showAtLocation(parent, Gravity.CENTER, 0, 0);
                            TextView popupText = popupViewSecurity.findViewById(R.id.securityText);
                            popupText.setText(Html.fromHtml("Sei sicuro di voler eliminare <b>" + nameText + "</b> dai tuoi account?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                            Button yes = popupViewSecurity.findViewById(R.id.yes);
                            Button no = popupViewSecurity.findViewById(R.id.no);
                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    account.getList().remove(singleAccountElement);
                                    listAccount.remove(account);
                                    listAccount.add(account);
                                    listCategory.remove(category);
                                    category.setListAcc(listAccount);
                                    listCategory.add(category);
                                    mngCat.serializationListCategory(ShowElementActivity.this, listCategory, usr.getUser());
                                    refresh();
                                    popupWindowSecurity.dismiss();
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindowSecurity.dismiss();
                                }
                            });
                        }
                    });
                    showPass((TextView) layoutToAdd.findViewById(R.id.passShowText), (ImageButton) layoutToAdd.findViewById((R.id.showPass)));
                }
                optionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMenu(R.style.rounded_menu_style_toolbar, R.menu.popup_single_account, v);
                    }
                });
            } else {
                notifyUser("Account non rilevato. Impossibile mostrare i dati.");
                goToViewActivity(category);
            }
        } else {
            notifyUser("Credenziali non rilevate. Impossibile mostrare l'account.");
            goToMainActivity();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                goToEditActivity(account, category);
                return true;
            case R.id.delete:
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewDeleteAccount = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                final PopupWindow popupWindowDeleteAccount = new PopupWindow(popupViewDeleteAccount, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowDeleteAccount.setOutsideTouchable(true);
                popupWindowDeleteAccount.setFocusable(true);
                //noinspection deprecation
                popupWindowDeleteAccount.setBackgroundDrawable(new BitmapDrawable());
                View parent = layoutShowElementActivity.getRootView();
                popupWindowDeleteAccount.showAtLocation(parent, Gravity.CENTER, 0, 0);
                TextView et = popupViewDeleteAccount.findViewById(R.id.securityText);
                et.setText(Html.fromHtml("Sei sicuro di voler eliminare <b>" + account.getName() + "</b> dai tuoi account?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                Button yes = popupViewDeleteAccount.findViewById(R.id.yes);
                Button no = popupViewDeleteAccount.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listAccount.remove(account);
                        listCategory.remove(category);
                        category.setListAcc(listAccount);
                        listCategory.add(category);
                        mngCat.serializationListCategory(ShowElementActivity.this, listCategory, usr.getUser());
                        goToViewActivity(category);
                        popupWindowDeleteAccount.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowDeleteAccount.dismiss();
                    }
                });
                return true;
            case R.id.copy:
                goToCategoryChoseActivity("copy");
                return true;
            case R.id.cut:
                if (listCategory.size() <= 1)
                    notifyUser("Non ci sono categorie su cui effetturare lo spostamento.");
                else
                    goToCategoryChoseActivity("cut");
                return true;
            default:
                return false;
        }
    }

    public void refresh() {
        finish();
        startActivity(getIntent());
    }

    public void goToMainActivity() {
        Intent intent = new Intent(ShowElementActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity(Category category) {
        Intent intent = new Intent(ShowElementActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    public void goToEditActivity(Account account, AccountElement accountElement, Category category) {
        Intent intent = new Intent(ShowElementActivity.this, EditActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("account", account);
        intent.putExtra("accountElement", accountElement);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void goToEditActivity(Account account, Category category) {
        Intent intent = new Intent(ShowElementActivity.this, EditActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("account", account);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void goToCategoryChoseActivity(String opt) {
        Intent intent = new Intent(ShowElementActivity.this, CategoryChoseActivity.class);
        ArrayList<Account> listAcc = new ArrayList<>();
        listAcc.add(account);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        intent.putExtra("listAccount", listAcc);
        intent.putExtra("option", opt);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public void popupMenu(int style, int menu, View v) {
        PopupMenu popup = new PopupMenu(ShowElementActivity.this, v, Gravity.END, 0, style);
        popup.setOnMenuItemClickListener(ShowElementActivity.this);
        popup.inflate(menu);
        popup.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showPass(final TextView tv, ImageButton showPass) {
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.rightText));
                        break;
                    case MotionEvent.ACTION_UP:
                        tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.transparent));
                        break;
                }
                return true;
            }
        });
    }
}
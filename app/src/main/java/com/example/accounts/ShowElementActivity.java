package com.example.accounts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Objects;

public class ShowElementActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private CoordinatorLayout cl;
    private Account account;
    private ArrayList<Account> listAccount;
    private Account acc;
    private TextView name;
    private TextView name2;
    private User usr;
    private ManageCategory mngCat;
    private Category cat;
    private ArrayList<Category> listCategory;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_element);
        Toolbar showToolbar = findViewById(R.id.showToolbar);
        showToolbar.setTitle("");
        setSupportActionBar(showToolbar);
        cl = findViewById(R.id.coordinatorLayShow);
        LinearLayout ll = findViewById(R.id.linearLayoutShowElements);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        account = (Account) Objects.requireNonNull(getIntent().getExtras()).get("account");
        Category category = (Category) (Objects.requireNonNull(getIntent().getExtras())).get("category");
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUsr = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUsr, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            cat = mngCat.findAndGetCategory(listCategory, Objects.requireNonNull(category).getCat());
            listAccount = cat.getListAcc();
            acc = mngCat.findAccount(listAccount, account.getName());
            if (acc != null) {
                name = findViewById(R.id.name);
                name.setText(acc.getName());
                name2 = findViewById(R.id.nameToolbar);
                name2.setText(acc.getName());
                name2.setVisibility(View.INVISIBLE);
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
                            name2.setVisibility(View.VISIBLE);
                        } else if (isShow) {
                            isShow = false;
                            name2.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                int i = 0;
                for (final AccountElement ae : acc.getList()) {
                    i++;
                    final String nameText = i + "." + acc.getName();
                    LayoutInflater inflater = LayoutInflater.from(ShowElementActivity.this);
                    @SuppressLint("InflateParams") final RelativeLayout relLey = (RelativeLayout) inflater.inflate(R.layout.fragment_show_element, null);
                    final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 15);
                    relLey.setLayoutParams(params);
                    ((TextView) relLey.findViewById(R.id.cardinalityElements)).setText(nameText);
                    (relLey.findViewById(R.id.emailShowImage)).setVisibility(View.GONE);
                    (relLey.findViewById(R.id.emailShowText)).setVisibility(View.GONE);
                    (relLey.findViewById(R.id.userShowImage)).setVisibility(View.GONE);
                    (relLey.findViewById(R.id.userShowText)).setVisibility(View.GONE);
                    (relLey.findViewById(R.id.passShowImage)).setVisibility(View.GONE);
                    (relLey.findViewById(R.id.passShowText)).setVisibility(View.GONE);
                    (relLey.findViewById(R.id.showPass)).setVisibility(View.GONE);
                    ((TextView) relLey.findViewById(R.id.descriptionShowText)).setText(ae.getDescription());
                    ll.addView(relLey);
                    final ImageButton show = relLey.findViewById(R.id.showButton);
                    final Boolean[] bool = {true};
                    show.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bool[0]) {
                                bool[0] = false;
                                show.setImageResource(android.R.drawable.arrow_down_float);
                                if (!ae.getEmail().isEmpty()) {
                                    ((TextView) relLey.findViewById(R.id.emailShowText)).setText(ae.getEmail());
                                    (relLey.findViewById(R.id.emailShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.emailShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.emailShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.emailShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!ae.getUser().isEmpty()) {
                                    ((TextView) relLey.findViewById(R.id.userShowText)).setText(ae.getUser());
                                    (relLey.findViewById(R.id.userShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.userShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.userShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.userShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!ae.getPassword().isEmpty()) {
                                    ((TextView) relLey.findViewById(R.id.passShowText)).setText(ae.getPassword());
                                    (relLey.findViewById(R.id.passShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.passShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.showPass)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.showPass)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.passShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.passShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            } else {
                                bool[0] = true;
                                show.setImageResource(android.R.drawable.arrow_up_float);
                                (relLey.findViewById(R.id.emailShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.emailShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.emailShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.emailShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.userShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.userShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.userShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.userShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.passShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.passShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.passShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.passShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.showPass)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.showPass)).setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    });
                    (relLey.findViewById(R.id.cardinalityElements)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bool[0]) {
                                bool[0] = false;
                                show.setImageResource(android.R.drawable.arrow_down_float);
                                if (!ae.getEmail().isEmpty()) {
                                    ((TextView) relLey.findViewById(R.id.emailShowText)).setText(ae.getEmail());
                                    (relLey.findViewById(R.id.emailShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.emailShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.emailShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.emailShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!ae.getUser().isEmpty()) {
                                    ((TextView) relLey.findViewById(R.id.userShowText)).setText(ae.getUser());
                                    (relLey.findViewById(R.id.userShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.userShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.userShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.userShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                if (!ae.getPassword().isEmpty()) {
                                    ((TextView) relLey.findViewById(R.id.passShowText)).setText(ae.getPassword());
                                    (relLey.findViewById(R.id.passShowText)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.passShowText)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.showPass)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.showPass)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    (relLey.findViewById(R.id.passShowImage)).animate()
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    super.onAnimationStart(animation);
                                                    (relLey.findViewById(R.id.passShowImage)).setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            } else {
                                bool[0] = true;
                                show.setImageResource(android.R.drawable.arrow_up_float);
                                (relLey.findViewById(R.id.emailShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.emailShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.emailShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.emailShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.userShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.userShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.userShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.userShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.passShowImage)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.passShowImage)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.passShowText)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.passShowText)).setVisibility(View.GONE);
                                            }
                                        });
                                (relLey.findViewById(R.id.showPass)).animate()
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                super.onAnimationStart(animation);
                                                (relLey.findViewById(R.id.showPass)).setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    });
                    ImageButton edit = relLey.findViewById(R.id.editButton);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToEditActivity(usr, acc, ae);
                        }
                    });
                    ImageButton del = relLey.findViewById(R.id.deleteButton);
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                            et.setText("Sei sicuro di voler eliminare " + nameText + " dai tuoi account?");
                            Button yes = popupView.findViewById(R.id.yes);
                            Button no = popupView.findViewById(R.id.no);
                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    acc.getList().remove(ae);
                                    listAccount.remove(account);
                                    listAccount.add(acc);
                                    listCategory.remove(cat);
                                    cat.setListAcc(listAccount);
                                    listCategory.add(cat);
                                    mngCat.serializationListCategory(ShowElementActivity.this, listCategory, usr.getUser());
                                    notifyUser(nameText + " è stato rimosso con successo!");
                                    refresh();
                                    popupWindow.dismiss();
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    notifyUser(nameText + " non è stato rimosso.");
                                    popupWindow.dismiss();
                                }
                            });
                        }
                    });
                    showPass((TextView) relLey.findViewById(R.id.passShowText), (ImageButton) relLey.findViewById((R.id.showPass)));
                }
                optionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMenu(R.style.rounded_menu_style_toolbar, R.menu.option_popup, v);
                    }
                });
            } else {
                notifyUser("Account non rilevato. Impossibile mostrare i dati.");
                goToViewActivity(usr);
            }
        } else {
            notifyUser("Utente non rilevato. Impossibile mostrare l'account.");
            goToMainActivity();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                goToEditActivity(usr, acc);
                return true;
            case R.id.delete:
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
                et.setText("Sei sicuro di voler eliminare " + account.getName() + " dai tuoi account?");
                Button yes = popupView.findViewById(R.id.yes);
                Button no = popupView.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listAccount.remove(account);
                        listCategory.remove(cat);
                        cat.setListAcc(listAccount);
                        listCategory.add(cat);
                        mngCat.serializationListCategory(ShowElementActivity.this, listCategory, usr.getUser());
                        notifyUser(account.getName() + " è stato rimosso con successo!");
                        goToViewActivity(usr);
                        popupWindow.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyUser(account.getName() + " non è stato rimosso.");
                        popupWindow.dismiss();
                    }
                });
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

    public void goToViewActivity(User usr) {
        Intent intent = new Intent(ShowElementActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("category", cat);
        startActivity(intent);
        finish();
    }

    public void goToEditActivity(User usr, Account acc, AccountElement ae) {
        Intent intent = new Intent(ShowElementActivity.this, EditActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("account", acc);
        intent.putExtra("accountElement", ae);
        startActivity(intent);
    }

    public void goToEditActivity(User usr, Account acc) {
        Intent intent = new Intent(ShowElementActivity.this, EditActivity.class);
        intent.putExtra("owner", usr);
        intent.putExtra("account", acc);
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
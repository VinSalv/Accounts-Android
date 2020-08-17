package com.app.accounts;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.RelativeLayout;
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
    private ImageButton showPass;
    private int attempts;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("account", account);
        intent.putExtra("accountElement", accountElement);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void goToEditActivity(Account account, Category category) {
        Intent intent = new Intent(ShowElementActivity.this, EditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("account", account);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void goToCategoryChoseActivity(String opt) {
        Intent intent = new Intent(ShowElementActivity.this, CategoryChoseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ArrayList<Account> listAcc = new ArrayList<>();
        listAcc.add(account);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        intent.putExtra("listAccount", listAcc);
        intent.putExtra("option", opt);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        goToViewActivity(category);
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

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRestart() {
        super.onRestart();
        layoutShowElementActivity.setVisibility(View.INVISIBLE);
        BiometricManager biometricManager = BiometricManager.from(ShowElementActivity.this);
        if (usr.getFinger() && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
            biometricAuthentication(layoutShowElementActivity);
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
                layoutShowElementActivity.setVisibility(View.VISIBLE);
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
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupViewCheck = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password_match_parent, (ViewGroup) findViewById(R.id.passSecurityPopupMatchParent));
        final PopupWindow popupWindowCheck = new PopupWindow(popupViewCheck, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindowCheck.setBackgroundDrawable(new BitmapDrawable());
        View parent = layoutShowElementActivity.getRootView();
        popupWindowCheck.showAtLocation(parent, Gravity.CENTER, 0, 0);
        final EditText popupText = popupViewCheck.findViewById(R.id.passSecurityEditText);
        Button conf = popupViewCheck.findViewById(R.id.confirmation);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ShowElementActivity.this, R.color.colorAccent)));
                if (popupText.getText().toString().isEmpty()) {
                    popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ShowElementActivity.this, R.color.errorEditText)));
                    notifyUser("Il campo password è vuoto");
                } else {
                    if (popupText.getText().toString().equals(usr.getPassword())) {
                        layoutShowElementActivity.setVisibility(View.VISIBLE);
                        attempts = 3;
                        popupWindowCheck.dismiss();
                    } else {
                        popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ShowElementActivity.this, R.color.errorEditText)));
                        attempts--;
                        if (attempts == 2)
                            notifyUserShortWay("Password errata. Hai altri " + attempts + " tentativi");
                        else if (attempts == 1)
                            notifyUserShortWay("Password errata. Hai un ultimo tenativo");
                        else {
                            notifyUserShortWay("Password errata");
                            goToMainActivity();
                        }
                    }
                }
            }
        });
        showPass = popupViewCheck.findViewById(R.id.showPass);
        showPass(popupText, showPass);
    }
}
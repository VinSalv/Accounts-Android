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
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "SuspiciousMethodCalls", "deprecation", "SameParameterValue"})
public class AddActivity extends AppCompatActivity {
    private RelativeLayout layoutAddActivity;
    private LinearLayout layoutContentAddToAddOtherAccount;
    private ArrayList<RelativeLayout> listInflaterLayout;
    private ManageCategory mngCat;
    private ArrayList<Category> listCategory;
    private ArrayList<Account> listAccount;
    private ArrayList<AccountElement> listAccountElement;
    private EditText nameAccount;
    private ArrayList<EditText> listEmailAccount;
    private ArrayList<EditText> listUserAccount;
    private ArrayList<EditText> listPasswordAccount;
    private ArrayList<EditText> listDescriptionAccount;
    private ArrayList<ImageButton> listShowPasswordAccount;
    private User usr;
    private Category category;
    private Account accountToAdd;
    private AccountElement accountElementToAdd;
    private int i;
    private boolean b;
    private int attempts;
    private ImageButton showPass;
    private boolean blockBack;
    private boolean doubleBackToExitPressedOnce;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.addToolbar);
        layoutAddActivity = findViewById(R.id.addActivityLay);
        layoutContentAddToAddOtherAccount = findViewById(R.id.linearLayAdd);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            blockBack = true;
            attempts = 3;
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            toolbar.setSubtitle(Html.fromHtml("Aggiungi account alla categoria <b>" + category.getCat() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            setSupportActionBar(toolbar);
            listAccount = category.getListAcc();
            listAccountElement = new ArrayList<>();
            listInflaterLayout = new ArrayList<>();
            nameAccount = findViewById(R.id.nameAddEdit);
            listEmailAccount = new ArrayList<>();
            listUserAccount = new ArrayList<>();
            listPasswordAccount = new ArrayList<>();
            listDescriptionAccount = new ArrayList<>();
            listShowPasswordAccount = new ArrayList<>();
            i = 0;
            addInflaterLayout(layoutContentAddToAddOtherAccount);
            Button saveNewAccount = findViewById(R.id.addButton);
            Button clearAllGaps = findViewById(R.id.emptyButton);
            FloatingActionButton addAccountElement = findViewById(R.id.addElemFloatingButton);
            saveNewAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b = false;
                    nameAccount.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                    listAccountElement.clear();
                    for (int n = 0; n < i; n++) {
                        listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        if (!(listEmailAccount.get(n).getText().toString().isEmpty()
                                && listUserAccount.get(n).getText().toString().isEmpty()
                                && listPasswordAccount.get(n).getText().toString().isEmpty()
                                && listDescriptionAccount.get(n).getText().toString().isEmpty())) {
                            accountElementToAdd = new AccountElement(listEmailAccount.get(n).getText().toString(),
                                    listUserAccount.get(n).getText().toString(),
                                    listPasswordAccount.get(n).getText().toString(),
                                    listDescriptionAccount.get(n).getText().toString());
                            listAccountElement.add(accountElementToAdd);
                        } else b = true;
                    }
                    accountToAdd = new Account(nameAccount.getText().toString(), category.getCat(), listAccountElement);
                    if (accountToAdd.getName().isEmpty()) {
                        nameAccount.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
                        notifyUser("Associare il nome all'account.");
                        return;
                    }
                    int n = 0;
                    for (AccountElement singleAccountElement : listAccountElement) {
                        if (checkGapError(singleAccountElement, n)) return;
                        n++;
                    }
                    if (mngCat.accountNotFound(accountToAdd, listAccount)) {
                        listAccount.add(accountToAdd);
                        listCategory.remove(category);
                        category.setListAcc(listAccount);
                        listCategory.add(category);
                        mngCat.serializationListCategory(AddActivity.this, listCategory, usr.getUser());
                        if (b)
                            notifyUserShortWay("Gli elementi senza nessun campo compilato non verranno memorizzati.");
                        goToViewActivity();
                    } else {
                        nameAccount.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
                        notifyUser(Html.fromHtml("Applicativo con il nome <b>" + accountToAdd.getName() + "</b> già registrato nella categoria <b>" + category.getCat() + "</b>.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                    }
                }

            });
            clearAllGaps.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewSecurity = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                    final PopupWindow popupWindowSecurity = new PopupWindow(popupViewSecurity, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowSecurity.setOutsideTouchable(false);
                    popupWindowSecurity.setFocusable(false);
                    popupWindowSecurity.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutAddActivity.getRootView();
                    popupWindowSecurity.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    TextView popupText = popupViewSecurity.findViewById(R.id.securityText);
                    popupText.setText(Html.fromHtml("Sei sicuro di voler resettare <b>TUTTI</b> i campi?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                    Button yes = popupViewSecurity.findViewById(R.id.yes);
                    Button no = popupViewSecurity.findViewById(R.id.no);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            i = 0;
                            listAccountElement.clear();
                            for (RelativeLayout l : listInflaterLayout) {
                                layoutContentAddToAddOtherAccount.removeView(l);
                            }
                            listInflaterLayout.clear();
                            listEmailAccount.clear();
                            listUserAccount.clear();
                            listPasswordAccount.clear();
                            listDescriptionAccount.clear();
                            listShowPasswordAccount.clear();
                            popupWindowSecurity.dismiss();
                            addInflaterLayout(v);
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
            addAccountElement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addInflaterLayout(v);
                }
            });
        } else {
            notifyUser("Credenziali non rilevate. Impossibile aggiungere l'account.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity() {
        Intent intent = new Intent(AddActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (blockBack) goToViewActivity();
        else {
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

    public boolean isInvalidWord(String word) {
        if (word.isEmpty()) return false;
        return !word.matches("[^ ]*");
    }

    public boolean isInvalidEmail(String email) {
        if (email.isEmpty()) return false;
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean checkGapError(AccountElement a, int n) {
        if (isInvalidEmail(a.getEmail()) && isInvalidWord(a.getUser()) && isInvalidWord(a.getPassword())) {
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente, Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidWord(a.getPassword())) {
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword()) && isInvalidEmail(a.getEmail())) {
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidEmail(a.getEmail())) {
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente e Email non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser())) {
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Utente non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidEmail(a.getEmail())) {
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Email non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword())) {
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Password non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    public void addInflaterLayout(View v) {
        LayoutInflater inflater = LayoutInflater.from(AddActivity.this);
        @SuppressLint("InflateParams") final RelativeLayout layoutToAdd = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 0);
        layoutToAdd.setLayoutParams(params);
        listInflaterLayout.add(layoutToAdd);
        listEmailAccount.add((EditText) layoutToAdd.findViewById(R.id.emailAddEdit));
        listUserAccount.add((EditText) layoutToAdd.findViewById(R.id.userAddEdit));
        listPasswordAccount.add((EditText) layoutToAdd.findViewById(R.id.passAddEdit));
        listDescriptionAccount.add((EditText) layoutToAdd.findViewById(R.id.descriptionAddEdit));
        listShowPasswordAccount.add((ImageButton) layoutToAdd.findViewById(R.id.showPass));
        (layoutToAdd.findViewById(R.id.emailAddImage)).setVisibility(View.GONE);
        (layoutToAdd.findViewById(R.id.emailAddEdit)).setVisibility(View.GONE);
        (layoutToAdd.findViewById(R.id.userAddImage)).setVisibility(View.GONE);
        (layoutToAdd.findViewById(R.id.userAddEdit)).setVisibility(View.GONE);
        (layoutToAdd.findViewById(R.id.passAddImage)).setVisibility(View.GONE);
        (layoutToAdd.findViewById(R.id.passAddEdit)).setVisibility(View.GONE);
        (layoutToAdd.findViewById(R.id.showPass)).setVisibility(View.GONE);
        refreshCardinality();
        layoutContentAddToAddOtherAccount.addView(layoutToAdd);
        final ImageButton showLayout = layoutToAdd.findViewById(R.id.showButton);
        final Boolean[] bool = {true};
        showLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bool[0]) {
                    bool[0] = false;
                    showLayout.setImageResource(android.R.drawable.arrow_down_float);
                    (layoutToAdd.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.emailAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.passAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddEdit)).setVisibility(View.VISIBLE);
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
                    (layoutToAdd.findViewById(R.id.passAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    bool[0] = true;
                    showLayout.setImageResource(android.R.drawable.arrow_up_float);
                    (layoutToAdd.findViewById(R.id.emailAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.passAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.passAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddEdit)).setVisibility(View.GONE);
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
                    (layoutToAdd.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.emailAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.passAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddEdit)).setVisibility(View.VISIBLE);
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
                    (layoutToAdd.findViewById(R.id.passAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    bool[0] = true;
                    showLayout.setImageResource(android.R.drawable.arrow_up_float);
                    (layoutToAdd.findViewById(R.id.emailAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.emailAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.userAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.userAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.passAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (layoutToAdd.findViewById(R.id.passAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (layoutToAdd.findViewById(R.id.passAddEdit)).setVisibility(View.GONE);
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
        ImageButton deleteLayoutInflater = layoutToAdd.findViewById(R.id.deleteButton);
        deleteLayoutInflater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewSecurityToDeleteOneInflater = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                final PopupWindow popupWindowSecurityToDeleteOneInflater = new PopupWindow(popupViewSecurityToDeleteOneInflater, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindowSecurityToDeleteOneInflater.setOutsideTouchable(true);
                popupWindowSecurityToDeleteOneInflater.setFocusable(true);
                popupWindowSecurityToDeleteOneInflater.setBackgroundDrawable(new BitmapDrawable());
                View parent = layoutAddActivity.getRootView();
                popupWindowSecurityToDeleteOneInflater.showAtLocation(parent, Gravity.CENTER, 0, 0);
                TextView popupText = popupViewSecurityToDeleteOneInflater.findViewById(R.id.securityText);
                popupText.setText(Html.fromHtml("Sei sicuro di voler eliminare <b>" + ((TextView) layoutToAdd.findViewById(R.id.cardinalityElements)).getText().toString() + "</b>?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                Button yes = popupViewSecurityToDeleteOneInflater.findViewById(R.id.yes);
                Button no = popupViewSecurityToDeleteOneInflater.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listEmailAccount.remove(layoutToAdd.findViewById(R.id.emailAddEdit));
                        listUserAccount.remove(layoutToAdd.findViewById(R.id.userAddEdit));
                        listPasswordAccount.remove(layoutToAdd.findViewById(R.id.passAddEdit));
                        listDescriptionAccount.remove(layoutToAdd.findViewById(R.id.descriptionAddEdit));
                        listShowPasswordAccount.remove(layoutToAdd.findViewById(R.id.showPass));
                        listInflaterLayout.remove(layoutToAdd);
                        layoutContentAddToAddOtherAccount.removeView(layoutToAdd);
                        i--;
                        refreshCardinality();
                        popupWindowSecurityToDeleteOneInflater.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowSecurityToDeleteOneInflater.dismiss();
                    }
                });
            }
        });
        showPass((EditText) layoutToAdd.findViewById(R.id.passAddEdit), (ImageButton) layoutToAdd.findViewById((R.id.showPass)));
        i++;
    }

    @SuppressLint("SetTextI18n")
    public void refreshCardinality() {
        int c = 0;
        for (RelativeLayout singleRelativeLayout : listInflaterLayout) {
            c++;
            ((TextView) singleRelativeLayout.findViewById(R.id.cardinalityElements)).setText(c + "");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRestart() {
        super.onRestart();
        layoutAddActivity.setVisibility(View.INVISIBLE);
        BiometricManager biometricManager = BiometricManager.from(AddActivity.this);
        if (usr.getFinger() && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
            biometricAuthentication(layoutAddActivity);
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
                layoutAddActivity.setVisibility(View.VISIBLE);
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
    public void biometricAuthentication(RelativeLayout lay) {
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
        final PopupWindow popupWindowCheck = new PopupWindow(popupViewCheck, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindowCheck.setBackgroundDrawable(new BitmapDrawable());
        View parent = layoutAddActivity.getRootView();
        popupWindowCheck.showAtLocation(parent, Gravity.CENTER, 0, 0);
        final EditText popupText = popupViewCheck.findViewById(R.id.passSecurityEditText);
        Button conf = popupViewCheck.findViewById(R.id.confirmation);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                if (popupText.getText().toString().isEmpty()) {
                    popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
                    notifyUser("Il campo password è vuoto");
                } else {
                    if (popupText.getText().toString().equals(usr.getPassword())) {
                        layoutAddActivity.setVisibility(View.VISIBLE);
                        blockBack = true;
                        attempts = 3;
                        popupWindowCheck.dismiss();
                    } else {
                        popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
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
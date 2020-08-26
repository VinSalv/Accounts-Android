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
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"SameParameterValue", "deprecation"})
public class SearchActivity extends AppCompatActivity {
    private LinearLayout layoutSearchActivity;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ArrayList<User> listUser;
    private ManageCategory mngCat;
    private User usr;
    private Category category;
    private ArrayList<Category> listCategory;
    private TextView accountFound;
    private int i;
    private int j;
    private int attempts;
    private boolean blockBack;
    private boolean doubleBackToExitPressedOnce;
    private PopupWindow popupWindowCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_search);
        layoutSearchActivity = findViewById(R.id.contentSearchLayout);
        mngApp = new ManageApp();
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser();
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            blockBack = true;
            attempts = 3;
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(usr.getUser());
            accountFound = findViewById(R.id.elemFind);
            EditText name = findViewById(R.id.nameSerched);
            name.addTextChangedListener(new TextWatcher() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("account", account);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void goToCategoryActivity() {
        Intent intent = new Intent(SearchActivity.this, CategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity() {
        Intent intent = new Intent(SearchActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    public void onBackPressed() {
        if (blockBack) {
            if (Objects.equals(Objects.requireNonNull(getIntent().getExtras()).get("cat"), ""))
                goToCategoryActivity();
            else {
                category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
                goToViewActivity();
            }
        } else {
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

    public ArrayList<Account> increasing(ArrayList<Account> list) {
        Collections.sort(list, new Comparator<Account>() {
            @Override
            public int compare(Account lhs, Account rhs) {
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        });
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRestart() {
        super.onRestart();
        if (!blockBack)
            popupWindowCheck.dismiss();
        layoutSearchActivity.setVisibility(View.INVISIBLE);
        BiometricManager biometricManager = BiometricManager.from(SearchActivity.this);
        if (usr.getFinger() && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
            biometricAuthentication(layoutSearchActivity);
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
                layoutSearchActivity.setVisibility(View.VISIBLE);
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
    public void biometricAuthentication(LinearLayout lay) {
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
        packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
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
        popupWindowCheck = new PopupWindow(popupViewCheck, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindowCheck.setBackgroundDrawable(new BitmapDrawable());
        View parent = layoutSearchActivity.getRootView();
        popupWindowCheck.showAtLocation(parent, Gravity.CENTER, 0, 0);
        final EditText popupText = popupViewCheck.findViewById(R.id.passSecurityEditText);
        Button conf = popupViewCheck.findViewById(R.id.confirmation);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SearchActivity.this, R.color.colorAccent)));
                if (popupText.getText().toString().isEmpty()) {
                    popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SearchActivity.this, R.color.errorEditText)));
                    notifyUser("Il campo password è vuoto");
                } else {
                    if (popupText.getText().toString().equals(usr.getPassword())) {
                        layoutSearchActivity.setVisibility(View.VISIBLE);
                        blockBack = true;
                        attempts = 3;
                        popupWindowCheck.dismiss();
                    } else {
                        popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SearchActivity.this, R.color.errorEditText)));
                        attempts--;
                        if (attempts == 2)
                            notifyUserShortWay("Password errata. Hai altri " + attempts + " tentativi");
                        else if (attempts == 1)
                            notifyUserShortWay("Password errata. Hai un ultimo tenativo");
                        else {
                            notifyUserShortWay("Password errata");
                            mngApp.serializationFlag(new LogApp());
                            goToMainActivity();
                        }
                    }
                }
            }
        });
        ImageButton showPass = popupViewCheck.findViewById(R.id.showPass);
        showPass(popupText, showPass);
        ImageButton cancel = popupViewCheck.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mngApp.serializationFlag(new LogApp());
                goToMainActivity();
            }
        });
    }
}

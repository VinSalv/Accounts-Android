package com.example.accounts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "SuspiciousMethodCalls", "deprecation"})
public class EditActivity extends AppCompatActivity {
    private RelativeLayout layoutEditActivity;
    private LinearLayout layoutContentEditToAddOtherAccount;
    private ScrollView scrollViewContentEdit;
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
    private Account accountToEdit;
    private AccountElement accountElementToEdit;
    private int i;
    private boolean b;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.editToolbar);
        layoutEditActivity = findViewById(R.id.editActivityLay);
        layoutContentEditToAddOtherAccount = findViewById(R.id.linearLayEdit);
        ConstraintLayout constraintLayoutButtons = findViewById(R.id.constraintLayoutButtons);
        AccountElement specificAccountElement = (AccountElement) (Objects.requireNonNull(getIntent().getExtras())).get("accountElement");
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            mngCat = new ManageCategory();
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            listAccount = category.getListAcc();
            accountToEdit = mngCat.findAndGetAccount(listAccount, ((Account) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("account"))).getName());
            toolbar.setSubtitle(Html.fromHtml("Modifica account <b>" + accountToEdit.getName() + "</b> dlla categoria <b>" + category.getCat() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            setSupportActionBar(toolbar);
            if (accountToEdit != null) {
                listAccountElement = new ArrayList<>();
                listInflaterLayout = new ArrayList<>();
                nameAccount = findViewById(R.id.nameEditEdit);
                listEmailAccount = new ArrayList<>();
                listUserAccount = new ArrayList<>();
                listPasswordAccount = new ArrayList<>();
                listDescriptionAccount = new ArrayList<>();
                listShowPasswordAccount = new ArrayList<>();
                i = 0;
                if (accountToEdit.getList().isEmpty())
                    addInflaterLayout(layoutContentEditToAddOtherAccount);
                Button saveEditAccount = constraintLayoutButtons.findViewById(R.id.saveButton);
                Button clearAllGaps = constraintLayoutButtons.findViewById(R.id.emptyButton);
                FloatingActionButton addAccountElement = findViewById(R.id.addElemFloatingButton);
                nameAccount.setText(accountToEdit.getName());
                for (final AccountElement singleAccountElement : accountToEdit.getList()) {
                    LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
                    @SuppressLint("InflateParams") final RelativeLayout layoutToAdd = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 20, 0, 0);
                    layoutToAdd.setLayoutParams(params);
                    listInflaterLayout.add(layoutToAdd);
                    listEmailAccount.add((EditText) layoutToAdd.findViewById(R.id.emailAddEdit));
                    listEmailAccount.get(i).setText(singleAccountElement.getEmail());
                    listUserAccount.add((EditText) layoutToAdd.findViewById(R.id.userAddEdit));
                    listUserAccount.get(i).setText(singleAccountElement.getUser());
                    listPasswordAccount.add((EditText) layoutToAdd.findViewById(R.id.passAddEdit));
                    listPasswordAccount.get(i).setText(singleAccountElement.getPassword());
                    listDescriptionAccount.add((EditText) layoutToAdd.findViewById(R.id.descriptionAddEdit));
                    listDescriptionAccount.get(i).setText(singleAccountElement.getDescription());
                    listShowPasswordAccount.add((ImageButton) layoutToAdd.findViewById((R.id.showPass)));
                    (layoutToAdd.findViewById(R.id.emailAddImage)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.emailAddEdit)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.userAddImage)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.userAddEdit)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.passAddImage)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.passAddEdit)).setVisibility(View.GONE);
                    (layoutToAdd.findViewById(R.id.showPass)).setVisibility(View.GONE);
                    final ImageButton showLayout = layoutToAdd.findViewById(R.id.showButton);
                    final Boolean[] bool = {true};
                    showLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bool[0]) {
                                bool[0] = false;
                                showLayout.setImageResource(android.R.drawable.arrow_down_float);
                                ((EditText) layoutToAdd.findViewById(R.id.emailAddEdit)).setText(singleAccountElement.getEmail());
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
                                ((EditText) layoutToAdd.findViewById(R.id.userAddEdit)).setText(singleAccountElement.getUser());
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
                                ((EditText) layoutToAdd.findViewById(R.id.passAddEdit)).setText(singleAccountElement.getPassword());
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
                    layoutContentEditToAddOtherAccount.addView(layoutToAdd);
                    refreshCardinality();
                    (layoutToAdd.findViewById(R.id.cardinalityElements)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bool[0]) {
                                bool[0] = false;
                                showLayout.setImageResource(android.R.drawable.arrow_down_float);
                                ((EditText) layoutToAdd.findViewById(R.id.emailAddEdit)).setText(singleAccountElement.getEmail());
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
                                ((EditText) layoutToAdd.findViewById(R.id.userAddEdit)).setText(singleAccountElement.getUser());
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
                                ((EditText) layoutToAdd.findViewById(R.id.passAddEdit)).setText(singleAccountElement.getPassword());
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
                            //noinspection deprecation
                            popupWindowSecurityToDeleteOneInflater.setBackgroundDrawable(new BitmapDrawable());
                            View parent = layoutEditActivity.getRootView();
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
                                    layoutContentEditToAddOtherAccount.removeView(layoutToAdd);
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
                if (specificAccountElement != null) {
                    for (RelativeLayout singleInflaterLayout : listInflaterLayout) {
                        if (((EditText) singleInflaterLayout.findViewById(R.id.emailAddEdit)).getText().toString().equals(specificAccountElement.getEmail()) &&
                                ((EditText) singleInflaterLayout.findViewById(R.id.userAddEdit)).getText().toString().equals(specificAccountElement.getUser()) &&
                                ((EditText) singleInflaterLayout.findViewById(R.id.passAddEdit)).getText().toString().equals(specificAccountElement.getPassword()) &&
                                ((EditText) singleInflaterLayout.findViewById(R.id.descriptionAddEdit)).getText().toString().equals(specificAccountElement.getDescription())) {
                            (singleInflaterLayout.findViewById(R.id.showButton)).performClick();
                            scrollViewContentEdit = findViewById(R.id.scrollEdit);
                            focusOnView(singleInflaterLayout);
                        }
                    }
                }
                saveEditAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b = false;
                        nameAccount.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.colorAccent)));
                        listAccountElement.clear();
                        for (int n = 0; n < i; n++) {
                            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.colorAccent)));
                            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.colorAccent)));
                            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.colorAccent)));
                            if (!(listEmailAccount.get(n).getText().toString().isEmpty()
                                    && listUserAccount.get(n).getText().toString().isEmpty()
                                    && listPasswordAccount.get(n).getText().toString().isEmpty()
                                    && listDescriptionAccount.get(n).getText().toString().isEmpty())) {
                                accountElementToEdit = new AccountElement(listEmailAccount.get(n).getText().toString(),
                                        listUserAccount.get(n).getText().toString(),
                                        listPasswordAccount.get(n).getText().toString(),
                                        listDescriptionAccount.get(n).getText().toString());
                                listAccountElement.add(accountElementToEdit);
                            } else b = true;
                        }
                        accountToEdit = new Account(nameAccount.getText().toString(), category.getCat(), listAccountElement);
                        if (accountToEdit.getName().isEmpty()) {
                            nameAccount.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
                            notifyUser("Il campo nome non può essere vuoto.");
                            return;
                        }
                        int n = 0;
                        for (AccountElement singleAccountElement : listAccountElement) {
                            if (fieldError(singleAccountElement, n)) return;
                            n++;
                        }
                        if (mngCat.accountNotFound(accountToEdit, listAccount) || accountToEdit.equals(accountToEdit.getName())) {
                            listAccount.remove(accountToEdit);
                            listAccount.add(accountToEdit);
                            listCategory.remove(category);
                            category.setListAcc(listAccount);
                            listCategory.add(category);
                            mngCat.serializationListCategory(EditActivity.this, listCategory, usr.getUser());
                            if (b)
                                notifyUserShortWay("Gli elementi senza nessun campo compilato non verranno memorizzati.");
                            goToViewActivity();
                        } else {
                            nameAccount.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
                            notifyUser(Html.fromHtml("Applicativo con il nome <b>" + accountToEdit.getName() + "</b> già registrato nella categoria <b>" + category.getCat() + "</b>.", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
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
                        View parent = layoutEditActivity.getRootView();
                        popupWindowSecurity.showAtLocation(parent, Gravity.CENTER, 0, 0);
                        TextView popupText = popupViewSecurity.findViewById(R.id.securityText);
                        popupText.setText(Html.fromHtml("Sei sicuro di voler resettare <b> TUTTI </b> i campi?", HtmlCompat.FROM_HTML_MODE_LEGACY));
                        Button yes = popupViewSecurity.findViewById(R.id.yes);
                        Button no = popupViewSecurity.findViewById(R.id.no);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                i = 0;
                                listAccountElement.clear();
                                for (RelativeLayout l : listInflaterLayout) {
                                    layoutContentEditToAddOtherAccount.removeView(l);
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
                notifyUser("Account da modificare non rilevato. Impossibile modificare i suoi dati.");
                goToViewActivity();
            }
        } else {
            notifyUser("Credenziali non rilevate. Impossibile modificare l'account.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity() {
        Intent intent = new Intent(EditActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    private void focusOnView(final RelativeLayout rel) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollViewContentEdit.scrollTo(0, rel.getBottom());
            }
        });
    }

    public boolean fieldError(AccountElement a, int n) {
        if (isInvalidWord(a.getUser()) && isInvalidEmail(a.getEmail()) && isInvalidWord(a.getPassword())) {
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            Toast.makeText(EditActivity.this, "Campi Utente, Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidWord(a.getPassword())) {
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            Toast.makeText(EditActivity.this, "Campi Utente e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword()) && isInvalidEmail(a.getEmail())) {
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            Toast.makeText(EditActivity.this, "Campi Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidEmail(a.getEmail())) {
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            Toast.makeText(EditActivity.this, "Campi Utente e Email non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser())) {
            listUserAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            Toast.makeText(EditActivity.this, "Campo Utente non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidEmail(a.getEmail())) {
            listEmailAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            Toast.makeText(EditActivity.this, "Campo Email non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword())) {
            listPasswordAccount.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(EditActivity.this, R.color.errorEditText)));
            Toast.makeText(EditActivity.this, "Campo Password non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean isInvalidWord(String word) {
        if (word.isEmpty()) return false;
        return !word.matches("[^ ]*");
    }

    public boolean isInvalidEmail(String email) {
        if (email.isEmpty()) return false;
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    @SuppressLint("SetTextI18n")
    public void addInflaterLayout(View v) {
        LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
        @SuppressLint("InflateParams") final RelativeLayout layoutToAdd = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
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
        layoutContentEditToAddOtherAccount.addView(layoutToAdd);
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
                View parent = layoutEditActivity.getRootView();
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
                        layoutContentEditToAddOtherAccount.removeView(layoutToAdd);
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
        for (RelativeLayout rl : listInflaterLayout) {
            c++;
            ((TextView) rl.findViewById(R.id.cardinalityElements)).setText(c + "." + accountToEdit.getName());
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
}
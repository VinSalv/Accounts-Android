package com.app.accounts;

import android.Manifest;
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
import android.os.Environment;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"ResultOfMethodCallIgnored", "deprecation", "MismatchedQueryAndUpdateOfCollection", "SameParameterValue"})
public class SettingActivity extends AppCompatActivity {
    private CoordinatorLayout layoutSettingActivity;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ManageCategory mngCat;
    private Category category;
    private ArrayList<User> listUser = new ArrayList<>();
    private ArrayList<Category> listCategory;
    private ArrayList<Category> listCategoryPdf;
    private LogApp log;
    private User usr;
    private TextView setting;
    private TextView settingToolbar;
    private ImageButton showPass;
    private File dir;
    private String path;
    private int attempts;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_setting);
        final Toolbar toolbar = findViewById(R.id.settingToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layoutSettingActivity = findViewById(R.id.settingActivityLay);
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            attempts = 3;
            mngCat = new ManageCategory();
            setting = findViewById(R.id.settingText);
            setting.setText("Impostazioni");
            settingToolbar = findViewById(R.id.settingTextToolbar);
            settingToolbar.setText("Impostazioni");
            settingToolbar.setVisibility(View.INVISIBLE);
            AppBarLayout appBar = findViewById(R.id.settingBarToolbar);
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    setting.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true;
                        settingToolbar.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        settingToolbar.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Button prof = findViewById(R.id.profile);
            prof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewProfile = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password, (ViewGroup) findViewById(R.id.passSecurityPopup));
                    final PopupWindow popupWindowProfile = new PopupWindow(popupViewProfile, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowProfile.setOutsideTouchable(true);
                    popupWindowProfile.setFocusable(true);
                    popupWindowProfile.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutSettingActivity.getRootView();
                    popupWindowProfile.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText popupText = popupViewProfile.findViewById(R.id.passSecurityEditText);
                    Button conf = popupViewProfile.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.colorAccent)));
                            if (popupText.getText().toString().equals(usr.getPassword())) {
                                goToProfileActivity();
                                popupWindowProfile.dismiss();
                            } else {
                                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
                                notifyUser("Password errata.");
                            }
                        }
                    });
                    showPass = popupViewProfile.findViewById(R.id.showPass);
                    showPass(popupText, showPass);
                }
            });
            Button custom = findViewById(R.id.customize);
            custom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToCustomizeActivity();
                }
            });
            Button pdf = findViewById(R.id.pdf);
            pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupViewPdf = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password, (ViewGroup) findViewById(R.id.passSecurityPopup));
                    final PopupWindow popupWindowPdf = new PopupWindow(popupViewPdf, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowPdf.setOutsideTouchable(true);
                    popupWindowPdf.setFocusable(true);
                    popupWindowPdf.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutSettingActivity.getRootView();
                    popupWindowPdf.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText popupText = popupViewPdf.findViewById(R.id.passSecurityEditText);
                    Button conf = popupViewPdf.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.colorAccent)));
                            if (popupText.getText().toString().equals(usr.getPassword())) {
                                popupWindowPdf.dismiss();
                                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                final View popupViewChoseCat = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_pdf_chose_category, (ViewGroup) findViewById(R.id.pdfChoseCategoryPopup));
                                final PopupWindow popupWindowChoseCat = new PopupWindow(popupViewChoseCat, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                popupWindowChoseCat.setOutsideTouchable(true);
                                popupWindowChoseCat.setFocusable(true);
                                popupWindowChoseCat.setBackgroundDrawable(new BitmapDrawable());
                                View parent = layoutSettingActivity.getRootView();
                                popupWindowChoseCat.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                final Button selectAll = popupViewChoseCat.findViewById(R.id.select_all_cat_pdf);
                                int i = 0;
                                for (Category singleCategory : mngCat.deserializationListCategory(SettingActivity.this, usr.getUser())) {
                                    CheckBox checkBox = new CheckBox(SettingActivity.this);
                                    checkBox.setId(i);
                                    checkBox.setText(singleCategory.getCat());
                                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            boolean bool = true;
                                            int i = 0;
                                            for (Category ignored : mngCat.deserializationListCategory(SettingActivity.this, usr.getUser())) {
                                                CheckBox checkBox = popupViewChoseCat.findViewById(i);
                                                if (!checkBox.isChecked()) bool = false;
                                                i++;
                                            }
                                            if (!bool) {
                                                selectAll.setText("Seleziona tutto");
                                            } else {
                                                selectAll.setText("Deseleziona tutto");
                                            }
                                        }
                                    });
                                    ((LinearLayout) popupViewChoseCat.findViewById(R.id.pdfChoseCategoryPopupElem)).addView(checkBox, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    i++;
                                }
                                selectAll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int i = 0;
                                        if (selectAll.getText().toString().toLowerCase().equals("seleziona tutto")) {
                                            for (Category ignored : mngCat.deserializationListCategory(SettingActivity.this, usr.getUser())) {
                                                CheckBox checkBox = popupViewChoseCat.findViewById(i);
                                                checkBox.setChecked(true);
                                                i++;
                                            }
                                            selectAll.setText("Deseleziona tutto");
                                        } else {
                                            for (Category ignored : mngCat.deserializationListCategory(SettingActivity.this, usr.getUser())) {
                                                CheckBox checkBox = popupViewChoseCat.findViewById(i);
                                                checkBox.setChecked(false);
                                                i++;
                                            }
                                            selectAll.setText("Seleziona tutto");
                                        }

                                    }
                                });
                                Button conf = new Button(SettingActivity.this);
                                conf.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                                conf.setText("Prosegui");
                                conf.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                conf.setTextColor(getResources().getColor(R.color.colorPrimaryClear));
                                conf.setTextSize(18);
                                ((LinearLayout) popupViewChoseCat.findViewById(R.id.pdfChoseCategoryPopup)).addView(conf, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                conf.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int i = 0;
                                        listCategoryPdf = new ArrayList<>();
                                        for (Category ignored : mngCat.deserializationListCategory(SettingActivity.this, usr.getUser())) {
                                            CheckBox checkBox = popupViewChoseCat.findViewById(i);
                                            if (checkBox.isChecked())
                                                listCategoryPdf.add(mngCat.findAndGetCategory(mngCat.deserializationListCategory(SettingActivity.this, usr.getUser()), checkBox.getText().toString()));
                                            i++;
                                        }
                                        if (listCategoryPdf.isEmpty()) {
                                            notifyUserShortWay("Non hai selezionato nessuna categoria.");
                                            return;
                                        } else popupWindowChoseCat.dismiss();
                                        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                        View popupViewCreatePdf = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_pdf, (ViewGroup) findViewById(R.id.popupPdf));
                                        final PopupWindow popupWindowCreatePdf = new PopupWindow(popupViewCreatePdf, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                        popupWindowCreatePdf.setOutsideTouchable(true);
                                        popupWindowCreatePdf.setFocusable(true);
                                        popupWindowCreatePdf.setBackgroundDrawable(new BitmapDrawable());
                                        View parent = layoutSettingActivity.getRootView();
                                        popupWindowCreatePdf.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                        final RadioGroup radioGroupSortPdfCategory = popupViewCreatePdf.findViewById(R.id.sortPDFCategory);
                                        final RadioGroup radioGroupSortPdfAccount = popupViewCreatePdf.findViewById(R.id.sortPDFAccount);
                                        final RadioGroup radioGroupOrientationPdf = popupViewCreatePdf.findViewById(R.id.orientationPDF);
                                        final RadioButton radioButtonIncreasingCategory = popupViewCreatePdf.findViewById(R.id.increasingCategory);
                                        final RadioButton radioButtonDecreasingCategory = popupViewCreatePdf.findViewById(R.id.decreasingCategory);
                                        final RadioButton radioButtonIncreasingAccount = popupViewCreatePdf.findViewById(R.id.increasingAccount);
                                        final RadioButton radioButtonDecreasingAccount = popupViewCreatePdf.findViewById(R.id.decreasingAccount);
                                        final RadioButton radioButtonHorizontal = popupViewCreatePdf.findViewById(R.id.horizontal);
                                        radioGroupSortPdfCategory.check(radioButtonIncreasingCategory.getId());
                                        radioGroupSortPdfAccount.check(radioButtonIncreasingAccount.getId());
                                        radioGroupOrientationPdf.check(radioButtonHorizontal.getId());
                                        Button conf = popupViewCreatePdf.findViewById(R.id.confirmation);
                                        conf.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PDF_Accounts/";
                                                dir = new File(path);
                                                if (!dir.exists()) dir.mkdirs();
                                                ArrayList<Category> listCategoryApp;
                                                if (radioGroupSortPdfCategory.getCheckedRadioButtonId() == radioButtonIncreasingCategory.getId()) {
                                                    listCategoryApp = increasingCat(listCategoryPdf);
                                                } else if (radioGroupSortPdfCategory.getCheckedRadioButtonId() == radioButtonDecreasingCategory.getId()) {
                                                    listCategoryApp = decreasingCat(listCategoryPdf);
                                                } else listCategoryApp = listCategoryPdf;
                                                ArrayList<Category> listCategoryAppPDF = new ArrayList<>();
                                                if (radioGroupSortPdfAccount.getCheckedRadioButtonId() == radioButtonIncreasingAccount.getId()) {
                                                    for (Category singleCategory : listCategoryApp) {
                                                        ArrayList<Account> listApp = (increasing(singleCategory.getListAcc()));
                                                        singleCategory.setListAcc(listApp);
                                                        listCategoryAppPDF.add(singleCategory);
                                                    }
                                                } else if (radioGroupSortPdfAccount.getCheckedRadioButtonId() == radioButtonDecreasingAccount.getId()) {
                                                    for (Category singleCategory : listCategoryApp) {
                                                        ArrayList<Account> listApp = (decreasing(singleCategory.getListAcc()));
                                                        singleCategory.setListAcc(listApp);
                                                        listCategoryAppPDF.add(singleCategory);
                                                    }
                                                } else {
                                                    for (Category singleCategory : listCategoryApp) {
                                                        ArrayList<Account> listApp = singleCategory.getListAcc();
                                                        singleCategory.setListAcc(listApp);
                                                        listCategoryAppPDF.add(singleCategory);
                                                    }
                                                }
                                                if (radioGroupOrientationPdf.getCheckedRadioButtonId() == radioButtonHorizontal.getId()) {
                                                    createPDF(listCategoryAppPDF, path, dir, true);
                                                } else {
                                                    createPDF(listCategoryAppPDF, path, dir, false);
                                                }
                                                popupWindowCreatePdf.dismiss();
                                            }
                                        });
                                    }
                                });
                            } else {
                                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
                                notifyUser("Password errata.");
                            }

                            showPass = popupViewPdf.findViewById(R.id.showPass);

                            showPass(popupText, showPass);
                        }
                    });
                }
            });
            Button delProf = findViewById(R.id.deleteProfile);
            delProf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewDeleteProfile = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password, (ViewGroup) findViewById(R.id.passSecurityPopup));
                    final PopupWindow popupWindowDeleteProfile = new PopupWindow(popupViewDeleteProfile, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindowDeleteProfile.setOutsideTouchable(true);
                    popupWindowDeleteProfile.setFocusable(true);
                    popupWindowDeleteProfile.setBackgroundDrawable(new BitmapDrawable());
                    View parent = layoutSettingActivity.getRootView();
                    popupWindowDeleteProfile.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText popupText = popupViewDeleteProfile.findViewById(R.id.passSecurityEditText);
                    Button conf = popupViewDeleteProfile.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.colorAccent)));
                            if (popupText.getText().toString().equals(usr.getPassword())) {
                                log = new LogApp();
                                mngApp.serializationFlag(SettingActivity.this, log);
                                listUser.remove(usr);
                                mngUsr.serializationListUser(SettingActivity.this, listUser);
                                mngCat.removeFileCategory(SettingActivity.this, usr.getUser());
                                goToMainActivity();
                                popupWindowDeleteProfile.dismiss();
                            } else {
                                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
                                notifyUser("Password errata.");
                            }
                        }
                    });
                    showPass = popupViewDeleteProfile.findViewById(R.id.showPass);
                    showPass(popupText, showPass);
                }
            });
        } else {
            notifyUser("Credenziali non rilevate. Impossibile aprire le impostazioni.");
            goToMainActivity();
        }

    }

    public void goToMainActivity() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToCategoryActivity() {
        Intent intent = new Intent(SettingActivity.this, CategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
    }

    public void goToProfileActivity() {
        Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        if (((String) getIntent().getExtras().get("cat")).equals(""))
            intent.putExtra("cat", "");
        else {
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            intent.putExtra("category", category);
            intent.putExtra("cat", category.getCat());
        }
        startActivity(intent);
        finish();
    }

    public void goToCustomizeActivity() {
        Intent intent = new Intent(SettingActivity.this, CustomizeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        if (((String) getIntent().getExtras().get("cat")).equals(""))
            intent.putExtra("cat", "");
        else {
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            intent.putExtra("category", category);
            intent.putExtra("cat", category.getCat());
        }
        startActivity(intent);
        finish();
    }

    public void goToViewActivity() {
        Intent intent = new Intent(SettingActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    public void onBackPressed() {
        if (((String) getIntent().getExtras().get("cat")).equals(""))
            goToCategoryActivity();
        else {
            listCategory = mngCat.deserializationListCategory(this, usr.getUser());
            category = mngCat.findAndGetCategory(listCategory, ((Category) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("category"))).getCat());
            goToViewActivity();
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

    public void createPDF(ArrayList<Category> listCategory, String path, File dir, Boolean b) {
        Document doc;
        Font font;
        File file;
        PdfPCell cell;
        if (b) doc = new Document(PageSize.A4.rotate());
        else doc = new Document(PageSize.A4);
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            if (b)
                file = new File(dir, "Lista account di " + usr.getUser() + sdf.format(Calendar.getInstance().getTime()) + "Orizzontale.pdf");
            else
                file = new File(dir, "Lista account di " + usr.getUser() + sdf.format(Calendar.getInstance().getTime()) + "Verticale.pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);
            doc.open();
            try {
                int j = 0;
                for (Category c : listCategory) {
                    PdfPTable pt = new PdfPTable(5);
                    pt.setWidthPercentage(100);
                    float[] fl = new float[]{15, 25, 20, 15, 25};
                    pt.setWidths(fl);
                    Phrase f;
                    if (j != 0) {
                        font = new Font(Font.FontFamily.TIMES_ROMAN, 2, Font.BOLD);
                        cell = new PdfPCell();
                        f = new Phrase(" ");
                        f.setFont(font);
                        cell.addElement(f);
                        cell.setBorder(0);
                        pt.addCell(cell);
                        cell = new PdfPCell();
                        f = new Phrase("");
                        f.setFont(font);
                        cell.addElement(f);
                        cell.setBorder(0);
                        pt.addCell(cell);
                        cell = new PdfPCell();
                        f = new Phrase("");
                        f.setFont(font);
                        cell.addElement(f);
                        cell.setBorder(0);
                        pt.addCell(cell);
                        cell = new PdfPCell();
                        f = new Phrase("");
                        f.setFont(font);
                        cell.addElement(f);
                        cell.setBorder(0);
                        pt.addCell(cell);
                        cell = new PdfPCell();
                        f = new Phrase("");
                        f.setFont(font);
                        cell.addElement(f);
                        cell.setBorder(0);
                        pt.addCell(cell);
                    }
                    j++;
                    font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
                    cell = new PdfPCell();
                    f = new Phrase(c.getCat());
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
                    f = new Phrase("Nome");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("e-Mail");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("Username");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("Password");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    cell = new PdfPCell();
                    f = new Phrase("Descrizione");
                    f.setFont(font);
                    cell.addElement(f);
                    cell.setBorder(0);
                    pt.addCell(cell);
                    for (Account a : c.getListAcc()) {
                        font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
                        cell = new PdfPCell();
                        f = new Phrase(a.getName());
                        f.setFont(font);
                        cell.addElement(f);
                        pt.addCell(cell);
                        if (a.getList().size() == 0) {
                            font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
                            cell = new PdfPCell();
                            f = new Phrase("");
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                            cell = new PdfPCell();
                            f = new Phrase("");
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                            cell = new PdfPCell();
                            f = new Phrase("");
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                            f = new Phrase("");
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                        }
                        int i = 0;
                        for (AccountElement ae : a.getList()) {
                            if (i != 0) {
                                font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
                                cell = new PdfPCell();
                                f = new Phrase("\"");
                                f.setFont(font);
                                cell.addElement(f);
                                pt.addCell(cell);
                            }
                            font = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
                            cell = new PdfPCell();
                            f = new Phrase(ae.getEmail());
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                            cell = new PdfPCell();
                            f = new Phrase(ae.getUser());
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                            cell = new PdfPCell();
                            f = new Phrase(ae.getPassword());
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                            cell = new PdfPCell();
                            f = new Phrase(ae.getDescription());
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                            i++;
                        }
                    }
                    doc.add(pt);
                }
                if (b)
                    notifyUser(Html.fromHtml("PDF creato in <b>" + path + "Lista account di " + usr.getUser() + sdf.format(Calendar.getInstance().getTime()) + "Orizzontale.pdf</b>", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                else
                    notifyUser(Html.fromHtml("PDF creato in <b>" + path + "Lista account di " + usr.getUser() + sdf.format(Calendar.getInstance().getTime()) + "Verticale.pdf</b>", HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
            } catch (Exception ignored) {
            } finally {
                doc.close();
            }
        } catch (Exception ignored) {
        }
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

    public ArrayList<Account> decreasing(ArrayList<Account> list) {
        Collections.sort(list, new Comparator<Account>() {
            @Override
            public int compare(Account lhs, Account rhs) {
                return rhs.getName().toLowerCase().compareTo(lhs.getName().toLowerCase());
            }
        });
        return list;
    }

    public ArrayList<Category> increasingCat(ArrayList<Category> list) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category lhs, Category rhs) {
                return lhs.getCat().toLowerCase().compareTo(rhs.getCat().toLowerCase());
            }
        });
        return list;
    }

    public ArrayList<Category> decreasingCat(ArrayList<Category> list) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category lhs, Category rhs) {
                return rhs.getCat().toLowerCase().compareTo(lhs.getCat().toLowerCase());
            }
        });
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRestart() {
        super.onRestart();
        layoutSettingActivity.setVisibility(View.INVISIBLE);
        BiometricManager biometricManager = BiometricManager.from(SettingActivity.this);
        if (usr.getFinger() && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
            biometricAuthentication(layoutSettingActivity);
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
                layoutSettingActivity.setVisibility(View.VISIBLE);
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
        View parent = layoutSettingActivity.getRootView();
        popupWindowCheck.showAtLocation(parent, Gravity.CENTER, 0, 0);
        final EditText popupText = popupViewCheck.findViewById(R.id.passSecurityEditText);
        Button conf = popupViewCheck.findViewById(R.id.confirmation);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.colorAccent)));
                if (popupText.getText().toString().isEmpty()) {
                    popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
                    notifyUser("Il campo password è vuoto");
                } else {
                    if (popupText.getText().toString().equals(usr.getPassword())) {
                        layoutSettingActivity.setVisibility(View.VISIBLE);
                        attempts = 3;
                        popupWindowCheck.dismiss();
                    } else {
                        popupText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
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
package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SettingActivity extends AppCompatActivity {
    private CoordinatorLayout cl;
    private ManageUser mngUsr;
    private User usr;
    private ArrayList<User> listUser = new ArrayList<>();
    private ManageApp mngApp;
    private LogApp log;
    private ManageAccount mngAcc;
    private ArrayList<Account> listAccount;
    private TextView setting;
    private TextView setting2;
    private String path;
    private File dir;
    private ImageButton showPass;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final Toolbar toolbar = findViewById(R.id.settingToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        cl = findViewById(R.id.settingActivityLay);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            mngAcc = new ManageAccount();
            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());
            Button prof = findViewById(R.id.profile);
            Button pdf = findViewById(R.id.pdf);
            Button delProf = findViewById(R.id.deleteProfile);
            setting = findViewById(R.id.settingText);
            setting.setText("Impostazioni");
            setting2 = findViewById(R.id.settingTextToolbar);
            setting2.setText("Impostazioni");
            setting2.setVisibility(View.INVISIBLE);
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
                        setting2.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        setting2.setVisibility(View.INVISIBLE);
                    }
                }
            });
            prof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password, (ViewGroup) findViewById(R.id.passSecurityPopup));
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    //noinspection deprecation
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    View parent = cl.getRootView();
                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText et = popupView.findViewById(R.id.passSecurityEditText);
                    Button conf = popupView.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.colorAccent)));
                            if (et.getText().toString().equals(usr.getPassword())) {
                                goToProfileActivity(usr);
                                popupWindow.dismiss();
                            } else {
                                et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
                                notifyUser("Password errata! Riprova");
                            }
                        }
                    });
                    showPass = popupView.findViewById(R.id.showPass);
                    showPass(et, showPass);
                }
            });
            pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password, (ViewGroup) findViewById(R.id.passSecurityPopup));
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    //noinspection deprecation
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    View parent = cl.getRootView();
                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText et = popupView.findViewById(R.id.passSecurityEditText);
                    Button conf = popupView.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.colorAccent)));
                            if (et.getText().toString().equals(usr.getPassword())) {
                                popupWindow.dismiss();
                                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_pdf, (ViewGroup) findViewById(R.id.popupPdf));
                                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                popupWindow.setOutsideTouchable(true);
                                popupWindow.setFocusable(true);
                                //noinspection deprecation
                                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                                View parent = cl.getRootView();
                                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                                Button increasing = popupView.findViewById(R.id.increasing);
                                final Button decreasing = popupView.findViewById(R.id.decreasing);
                                increasing.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PDF_Accounts/";
                                        dir = new File(path);
                                        if (!dir.exists()) dir.mkdirs();
                                        createPDF(usr, increasing(listAccount), path, dir);
                                        popupWindow.dismiss();
                                    }
                                });
                                decreasing.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PDF_Accounts/";
                                        dir = new File(path);
                                        if (!dir.exists()) dir.mkdirs();
                                        createPDF(usr, decreasing(listAccount), path, dir);
                                        popupWindow.dismiss();
                                    }
                                });
                            } else {
                                et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
                                notifyUser("Password errata! Riprova");
                            }
                        }
                    });
                    showPass = popupView.findViewById(R.id.showPass);
                    showPass(et, showPass);
                }
            });
            delProf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security_password, (ViewGroup) findViewById(R.id.passSecurityPopup));
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    //noinspection deprecation
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    View parent = cl.getRootView();
                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    final EditText et = popupView.findViewById(R.id.passSecurityEditText);
                    Button conf = popupView.findViewById(R.id.confirmation);
                    conf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.colorAccent)));
                            if (et.getText().toString().equals(usr.getPassword())) {
                                log = new LogApp();
                                mngApp.serializationFlag(SettingActivity.this, log);
                                listUser.remove(usr);
                                mngUsr.serializationListUser(SettingActivity.this, listUser);
                                mngAcc.removeFileAccount(SettingActivity.this, usr.getUser());
                                goToMainActivity();
                                popupWindow.dismiss();
                            } else {
                                et.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SettingActivity.this, R.color.errorEditText)));
                                notifyUser("Password errata! Riprova");
                            }
                        }
                    });
                    showPass = popupView.findViewById(R.id.showPass);
                    showPass(et, showPass);
                }
            });
        } else {
            notifyUser("Utente non rilevato. Impossibile aprire le impostazioni.");
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

    public void goToViewActivity() {
        Intent intent = new Intent(SettingActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
    }

    public void goToProfileActivity(User usr) {
        Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public void onBackPressed() {
        goToViewActivity();
    }

    public void createPDF(User usr, ArrayList<Account> listAccount, String path, File dir) {
        Document doc = new Document(PageSize.A4.rotate());
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            File file = new File(dir, "Lista account di " + usr.getUser() + sdf.format(Calendar.getInstance().getTime()) + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);
            doc.open();
            try {
                PdfPTable pt = new PdfPTable(5);
                pt.setWidthPercentage(100);
                float[] fl = new float[]{10, 25, 20, 15, 30};
                pt.setWidths(fl);
                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
                Phrase f;
                PdfPCell cell = new PdfPCell();
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
                for (Account a : listAccount) {
                    font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
                    cell = new PdfPCell();
                    f = new Phrase(a.getName());
                    f.setFont(font);
                    cell.addElement(f);
                    pt.addCell(cell);
                    if (a.getList().size() == 0) {
                        font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
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
                            font = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
                            cell = new PdfPCell();
                            f = new Phrase("\"");
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                        }
                        font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
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
                        f = new Phrase(ae.getDescription());
                        f.setFont(font);
                        cell.addElement(f);
                        pt.addCell(cell);
                        i++;
                    }
                }
                doc.add(pt);
                notifyUser("PDF creato in " + path + "Lista account di " + usr.getUser() + sdf.format(Calendar.getInstance().getTime()) + ".pdf");
            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } finally {
                doc.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    AppBarLayout appBar;
    ManageUser mngUsr;
    ArrayList<User> listUser = new ArrayList<>();
    private TextView setting;
    private TextView setting2;
    private ManageApp mngApp;
    private LogApp log;
    private ArrayList<Account> listAccount;
    private ManageAccount mngAcc;
    private User usr;

    private PdfPCell cell;
    private String path;
    private File dir;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final Toolbar toolbar = findViewById(R.id.toolbarSetting);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

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
            Button esportAcc = findViewById(R.id.esportAccount);
            Button delProf = findViewById(R.id.deleteProfile);

            setting = findViewById(R.id.setting);
            setting.setText(getResources().getString(R.string.impostazioni));

            setting2 = findViewById(R.id.settingToolbar);
            setting2.setText(getResources().getString(R.string.impostazioni));
            setting2.setVisibility(View.INVISIBLE);

            appBar = findViewById(R.id.app_bar_setting);
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
                    goToProfileActivity(usr);
                }
            });

            pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PDF_Accounts/";
                    dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    try {
                        createPDF(usr, listAccount, path, dir);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            });

            esportAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToExportActivity(usr);
                }
            });


            delProf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    log = new LogApp();
                    mngApp.serializationFlag(SettingActivity.this, log);
                    listUser.remove(usr);
                    mngUsr.serializationListUser(SettingActivity.this, listUser);
                    mngAcc.removeFileAccount(SettingActivity.this, usr.getUser());
                    goToMainActivity();
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

    public void goToExportActivity(User usr){
        Intent intent = new Intent(SettingActivity.this, ExportActivity.class);
        intent.putExtra("owner", usr);
        startActivity(intent);
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

    public void createPDF(User usr, ArrayList<Account> listAccount, String path, File dir) throws FileNotFoundException, DocumentException {
        Document doc = new Document();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            file = new File(dir, "Lista account di " + usr.getUser() + sdf.format(Calendar.getInstance().getTime()) + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            doc.open();
            try {
                PdfPTable pt = new PdfPTable(4);
                pt.setWidthPercentage(100);
                float[] fl = new float[]{25, 25, 25, 25};
                pt.setWidths(fl);
                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
                Phrase f;

                cell = new PdfPCell();
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

                int i = 0;
                for (Account a : listAccount) {
                    font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

                    cell = new PdfPCell();
                    f = new Phrase(a.getName());
                    f.setFont(font);
                    cell.addElement(f);
                    pt.addCell(cell);

                    if (a.getList().size() == 0) {
                        font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);

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
                    }

                    i = 0;
                    for (AccountElement ae : a.getList()) {
                        if (i != 0) {
                            font = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);

                            cell = new PdfPCell();
                            f = new Phrase("\"");
                            f.setFont(font);
                            cell.addElement(f);
                            pt.addCell(cell);
                        }
                        font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);

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

}
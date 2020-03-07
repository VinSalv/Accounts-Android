package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public class CustomizeActivity extends AppCompatActivity {
    private ManageUser mngUsr;
    private User usr;
    private ArrayList<User> listUser;
    private TextView customize;
    private TextView customize2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        Toolbar toolbar = findViewById(R.id.customizeToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        customize = findViewById(R.id.customizeText);
        customize2 = findViewById(R.id.customizeTextToolbar);
        customize2.setVisibility(View.INVISIBLE);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            AppBarLayout appBar = findViewById(R.id.customizeBarToolbar);
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    customize.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true;
                        customize2.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        customize2.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
            ArrayList<String> numColumnCat = new ArrayList<>();
            numColumnCat.add("1");
            numColumnCat.add("2");
            ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numColumnCat);
            adapterCat.setDropDownViewResource(R.layout.spinner_item);
            spinnerCategory.setAdapter(adapterCat);
            spinnerCategory.setSelection(usr.getColCat() - 1);
            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String num = parent.getItemAtPosition(position).toString();
                    listUser.remove(usr);
                    usr.setColCat(Integer.parseInt(num));
                    listUser.add(usr);
                    mngUsr.serializationListUser(CustomizeActivity.this, listUser);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            Spinner spinnerAccount = findViewById(R.id.spinnerAccount);
            ArrayList<String> numColumnAcc = new ArrayList<>();
            numColumnAcc.add("1");
            numColumnAcc.add("2");
            ArrayAdapter<String> adapterAcc = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numColumnAcc);
            adapterAcc.setDropDownViewResource(R.layout.spinner_item);
            spinnerAccount.setAdapter(adapterAcc);
            spinnerAccount.setSelection(usr.getColAcc() - 1);
            spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String num = parent.getItemAtPosition(position).toString();
                    listUser.remove(usr);
                    usr.setColAcc(Integer.parseInt(num));
                    listUser.add(usr);
                    mngUsr.serializationListUser(CustomizeActivity.this, listUser);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            notifyUser("Utente non rilevato. Impossibile settare i parametri di autenticazione.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(CustomizeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToSettingActivity() {
        Intent intent = new Intent(CustomizeActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public void onBackPressed() {
        goToSettingActivity();
    }
}

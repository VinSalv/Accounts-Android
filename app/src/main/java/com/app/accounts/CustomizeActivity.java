package com.app.accounts;

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
    private ArrayList<User> listUser;
    private User usr;
    private TextView customize;
    private TextView customizeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        Toolbar toolbar = findViewById(R.id.customizeToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, ((User) Objects.requireNonNull((Objects.requireNonNull(getIntent().getExtras())).get("owner"))).getUser());
        if (usr != null) {
            customize = findViewById(R.id.customizeText);
            customizeToolbar = findViewById(R.id.customizeTextToolbar);
            customizeToolbar.setVisibility(View.INVISIBLE);
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
                        customizeToolbar.setVisibility(View.VISIBLE);
                    } else if (isShow) {
                        isShow = false;
                        customizeToolbar.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
            ArrayList<String> numberColumnCategory = new ArrayList<>();
            numberColumnCategory.add("1");
            numberColumnCategory.add("2");
            ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numberColumnCategory);
            adapterCategory.setDropDownViewResource(R.layout.spinner_item);
            spinnerCategory.setAdapter(adapterCategory);
            spinnerCategory.setSelection(usr.getColCat() - 1);
            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String number = parent.getItemAtPosition(position).toString();
                    listUser.remove(usr);
                    usr.setColCat(Integer.parseInt(number));
                    listUser.add(usr);
                    mngUsr.serializationListUser(CustomizeActivity.this, listUser);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            Spinner spinnerAccount = findViewById(R.id.spinnerAccount);
            ArrayList<String> numberColumnAccount = new ArrayList<>();
            numberColumnAccount.add("1");
            numberColumnAccount.add("2");
            ArrayAdapter<String> adapterAccount = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numberColumnAccount);
            adapterAccount.setDropDownViewResource(R.layout.spinner_item);
            spinnerAccount.setAdapter(adapterAccount);
            spinnerAccount.setSelection(usr.getColAcc() - 1);
            spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String number = parent.getItemAtPosition(position).toString();
                    listUser.remove(usr);
                    usr.setColAcc(Integer.parseInt(number));
                    listUser.add(usr);
                    mngUsr.serializationListUser(CustomizeActivity.this, listUser);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            notifyUser("Credenziali non rilevate. Impossibile effettuare la personalizzazione.");
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
        finish();
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

package com.example.accounts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SignActivity extends AppCompatActivity {
    private RelativeLayout lay;
    private EditText userEdit;
    private ImageView userError;
    private EditText passEdit;
    private ImageView passError;
    private EditText passEdit2;
    private ImageView passError2;
    private Button sign;
    private Switch flagFinger;
    private String path;
    private ManageApp mngApp;
    private ManageUser mngUsr;
    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Toolbar toolbarSign = findViewById(R.id.toolbarSign);
        setSupportActionBar(toolbarSign);

        lay = findViewById(R.id.relLaySign);
        path = getIntent().getExtras().getString("path");

        userEdit = findViewById(R.id.usernameEdit);
        userError = findViewById(R.id.errorUsername);
        passEdit = findViewById(R.id.passwordEdit);
        passError = findViewById(R.id.errorPassword);
        passEdit2 = findViewById(R.id.passwordEdit2);
        passError2 = findViewById(R.id.errorPassword2);
        flagFinger = findViewById(R.id.flagFinger);
        sign = findViewById(R.id.signButton);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userError.setVisibility(View.INVISIBLE);
                passError.setVisibility(View.INVISIBLE);
                passError2.setVisibility(View.INVISIBLE);

                User u = new User(userEdit.getText().toString(), passEdit.getText().toString(), false, flagFinger.isChecked());
                mngUsr = new ManageUser();

                listUser = mngUsr.deserializationListUser(path);

                if (!fieldCheck(u)) return;
                if (!passEdit.getText().toString().equals(passEdit2.getText().toString())) {
                    passError2.setVisibility(View.VISIBLE);
                    return;
                }
                if (!mngUsr.search(u, listUser)) {
                    listUser.add(u);
                    mngUsr.serializationListUser(listUser, path);
                    Intent intent = new Intent(SignActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("path", path);
                    startActivity(intent);
                    finish();
                } else {
                    userError.setVisibility(View.VISIBLE);
                    Snackbar.make(view, "User già esistente", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public boolean fieldCheck(User usr) {
        if (!isValidWord(usr.getUser()) && !isValidWord(usr.getPassword())) {
            userError.setVisibility(View.VISIBLE);
            passError.setVisibility(View.VISIBLE);
            Snackbar.make(lay, "Campi Utente e Password non validi !!!", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (!isValidWord(usr.getUser())) {
            userError.setVisibility(View.VISIBLE);
            Snackbar.make(lay, "Campo Utente non valido !!!", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (!isValidWord(usr.getPassword())) {
            passError.setVisibility(View.VISIBLE);
            Snackbar.make(lay, "Campo Password non valido !!!", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean isValidWord(String word) {
        return ((word.matches("[A-Za-z0-9?!_.-]*")) && (!word.isEmpty()));
    }

}

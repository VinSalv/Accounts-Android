package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class SignActivity extends AppCompatActivity {
    private EditText userEdit;
    private ImageView userError;
    private EditText passEdit;
    private ImageView passError;
    private EditText passEdit2;
    private ImageView passError2;
    private Button sign;
    private Switch flagFinger;
    private String path;
    private ManageUser mngUsr;
    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Toolbar toolbarSign = findViewById(R.id.toolbarSign);
        setSupportActionBar(toolbarSign);

        path = getIntent().getExtras().getString("path");
        userEdit = findViewById(R.id.usernameEdit);
        userError = findViewById(R.id.errorUsername);
        passEdit = findViewById(R.id.passwordEdit);
        passError = findViewById(R.id.errorPassword);
        passEdit2 = findViewById(R.id.passwordEdit2);
        passError2 = findViewById(R.id.errorPassword2);
        flagFinger = findViewById(R.id.flagFinger);
        sign = findViewById(R.id.signButton);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(path);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userError.setVisibility(View.INVISIBLE);
                passError.setVisibility(View.INVISIBLE);
                passError2.setVisibility(View.INVISIBLE);

                User usr = new User(userEdit.getText().toString(), passEdit.getText().toString(), flagFinger.isChecked());

                if (!fieldCheck(usr)) return;
                if (!passEdit.getText().toString().equals(passEdit2.getText().toString())) {
                    passError2.setVisibility(View.VISIBLE);
                    notifyUser("Le password non corrispondono");
                    return;
                }
                if (!mngUsr.search(usr, listUser)) {
                    listUser.add(usr);
                    mngUsr.serializationListUser(listUser, path);
                    Intent intent = new Intent(SignActivity.this, ViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("path", path);
                    intent.putExtra("owner", usr.getUser());
                    startActivity(intent);
                    finish();
                } else {
                    userError.setVisibility(View.VISIBLE);
                    notifyUser("User già esistente");
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
            notifyUser("Campi Utente e Password non validi !!!");
            return false;
        } else if (!isValidWord(usr.getUser())) {
            userError.setVisibility(View.VISIBLE);
            notifyUser("Campo Utente non valido !!!");
            return false;
        } else if (!isValidWord(usr.getPassword())) {
            passError.setVisibility(View.VISIBLE);
            notifyUser("Campo Password non valido !!!");
            return false;
        }
        return true;
    }

    public boolean isValidWord(String word) {
        return ((word.matches("[A-Za-z0-9?!_.-]*")) && (!word.isEmpty()));
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }
}

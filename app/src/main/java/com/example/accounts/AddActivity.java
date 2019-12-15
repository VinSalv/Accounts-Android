package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class AddActivity extends AppCompatActivity {
    private String path;
    private String owner;
    private ManageAccount mngAcc;
    private ArrayList<Account> listAccount;
    private EditText name;
    private EditText email;
    private EditText user;
    private EditText password;
    private ImageView nameError;
    private ImageView emailError;
    private ImageView userError;
    private ImageView passError;
    private Button addButton;
    private Button emptyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        path = getIntent().getExtras().getString("path");
        owner = getIntent().getExtras().getString("owner");

        mngAcc = new ManageAccount();

        listAccount = mngAcc.deserializationListAccount(path, owner);

        name = findViewById(R.id.nameAddEdit);
        email = findViewById(R.id.emailAddEdit);
        user = findViewById(R.id.userAddEdit);
        password = findViewById(R.id.passAddEdit);
        nameError = findViewById(R.id.errorAddName);
        emailError = findViewById(R.id.errorAddEmail);
        userError = findViewById(R.id.errorAddUser);
        passError = findViewById(R.id.errorAddPassword);
        addButton = findViewById(R.id.addButton);
        emptyButton = findViewById(R.id.emptyButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameError.setVisibility(View.INVISIBLE);
                userError.setVisibility(View.INVISIBLE);
                emailError.setVisibility(View.INVISIBLE);
                passError.setVisibility(View.INVISIBLE);

                Account a = new Account(owner, name.getText().toString(), email.getText().toString(), user.getText().toString(), password.getText().toString(),false);

                if (a.getName().isEmpty()) {
                    nameError.setVisibility(View.VISIBLE);
                    Toast.makeText(AddActivity.this, "Nome applicativo vuoto !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fieldError(a)) {
                    if (!mngAcc.search(a, listAccount)) {
                        listAccount.add(a);
                        mngAcc.serializationListAccount(listAccount, path, owner);
                        Intent intent = new Intent(AddActivity.this, ViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("path", path);
                        intent.putExtra("owner", owner);
                        startActivity(intent);
                        finish();
                    } else {
                        nameError.setVisibility(View.VISIBLE);
                        Toast.makeText(AddActivity.this, "Applicativo già registrato !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText("");
                email.setText("");
                user.setText("");
                password.setText("");
            }
        });
    }


    public boolean isValidWord(String word) {
        if (word.isEmpty()) return true;
        return word.matches("[^ ]*");
    }

    public boolean isValidEmail(String email) {
        if (email.isEmpty()) return true;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean fieldError(Account a) {
        if (!isValidWord(a.getUser()) && !isValidEmail(a.getEmail()) && !isValidWord(a.getPassword())) {
            userError.setVisibility(View.VISIBLE);
            emailError.setVisibility(View.VISIBLE);
            passError.setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Utente, Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidWord(a.getUser()) && !isValidWord(a.getPassword())) {
            userError.setVisibility(View.VISIBLE);
            passError.setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Utente e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidWord(a.getPassword()) && !isValidEmail(a.getEmail())) {
            passError.setVisibility(View.VISIBLE);
            emailError.setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidWord(a.getUser()) && !isValidEmail(a.getEmail())) {
            userError.setVisibility(View.VISIBLE);
            emailError.setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Utente e Email non validi !!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidWord(a.getUser())) {
            userError.setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campo Utente non valido !!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidEmail(a.getEmail())) {
            emailError.setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campo Email non valido !!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidWord(a.getPassword())) {
            passError.setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campo Password non valido !!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onBackPressed() {
        Intent intent = new Intent(AddActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("path", path);
        intent.putExtra("owner", owner);
        startActivity(intent);
        this.finish();
    }
}

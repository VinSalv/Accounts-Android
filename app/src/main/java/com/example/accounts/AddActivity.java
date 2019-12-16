package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private ArrayList<AccountElement> listElem;
    private AccountElement elem;
    private FloatingActionButton addElem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        path = getIntent().getExtras().getString("path");
        owner = getIntent().getExtras().getString("owner");

        mngAcc = new ManageAccount();

        listAccount = mngAcc.deserializationListAccount(path, owner);
        listElem = new ArrayList<AccountElement>();

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
        addElem = findViewById(R.id.addElemFloatingButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameError.setVisibility(View.INVISIBLE);
                userError.setVisibility(View.INVISIBLE);
                emailError.setVisibility(View.INVISIBLE);
                passError.setVisibility(View.INVISIBLE);

                elem = new AccountElement(email.getText().toString(), user.getText().toString(), password.getText().toString());
                listElem.add(elem);
                Account a = new Account(owner, name.getText().toString(), listElem);

                if (a.getName().isEmpty()) {
                    nameError.setVisibility(View.VISIBLE);
                    Toast.makeText(AddActivity.this, "Il campo nome non può essere vuoto !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fieldError(elem)) {
                    if (!mngAcc.search(a, listAccount)) {
                        Log.d("QUI", a.getName());
                        listAccount.add(a);
                        Log.d("QUI", listAccount.get(0).getName());
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

        addElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lin = findViewById(R.id.linLayAdd);
                LayoutInflater inflater = LayoutInflater.from(AddActivity.this);
                View view = inflater.inflate(R.layout.more_elem_add, lin, false);


                //   text.setTypeface(FontSelector.getBold(getActivity()));
                lin.addView(view);
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

    public boolean fieldError(AccountElement a) {
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

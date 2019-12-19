package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
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
    private ArrayList<EditText> email;
    private ArrayList<EditText> user;
    private ArrayList<EditText> password;
    private ImageView nameError;
    private ImageView emailError;
    private ImageView userError;
    private ImageView passError;
    private Button addButton;
    private Button emptyButton;
    private ArrayList<AccountElement> listElem;
    private AccountElement elem;
    private FloatingActionButton addElem;
    private ArrayList<RelativeLayout> layList;
    private ArrayList<ImageView> emailErrorList;
    private ArrayList<ImageView> userErrorList;
    private ArrayList<ImageView> passwordErrorList;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        path = getIntent().getExtras().getString("path");
        owner = getIntent().getExtras().getString("owner");

        mngAcc = new ManageAccount();

        listAccount = mngAcc.deserializationListAccount(path, owner);
        listElem = new ArrayList<AccountElement>();
        layList = new ArrayList<RelativeLayout>();
        email = new ArrayList<EditText>();
        user = new ArrayList<EditText>();
        password = new ArrayList<EditText>();
        emailErrorList = new ArrayList<ImageView>();
        userErrorList = new ArrayList<ImageView>();
        passwordErrorList = new ArrayList<ImageView>();
        i = 0;
        final RelativeLayout rel = (RelativeLayout) findViewById(R.id.subRelLayAdd);
        layList.add((RelativeLayout) findViewById(R.id.subRelLayAdd));

        name = findViewById(R.id.nameAddEdit);
        nameError = findViewById(R.id.errorAddName);

        email.add((EditText) findViewById(R.id.emailAddEdit));
        emailErrorList.add((ImageView) findViewById(R.id.errorAddEmail));
        user.add((EditText) findViewById(R.id.userAddEdit));
        userErrorList.add((ImageView) findViewById(R.id.errorAddUser));
        password.add((EditText) findViewById(R.id.passAddEdit));
        passwordErrorList.add((ImageView) findViewById(R.id.errorAddPassword));

        addButton = findViewById(R.id.addButton);
        emptyButton = findViewById(R.id.emptyButton);
        addElem = findViewById(R.id.addElemFloatingButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameError.setVisibility(View.INVISIBLE);
                listElem.clear();

                for (int n = 0; n <= i; n++) {
                    emailErrorList.get(n).setVisibility(View.INVISIBLE);
                    userErrorList.get(n).setVisibility(View.INVISIBLE);
                    passwordErrorList.get(n).setVisibility(View.INVISIBLE);
                    elem = new AccountElement(email.get(n).getText().toString(), user.get(n).getText().toString(), password.get(n).getText().toString());
                    listElem.add(elem);
                }

                Account a = new Account(owner, name.getText().toString(), listElem);

                if (a.getName().isEmpty()) {
                    nameError.setVisibility(View.VISIBLE);
                    Toast.makeText(AddActivity.this, "Il campo nome non può essere vuoto !", Toast.LENGTH_SHORT).show();
                    return;
                }

                int n = 0;
                for (AccountElement elem : listElem) {
                    if (fieldError(elem, n)) return;
                    n++;
                }

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
                    return;
                }
            }

        });

        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0;
                listElem.clear();
                emailErrorList.clear();
                userErrorList.clear();
                passwordErrorList.clear();
                LinearLayout lin = findViewById(R.id.linLayAdd);
                for (RelativeLayout l : layList) {
                    if (l != rel) lin.removeView(l);
                }
                layList.clear();
                findViewById(R.id.errorAddEmail).setVisibility(View.INVISIBLE);
                findViewById(R.id.errorAddUser).setVisibility(View.INVISIBLE);
                findViewById(R.id.errorAddPassword).setVisibility(View.INVISIBLE);
                ((EditText) findViewById(R.id.emailAddEdit)).setText("");
                ((EditText) findViewById(R.id.userAddEdit)).setText("");
                ((EditText) findViewById(R.id.passAddEdit)).setText("");
                ((EditText) findViewById(R.id.nameAddEdit)).setText("");
                layList.add((RelativeLayout) findViewById(R.id.subRelLayAdd));
                email.add((EditText) findViewById(R.id.emailAddEdit));
                emailErrorList.add((ImageView) findViewById(R.id.errorAddEmail));
                user.add((EditText) findViewById(R.id.userAddEdit));
                userErrorList.add((ImageView) findViewById(R.id.errorAddUser));
                password.add((EditText) findViewById(R.id.passAddEdit));
                passwordErrorList.add((ImageView) findViewById(R.id.errorAddPassword));
            }
        });

        addElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                final LinearLayout lin = findViewById(R.id.linLayAdd);
                LayoutInflater inflater = LayoutInflater.from(AddActivity.this);
                final RelativeLayout relLey = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 20);
                relLey.setLayoutParams(params);
                layList.add(relLey);
                emailErrorList.add((ImageView) relLey.findViewById(R.id.errorAddEmail));
                userErrorList.add((ImageView) relLey.findViewById(R.id.errorAddUser));
                passwordErrorList.add((ImageView) relLey.findViewById(R.id.errorAddPassword));
                email.add((EditText) relLey.findViewById(R.id.emailAddEdit));
                user.add((EditText) relLey.findViewById(R.id.userAddEdit));
                password.add((EditText) relLey.findViewById(R.id.passAddEdit));
                Button del = relLey.findViewById(R.id.deleteAddLay);
                lin.addView(relLey);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        i--;
                        layList.remove(relLey);
                        lin.removeView(relLey);
                    }
                });
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

    public boolean fieldError(AccountElement a, int n) {
        if (!isValidWord(a.getUser()) && !isValidEmail(a.getEmail()) && !isValidWord(a.getPassword())) {
            userErrorList.get(n).setVisibility(View.VISIBLE);
            emailErrorList.get(n).setVisibility(View.VISIBLE);
            passwordErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Utente, Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getUser()) && !isValidWord(a.getPassword())) {
            userErrorList.get(n).setVisibility(View.VISIBLE);
            passwordErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Utente e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getPassword()) && !isValidEmail(a.getEmail())) {
            passwordErrorList.get(n).setVisibility(View.VISIBLE);
            emailErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getUser()) && !isValidEmail(a.getEmail())) {
            userErrorList.get(n).setVisibility(View.VISIBLE);
            emailErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campi Utente e Email non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getUser())) {
            userErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campo Utente non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidEmail(a.getEmail())) {
            emailErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campo Email non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getPassword())) {
            passwordErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(AddActivity.this, "Campo Password non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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

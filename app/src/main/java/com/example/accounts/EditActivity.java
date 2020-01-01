package com.example.accounts;

import android.annotation.SuppressLint;
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
import java.util.Objects;

public class EditActivity extends AppCompatActivity {
    private String owner;
    private ManageAccount mngAcc;
    private ArrayList<Account> listAccount;
    private EditText name;
    private ArrayList<EditText> email;
    private ArrayList<EditText> user;
    private ArrayList<EditText> password;
    private ImageView nameError;
    Button addButton;
    Button emptyButton;
    private ArrayList<AccountElement> listElem;
    private AccountElement elem;
    FloatingActionButton addElem;
    private ArrayList<RelativeLayout> layList;
    private ArrayList<ImageView> emailErrorList;
    private ArrayList<ImageView> userErrorList;
    private ArrayList<ImageView> passwordErrorList;
    private int i;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        account = (Account) Objects.requireNonNull(getIntent().getExtras()).get("account");
        owner = getIntent().getExtras().getString("owner");

        mngAcc = new ManageAccount();

        listAccount = mngAcc.deserializationListAccount(this, owner);
        listElem = new ArrayList<>();
        layList = new ArrayList<>();
        email = new ArrayList<>();
        user = new ArrayList<>();
        password = new ArrayList<>();
        emailErrorList = new ArrayList<>();
        userErrorList = new ArrayList<>();
        passwordErrorList = new ArrayList<>();
        i = 0;
        addButton = findViewById(R.id.addButton);
        emptyButton = findViewById(R.id.emptyButton);
        addElem = findViewById(R.id.addElemFloatingButton);
        name = findViewById(R.id.nameAddEdit);
        name.setText(account.getName());
        nameError = findViewById(R.id.errorAddName);

        for (AccountElement ae : account.getList()) {
            final LinearLayout lin = findViewById(R.id.linLayAdd);
            LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
            @SuppressLint("InflateParams") final RelativeLayout relLey = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 0, 0);
            relLey.setLayoutParams(params);
            layList.add(relLey);
            emailErrorList.add((ImageView) relLey.findViewById(R.id.errorAddEmail));
            userErrorList.add((ImageView) relLey.findViewById(R.id.errorAddUser));
            passwordErrorList.add((ImageView) relLey.findViewById(R.id.errorAddPassword));
            email.add((EditText) relLey.findViewById(R.id.emailAddEdit));
            email.get(i).setText(ae.getEmail());
            user.add((EditText) relLey.findViewById(R.id.userAddEdit));
            user.get(i).setText(ae.getUser());
            password.add((EditText) relLey.findViewById(R.id.passAddEdit));
            password.get(i).setText(ae.getPassword());
            final Button del = relLey.findViewById(R.id.deleteAddLay);
            del.setTag(i);
            lin.addView(relLey);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i--;
                    layList.remove(relLey);
                    lin.removeView(relLey);
                    email.remove(email.get(Integer.parseInt(del.getTag().toString())));
                    user.remove(user.get(Integer.parseInt(del.getTag().toString())));
                    password.remove(password.get(Integer.parseInt(del.getTag().toString())));
                }
            });
            i++;
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameError.setVisibility(View.INVISIBLE);
                listElem.clear();

                for (int n = 0; n < i; n++) {
                    emailErrorList.get(n).setVisibility(View.INVISIBLE);
                    userErrorList.get(n).setVisibility(View.INVISIBLE);
                    passwordErrorList.get(n).setVisibility(View.INVISIBLE);
                    if (!(email.get(n).getText().toString().isEmpty() && user.get(n).getText().toString().isEmpty() && password.get(n).getText().toString().isEmpty())) {
                        elem = new AccountElement(email.get(n).getText().toString(), user.get(n).getText().toString(), password.get(n).getText().toString());
                        listElem.add(elem);
                    }
                }

                Account a = new Account(owner, name.getText().toString(), listElem);
                Account a2 = new Account(owner, account.getName(), (ArrayList<AccountElement>) account.getList());

                if (a.getName().isEmpty()) {
                    nameError.setVisibility(View.VISIBLE);
                    Toast.makeText(EditActivity.this, "Il campo nome non può essere vuoto !", Toast.LENGTH_SHORT).show();
                    return;
                }

                int n = 0;
                for (AccountElement elem : listElem) {
                    if (fieldError(elem, n)) return;
                    n++;
                }

                if (!mngAcc.search(a, listAccount) || a.equals(account.getName())) {
                    listAccount.remove(a2);
                    listAccount.add(a);
                    mngAcc.serializationListAccount(EditActivity.this, listAccount, owner);
                    Intent intent = new Intent(EditActivity.this, ViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("owner", owner);
                    startActivity(intent);
                    finish();
                } else {
                    nameError.setVisibility(View.VISIBLE);
                    Toast.makeText(EditActivity.this, "Applicativo già registrato !!!", Toast.LENGTH_SHORT).show();
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
                    lin.removeView(l);
                }
                layList.clear();
                email.clear();
                user.clear();
                password.clear();
                moreElem(v);
            }
        });

        addElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreElem(v);
            }
        });

    }

    public void moreElem(View v) {
        i++;
        final LinearLayout lin = findViewById(R.id.linLayAdd);
        LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
        @SuppressLint("InflateParams") final RelativeLayout relLey = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 0);
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
            Toast.makeText(EditActivity.this, "Campi Utente, Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getUser()) && !isValidWord(a.getPassword())) {
            userErrorList.get(n).setVisibility(View.VISIBLE);
            passwordErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(EditActivity.this, "Campi Utente e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getPassword()) && !isValidEmail(a.getEmail())) {
            passwordErrorList.get(n).setVisibility(View.VISIBLE);
            emailErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(EditActivity.this, "Campi Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getUser()) && !isValidEmail(a.getEmail())) {
            userErrorList.get(n).setVisibility(View.VISIBLE);
            emailErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(EditActivity.this, "Campi Utente e Email non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getUser())) {
            userErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(EditActivity.this, "Campo Utente non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidEmail(a.getEmail())) {
            emailErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(EditActivity.this, "Campo Email non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!isValidWord(a.getPassword())) {
            passwordErrorList.get(n).setVisibility(View.VISIBLE);
            Toast.makeText(EditActivity.this, "Campo Password non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void onBackPressed() {
        Intent intent = new Intent(EditActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", owner);
        startActivity(intent);
        this.finish();
    }
}

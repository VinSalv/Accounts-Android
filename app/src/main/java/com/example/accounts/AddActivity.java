package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
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
        email = new ArrayList<EditText>();
        user = new ArrayList<EditText>();
        password = new ArrayList<EditText>();
        i = 0;
        name = findViewById(R.id.nameAddEdit);
        EditText editText = findViewById(R.id.emailAddEdit);
        email.add(editText);
        editText = findViewById(R.id.userAddEdit);
        user.add(editText);
        editText = findViewById(R.id.passAddEdit);
        password.add(editText);
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

                elem = new AccountElement(email.get(0).getText().toString(), user.get(0).getText().toString(), password.get(0).getText().toString());
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

            }
        });

        addElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                LinearLayout lin = findViewById(R.id.linLayAdd);

                RelativeLayout relLay = new RelativeLayout(AddActivity.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 20);
                relLay.setLayoutParams(params);
                relLay.setBackground(getDrawable(R.drawable.rounded_color));

                RelativeLayout.LayoutParams paramsEmail = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                EditText emailAddEdit = (EditText) getLayoutInflater().inflate(R.layout.add_element, null);
                emailAddEdit.setTag("email" + i);
                emailAddEdit.setId(R.id.emailAddEdit);
                emailAddEdit.setHint(getResources().getString(R.string.emailAddHint));
                emailAddEdit.setTextSize(16);
                emailAddEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                paramsEmail.setMargins(85, 0, 0, 55);
                emailAddEdit.setLayoutParams(paramsEmail);
                relLay.addView(emailAddEdit);

                RelativeLayout.LayoutParams paramsEmailImage = new RelativeLayout.LayoutParams(
                        55,
                        55);
                ImageView emailImage = new ImageView(AddActivity.this);
                emailImage.setImageDrawable(getDrawable(R.drawable.email));
                paramsEmailImage.addRule(RelativeLayout.ALIGN_BOTTOM, emailAddEdit.getId());
                paramsEmailImage.setMargins(0, 0, 0, 15);
                emailImage.setLayoutParams(paramsEmailImage);
                relLay.addView(emailImage);

                RelativeLayout.LayoutParams paramsUser = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                EditText userAddEdit = (EditText) getLayoutInflater().inflate(R.layout.add_element, null);
                userAddEdit.setTag("user" + i);
                userAddEdit.setId(R.id.userAddEdit);
                userAddEdit.setHint(getResources().getString(R.string.userAddHint));
                userAddEdit.setTextSize(16);
                userAddEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                paramsUser.addRule(RelativeLayout.BELOW, emailAddEdit.getId());
                paramsUser.setMargins(85, 0, 0, 55);
                userAddEdit.setLayoutParams(paramsUser);
                relLay.addView(userAddEdit);

                RelativeLayout.LayoutParams paramsUserImage = new RelativeLayout.LayoutParams(
                        55,
                        55);
                ImageView userImage = new ImageView(AddActivity.this);
                userImage.setImageDrawable(getDrawable(R.drawable.user));
                paramsUserImage.addRule(RelativeLayout.ALIGN_BOTTOM, userAddEdit.getId());
                paramsUserImage.setMargins(0, 0, 0, 15);
                userImage.setLayoutParams(paramsUserImage);
                relLay.addView(userImage);

                RelativeLayout.LayoutParams paramsPass = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                EditText passAddEdit = (EditText) getLayoutInflater().inflate(R.layout.add_element, null);
                passAddEdit.setTag("pass" + i);
                passAddEdit.setId(R.id.passAddEdit);
                passAddEdit.setHint(getResources().getString(R.string.passAddHint));
                passAddEdit.setTextSize(16);
                passAddEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                paramsPass.addRule(RelativeLayout.BELOW, userAddEdit.getId());
                paramsPass.setMargins(85, 0, 0, 0);
                passAddEdit.setLayoutParams(paramsPass);
                relLay.addView(passAddEdit);

                RelativeLayout.LayoutParams paramsPassImage = new RelativeLayout.LayoutParams(
                        55,
                        55);
                ImageView passImage = new ImageView(AddActivity.this);
                passImage.setImageDrawable(getDrawable(R.drawable.pass));
                paramsPassImage.addRule(RelativeLayout.ALIGN_BOTTOM, passAddEdit.getId());
                paramsPassImage.setMargins(0, 0, 0, 15);
                passImage.setLayoutParams(paramsPassImage);
                relLay.addView(passImage);

                lin.addView(relLay);
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

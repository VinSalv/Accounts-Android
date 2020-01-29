package com.example.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "SuspiciousMethodCalls"})
public class AddActivity extends AppCompatActivity {
    private RelativeLayout rl;
    private LinearLayout ll;
    private ArrayList<RelativeLayout> relativeLayoutsList;
    private User owner;
    private ManageAccount mngAcc;
    private ArrayList<Account> listAccount;
    private AccountElement elem;
    private ArrayList<AccountElement> accountElementsList;
    private EditText name;
    private ArrayList<EditText> email;
    private ArrayList<EditText> user;
    private ArrayList<EditText> password;
    private ArrayList<EditText> description;
    private ArrayList<ImageButton> showPass;
    private int i;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar addToolbar = findViewById(R.id.addToolbar);
        owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        addToolbar.setSubtitle("Aggiungi account alla lista di " + Objects.requireNonNull(owner).getUser());
        setSupportActionBar(addToolbar);
        ll = findViewById(R.id.linearLayAdd);
        rl = findViewById(R.id.subRelativeLayAdd);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        User usr = mngUsr.findUser(listUser, owner.getUser());
        if (usr != null) {
            mngAcc = new ManageAccount();
            listAccount = mngAcc.deserializationListAccount(this, Objects.requireNonNull(owner).getUser());
            accountElementsList = new ArrayList<>();
            relativeLayoutsList = new ArrayList<>();
            email = new ArrayList<>();
            user = new ArrayList<>();
            password = new ArrayList<>();
            description = new ArrayList<>();
            showPass = new ArrayList<>();
            i = 0;
            relativeLayoutsList.add(rl);
            name = findViewById(R.id.nameAddEdit);
            email.add((EditText) findViewById(R.id.emailAddEdit));
            user.add((EditText) findViewById(R.id.userAddEdit));
            password.add((EditText) findViewById(R.id.passAddEdit));
            description.add((EditText) findViewById(R.id.descriptionAddEdit));
            showPass.add((ImageButton) findViewById(R.id.showPassImage));
            Button addButton = findViewById(R.id.addButton);
            Button emptyButton = findViewById(R.id.emptyButton);
            FloatingActionButton addElem = findViewById(R.id.addElemFloatingButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                    accountElementsList.clear();
                    for (int n = 0; n <= i; n++) {
                        email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        if (!(email.get(n).getText().toString().isEmpty() && user.get(n).getText().toString().isEmpty() && password.get(n).getText().toString().isEmpty() && description.get(n).getText().toString().isEmpty())) {
                            elem = new AccountElement(email.get(n).getText().toString(), user.get(n).getText().toString(), password.get(n).getText().toString(), description.get(n).getText().toString());
                            accountElementsList.add(elem);
                        }
                    }
                    Account acc = new Account(name.getText().toString(), accountElementsList);
                    if (acc.getName().isEmpty()) {
                        name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
                        notifyUser("Associare il nome all'account!");
                        return;
                    }
                    int n = 0;
                    for (AccountElement elem : accountElementsList) {
                        if (fieldError(elem, n)) return;
                        n++;
                    }
                    if (mngAcc.notFind(acc, listAccount)) {
                        listAccount.add(acc);
                        mngAcc.serializationListAccount(AddActivity.this, listAccount, owner.getUser());
                        goToViewActivity(owner);
                    } else {
                        name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
                        notifyUser("Applicativo già registrato!");
                    }
                }

            });
            emptyButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    //noinspection deprecation
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    View parent = rl.getRootView();
                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    TextView et = popupView.findViewById(R.id.securityText);
                    et.setText("Sei sicuro di voler resettare tutti i campi?");
                    Button yes = popupView.findViewById(R.id.yes);
                    Button no = popupView.findViewById(R.id.no);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            i = 0;
                            accountElementsList.clear();
                            for (RelativeLayout relativeLayout : relativeLayoutsList) {
                                if (relativeLayout != rl)
                                    ll.removeView(relativeLayout);
                            }
                            relativeLayoutsList.clear();
                            ((EditText) findViewById(R.id.nameAddEdit)).setText("");
                            findViewById(R.id.emailAddEdit).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                            ((EditText) findViewById(R.id.emailAddEdit)).setText("");
                            findViewById(R.id.userAddEdit).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                            ((EditText) findViewById(R.id.userAddEdit)).setText("");
                            findViewById(R.id.passAddEdit).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                            ((EditText) findViewById(R.id.passAddEdit)).setText("");
                            ((EditText) findViewById(R.id.descriptionAddEdit)).setText("");
                            relativeLayoutsList.add(rl);
                            email.add((EditText) findViewById(R.id.emailAddEdit));
                            user.add((EditText) findViewById(R.id.userAddEdit));
                            password.add((EditText) findViewById(R.id.passAddEdit));
                            description.add((EditText) findViewById(R.id.descriptionAddEdit));
                            showPass.add((ImageButton) findViewById(R.id.showPass));
                            popupWindow.dismiss();
                            notifyUser("Campi svuotati");
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                }
            });
            addElem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(AddActivity.this);
                    @SuppressLint("InflateParams") final RelativeLayout relLey = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 15);
                    relLey.setLayoutParams(params);
                    relativeLayoutsList.add(relLey);
                    email.add((EditText) relLey.findViewById(R.id.emailAddEdit));
                    user.add((EditText) relLey.findViewById(R.id.userAddEdit));
                    password.add((EditText) relLey.findViewById(R.id.passAddEdit));
                    showPass.add((ImageButton) relLey.findViewById(R.id.showPass));
                    description.add((EditText) relLey.findViewById(R.id.descriptionAddEdit));
                    Button del = relLey.findViewById(R.id.deleteAddLay);
                    ll.addView(relLey);
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            email.remove(relLey.findViewById(R.id.emailAddEdit));
                            user.remove(relLey.findViewById(R.id.userAddEdit));
                            password.remove(relLey.findViewById(R.id.passAddEdit));
                            description.remove(relLey.findViewById(R.id.descriptionAddEdit));
                            showPass.remove(relLey.findViewById(R.id.showPass));
                            relativeLayoutsList.remove(relLey);
                            ll.removeView(relLey);
                            i--;
                        }
                    });
                    showPass((EditText) relLey.findViewById(R.id.passAddEdit), (ImageButton) relLey.findViewById(R.id.showPass));
                    i++;
                }

            });
            showPass((EditText) findViewById(R.id.passAddEdit), (ImageButton) findViewById(R.id.showPassImage));
        } else {
            notifyUser("Utente non rilevato. Impossibile aggiungere l'account.");
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity(User usr) {
        Intent intent = new Intent(AddActivity.this, ViewActivity.class);
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

    public boolean isInvalidWord(String word) {
        if (word.isEmpty()) return false;
        return !word.matches("[^ ]*");
    }

    public boolean isInvalidEmail(String email) {
        if (email.isEmpty()) return false;
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean fieldError(AccountElement a, int n) {
        if (isInvalidEmail(a.getEmail()) && isInvalidWord(a.getUser()) && isInvalidWord(a.getPassword())) {
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente, Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidWord(a.getPassword())) {
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword()) && isInvalidEmail(a.getEmail())) {
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidEmail(a.getEmail())) {
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente e Email non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser())) {
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Utente non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidEmail(a.getEmail())) {
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Email non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword())) {
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Password non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showPass(final EditText et, ImageButton showPass) {
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        et.setInputType(InputType.TYPE_NULL);
                        break;
                    case MotionEvent.ACTION_UP:
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(AddActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", owner);
        startActivity(intent);
        this.finish();
    }
}

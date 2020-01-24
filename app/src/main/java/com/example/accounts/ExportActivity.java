package com.example.accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class ExportActivity extends AppCompatActivity {
    private ManageUser mngUsr;
    private ArrayList<User> listUser = new ArrayList<>();
    private ManageApp mngApp;
    private LogApp log;
    private ArrayList<Account> listAccount;
    private ArrayList<Acc> listAcc;
    private ArrayList<Account> listAccountSelected;
    private ManageAccount mngAcc;
    private User usr;
    private ListView lv;
    private MyCustomAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");

        mngApp = new ManageApp();
        log = mngApp.deserializationFlag(this);

        mngUsr = new ManageUser();
        listUser = mngUsr.deserializationListUser(this);

        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());

        if (usr != null) {
            mngAcc = new ManageAccount();
            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());

           /* if (usr.getSort() == 1)
                mngAcc.serializationListAccount(this, AtoZ(listAccount), usr.getUser());
            else
                mngAcc.serializationListAccount(this, ZtoA(listAccount), usr.getUser());
           */

            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());
            displayAccList(listAccount);
        } else {
            notifyUser("Utente non rilevato. Impossibile esportare gli account.");
            goToMainActivity();
        }

    }

    public void goToMainActivity() {
        Intent intent = new Intent(ExportActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    private void displayAccList(ArrayList<Account> listAccount) {
        if (!listAccount.isEmpty()) {
            listAcc= new ArrayList<Acc>();
            for (Account a : listAccount) {
                listAcc.add(new Acc(a.getName(), (ArrayList<AccountElement>) a.getList()));
            }
            dataAdapter = new MyCustomAdapter(this, R.layout.single_listview_account, listAcc);
            lv = (ListView) findViewById(R.id.listAccount);
            lv.setAdapter(dataAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Acc acc = (Acc) parent.getItemAtPosition(position);
                }
            });
        } else notifyUser("Lista account vuota");
    }

    private class MyCustomAdapter extends ArrayAdapter<Account> {
        private ArrayList<Acc> listAcc;

        public MyCustomAdapter(Context context, int textViewResoutceId, ArrayList<Acc> listAcc) {
            super(context, textViewResoutceId, listAccount);
            this.listAcc = listAcc;
        }

        private class ViewHolder {
            TextView name;
            CheckBox chkBox;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.single_listview_account, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.chkBox = (CheckBox) convertView.findViewById(R.id.chk_box);
                convertView.setTag(holder);
                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Acc a = (Acc) cb.getTag();
                        a.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Acc a = listAcc.get(position);
            holder.name.setText(a.getName());
            holder.chkBox.setChecked(a.isSelected());
            return convertView;
        }
    }

    public void checkButtonClick() {
        Button myButton = (Button) findViewById(R.id.export);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Acc> accountArrayList = dataAdapter.listAcc;
                listAccountSelected = null;
                for (Acc a : accountArrayList) {
                    if (a.isSelected()) {
                        listAccountSelected.add(new Account(a.getName(), (ArrayList<AccountElement>) a.getList()));
                    }
                }
            }
        });
    }

    public ArrayList<Account> AtoZ(ArrayList<Account> list) {
        Collections.sort(list, new Comparator<Account>() {
            @Override
            public int compare(Account lhs, Account rhs) {
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        });
        return list;
    }

    public ArrayList<Account> ZtoA(ArrayList<Account> list) {
        Collections.sort(list, new Comparator<Account>() {
            @Override
            public int compare(Account lhs, Account rhs) {
                return rhs.getName().toLowerCase().compareTo(lhs.getName().toLowerCase());
            }
        });
        return list;
    }

}

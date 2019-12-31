package com.example.accounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class DynamicFragment extends Fragment {

    static DynamicFragment addFrag(String email, String user, String password) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("user", user);
        args.putString("password", password);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_show_element, container, false);
        EditText email;
        email = view.findViewById(R.id.emailShowEdit);
        email.setText(Objects.requireNonNull(getArguments()).getString("email", ""));
        EditText user;
        user = view.findViewById(R.id.userShowEdit);
        user.setText(getArguments().getString("user", ""));
        EditText password;
        password = view.findViewById(R.id.passShowEdit);
        password.setText(getArguments().getString("password", ""));
        return view;
    }
}
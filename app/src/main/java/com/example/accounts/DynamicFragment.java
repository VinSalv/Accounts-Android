package com.example.accounts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
        TextView email;
        email = view.findViewById(R.id.emailShowEdit);
        email.setText(Objects.requireNonNull(getArguments()).getString("email", ""));
        TextView user;
        user = view.findViewById(R.id.userShowEdit);
        user.setText(getArguments().getString("user", ""));
        TextView password;
        password = view.findViewById(R.id.passShowEdit);
        password.setText(getArguments().getString("password", ""));
        if (!password.getText().toString().equals("")) {
            ImageButton showPass = view.findViewById(R.id.showPass);
            showPass.setVisibility(View.VISIBLE);
            showPass(password, showPass, view);
        }
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showPass(final TextView tv, ImageButton showPass, View v) {
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorAccent));
                        break;
                    case MotionEvent.ACTION_UP:
                        tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.transparent));
                        break;
                }
                return true;
            }
        });
    }
}
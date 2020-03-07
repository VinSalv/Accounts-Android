package com.example.accounts;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

public class AboutActivity extends AppCompatActivity {
    private LinearLayout wellcome;
    private TextView wellcomeMini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        final Toolbar toolbar = findViewById(R.id.aboutToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        wellcome = findViewById(R.id.aboutContent);
        wellcomeMini = findViewById(R.id.aboutTextToolbar);
        wellcomeMini.setVisibility(View.INVISIBLE);
        AppBarLayout appBar = findViewById(R.id.aboutBarToolbar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                wellcome.setAlpha((1.0f - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()));
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    wellcomeMini.setVisibility(View.VISIBLE);

                } else if (isShow) {
                    wellcomeMini.setVisibility(View.INVISIBLE);
                    isShow = false;
                }
            }
        });
    }
}

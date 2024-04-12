package com.example.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class record_activity extends AppCompatActivity implements NavigationListener {

    TabLayout tab;
    ViewPager viewPager;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        tab = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewpagers);
        back = findViewById(R.id.backs);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the current activity instead of starting MainActivity again
            }
        });

        viewpagermessengerAdapter adapter = new viewpagermessengerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tab.setupWithViewPager(viewPager);
    }

    @Override
    public void navigateToNextFragment() {
        int nextFragmentIndex = viewPager.getCurrentItem() + 1;
        if (nextFragmentIndex < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(nextFragmentIndex);
        }
    }

}

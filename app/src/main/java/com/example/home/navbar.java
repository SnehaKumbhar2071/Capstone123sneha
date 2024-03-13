package com.example.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class navbar extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar_activity);
        bottomNavigationView = findViewById(R.id.bv);
        frameLayout = findViewById(R.id.framelayout); // Corrected ID for frameLayout

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    loadFragment(new home(), false);
                }
//                else if (itemId == R.id.profile)
//                {
//                    loadFragment(new profile(), false);
//                }
                else if (itemId == R.id.setting) {
                    loadFragment(new setting(), false);
                } else if (itemId == R.id.todo) {
                    loadFragment(new todo(), false);
                } else {

                }
                return true;
            }
        });
        loadFragment(new home(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment); // Corrected ID for frameLayout
        if (!isAppInitialized) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
}

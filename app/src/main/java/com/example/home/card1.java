package com.example.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class card1 extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    SearchView searchView;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card1);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(card1.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(card1.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new MyAdapter(card1.this, dataList);
        recyclerView.setAdapter(adapter);

        dialog.show();

        // Query Firestore data
        firestore.collection("patients").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DataClass dataClass = document.toObject(DataClass.class);
                            dataClass.setKey(document.getId());
                            dataList.add(dataClass);
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        // Handle errors
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(card1.this, record_activity.class);
            startActivity(intent);
        });

    }

    public void searchList(String text){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass: dataList){
            if (dataClass.getFullName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the activity when it is resumed
        refreshActivity();
    }

    private void refreshActivity() {
        // Query Firestore data again to refresh the list
        // This is similar to the code you used in onCreate() to initially populate the list

        AlertDialog.Builder builder = new AlertDialog.Builder(card1.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        firestore.collection("patients").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DataClass dataClass = document.toObject(DataClass.class);
                            dataClass.setKey(document.getId());
                            dataList.add(dataClass);
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        // Handle errors
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the detail activity when navigating back
    }
}

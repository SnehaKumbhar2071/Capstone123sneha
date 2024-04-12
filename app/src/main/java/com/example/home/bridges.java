package com.example.home;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class bridges extends AppCompatActivity {
    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    SearchView searchView;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridges);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView4);

        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(bridges.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(bridges.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new MyAdapter(bridges.this, dataList);
        recyclerView.setAdapter(adapter);

        dialog.show();

        // Query Firestore data
        firestore.collection("patients")
                .whereArrayContains("treatment", "bridge").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            DataClass dataClass = doc.toObject(DataClass.class);
                            if (dataClass != null) {
                                dataClass.setKey(doc.getId());
                                dataList.add(dataClass);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                    }
                    dialog.dismiss();
                });
        firestore.collection("patients")
                .whereArrayContains("treatment", "Bridge").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            DataClass dataClass = doc.toObject(DataClass.class);
                            if (dataClass != null) {
                                dataClass.setKey(doc.getId());
                                dataList.add(dataClass);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
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

    }

    public void searchList(String text) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : dataList) {
            if (dataClass.getFullName().toLowerCase().contains(text.toLowerCase())) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(bridges.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        firestore.collection("patients")
                .whereArrayContains("treatment", "bridge").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            DataClass dataClass = doc.toObject(DataClass.class);
                            if (dataClass != null) {
                                dataClass.setKey(doc.getId());
                                dataList.add(dataClass);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                    }
                    dialog.dismiss();
                });
        firestore.collection("patients")
                .whereArrayContains("treatment", "Bridge").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            DataClass dataClass = doc.toObject(DataClass.class);
                            if (dataClass != null) {
                                dataClass.setKey(doc.getId());
                                dataList.add(dataClass);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
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

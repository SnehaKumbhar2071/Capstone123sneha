package com.example.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (dataList == null || dataList.isEmpty()) {
            return; // Return if dataList is null or empty
        }

        DataClass currentItem = dataList.get(position);

        // Set data to views if they are not null
        if (holder.fullName != null) {
            holder.fullName.setText(currentItem.getFullName());
        }
        if (holder.address != null) {
            holder.address.setText(currentItem.getAddress());
        }
        if (holder.date != null) {
            holder.date.setText(currentItem.getDate());
        }
        if (holder.gender != null) {
            holder.gender.setText(currentItem.getGender());
        }
        if (holder.phoneNumber != null) {
            holder.phoneNumber.setText(currentItem.getPhoneNumber());
        }
        if (holder.dob != null) {
            holder.dob.setText(currentItem.getDob());
        }

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, detail.class);
                intent.putExtra("address", currentItem.getAddress());
                intent.putExtra("fullName", currentItem.getFullName());
                intent.putExtra("Key", currentItem.getKey());
                intent.putExtra("gender", currentItem.getGender());
                intent.putExtra("date", currentItem.getDate());
                intent.putExtra("dob", currentItem.getDob());
                intent.putExtra("phoneNumber", currentItem.getPhoneNumber());
                intent.putExtra("prepostimages", currentItem.getPrepostimages());
                intent.putExtra("prescript", currentItem.getPrescription());
                intent.putExtra("status", currentItem.getStatus());
                intent.putExtra("treatment", currentItem.getTreatment());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size(); // Return 0 if dataList is null
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}
class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView date,fullName,address,phoneNumber,dob,gender;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage=itemView.findViewById(R.id.recImage);

        date = itemView.findViewById(R.id.dateTextView);
        recCard = itemView.findViewById(R.id.recCard);
        fullName = itemView.findViewById(R.id.fullName);
    }
}


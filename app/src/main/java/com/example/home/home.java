package com.example.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class home extends Fragment implements View.OnClickListener {

    private CardView cardView1;
    private CardView cardView2;
    private CardView cardView3;
    private CardView cardView4;

    private CardView cardView5;
    private CardView cardView6;




    ImageView  imageView;
    @SuppressLint("MissingInflatedId")
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageView =view.findViewById(R.id.imageView);
        // Initialize CardViews
        cardView1 = view.findViewById(R.id.c1);
        cardView2 = view.findViewById(R.id.c2);
        cardView3 = view.findViewById(R.id.brace);
        cardView4 = view.findViewById(R.id.bridge);
        cardView4 = view.findViewById(R.id.root);
        cardView4 = view.findViewById(R.id.teeth);
        // Set click listeners for CardViews
         ImageView imageView=view.findViewById(R.id.imageView);
        CardView cardView1 = view.findViewById(R.id.c1);
        CardView cardView2 = view.findViewById(R.id.c2);
        CardView cardView3 = view.findViewById(R.id.bridge);
        CardView cardView4 = view.findViewById(R.id.brace);
        CardView cardView5= view.findViewById(R.id.teeth);
        CardView cardView6 = view.findViewById(R.id.root);
        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        cardView5.setOnClickListener(this);
        imageView.setOnClickListener(this);
  imageView.setOnClickListener(this);


        return view;
    }




    @Override
    public void onClick(View v) {
        Intent intent;

        // Handle click events based on the clicked view
        if (v.getId() == R.id.c1) {
            // Replace "d2.class" with the actual class you want to navigate to
            Intent intents = new Intent(getActivity(), card1.class);
            startActivity(intents);
        }

        else if (v.getId() == R.id.c2) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), card2.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.brace) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), brace.class);
            startActivity(intent);
        }

        else if (v.getId() == R.id.bridge) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), bridges.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.teeth) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), teeth.class);
            startActivity(intent);
        }

        else if (v.getId() == R.id.root) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), root.class);
            startActivity(intent);
        }

        else if (v.getId() == R.id.imageView) {
            // Replace "imageview.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), imageview.class);
            startActivity(intent);
        }


    }
}


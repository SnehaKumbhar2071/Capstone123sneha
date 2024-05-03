package com.example.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class home extends Fragment implements View.OnClickListener {

    private CardView cardView1;
    private CardView cardView2;
    private CardView cardView3;
    private CardView cardView4;

    private CardView cardView5;
    private CardView cardView6;
    private CardView cardView7;
    private CardView cardView8;
    private CardView cardView9;





//    ImageView  imageView;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cardView1 = view.findViewById(R.id.c1);
        cardView2 = view.findViewById(R.id.fillingcard);
        cardView3 = view.findViewById(R.id.Rootcard);
        cardView4 = view.findViewById(R.id.extractioncard);
        cardView5 = view.findViewById(R.id.lasercard);
        cardView6 = view.findViewById(R.id.crowncard);
        cardView7 = view.findViewById(R.id.Lami);
        cardView8 = view.findViewById(R.id.implantcard);
        cardView9 = view.findViewById(R.id.periodonticscard);

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        cardView5.setOnClickListener(this);
        cardView6.setOnClickListener(this);
        cardView7.setOnClickListener(this);
        cardView8.setOnClickListener(this);
        cardView9.setOnClickListener(this);
        return view;
    }




    @Override
    public void onClick(View v) {
        Intent intent;


        if (v.getId() == R.id.c1) {

            Intent intents = new Intent(getActivity(), card1.class);
            startActivity(intents);
        }

        else if (v.getId() == R.id.fillingcard) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), fillings.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.Rootcard) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), root.class);
            startActivity(intent);
        }

        else if (v.getId() == R.id.extractioncard) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), extractions.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.lasercard) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), laserSurgery.class);
            startActivity(intent);
        }

        else if (v.getId() == R.id.periodonticscard) {
            // Replace "d2.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), periodentics.class);
            startActivity(intent);
        }

        else if (v.getId() == R.id.implantcard) {
            // Replace "imageview.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), Implants.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.Lami) {
            // Replace "imageview.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), Laminates.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.crowncard) {
            // Replace "imageview.class" with the actual class you want to navigate to
            intent = new Intent(getActivity(), CrownAndBridges.class);
            startActivity(intent);
        }


    }
}


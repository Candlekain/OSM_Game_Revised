package com.example.georg.osmapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Created by Georg on 08.12.2016.
 */

public class ResultDialog extends DialogFragment {

    private int points;
    private String itemName;
    private String[] correctAnswers;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.points = getArguments().getInt("points");
        this.itemName = getArguments().getString("itemName");
        this.correctAnswers = getArguments().getStringArray("answers");
        //System.out.println("Array: "+correctAnswers[0]+correctAnswers[1]+correctAnswers[2]);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Punktestand für: "+itemName);

        builder.setView(getRatingLayout());


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.putExtra("points", points);
                getActivity().setResult(1,intent);

                //startActivity(intent);
                getActivity().finish();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public LinearLayout getRatingLayout(){

        LinearLayout upperLayout = new LinearLayout(getActivity());
        upperLayout.setOrientation(LinearLayout.HORIZONTAL);
        upperLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
        //upperLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        //upperLayout.setBaselineAligned(false);

        ImageView image = new ImageView(getActivity());
        image.setImageResource(getResources().getIdentifier(itemName.toLowerCase().replace(" ","").replace("-",""),"drawable",getActivity().getPackageName()));
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,Gravity.CENTER_HORIZONTAL));
        //image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setAdjustViewBounds(true);
        //image.setMinimumHeight(200);
        image.setMaxHeight(200);

        upperLayout.addView(image);

        TextView answers = new TextView(getActivity());
        answers.setText("Richtige Antworten:\n [1] "+correctAnswers[0]+"\n [2] "+ correctAnswers[1]+"\n [3] "+correctAnswers[2]);
        answers.setTextColor(Color.BLACK);
        answers.setTextSize(24);
        answers.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,Gravity.CENTER_HORIZONTAL));

        upperLayout.addView(answers);

        RatingBar stars = new RatingBar(getActivity());
        stars.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
        stars.setNumStars(3);
        stars.setMax(3);
        stars.setIsIndicator(true);
        stars.setRating(points);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBaselineAligned(false);

        layout.addView(upperLayout);
        layout.addView(stars);

       // ScrollView container = new ScrollView(getActivity());
       // container.addView(layout);

        return layout;
    }

    /*
    public LinearLayout getPointLayout(){
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        // adding stars for right answers
        for(int i=1; i<=points;i++){
            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.star1);
            layout.addView(image);
        }

        // adding empty stars for missing points
        for(int i=1; i<=5-points;i++){
            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.star2);
            layout.addView(image);
        }

        return layout;
    }*/
}

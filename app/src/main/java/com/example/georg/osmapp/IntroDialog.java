package com.example.georg.osmapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Georg on 18.05.2017.
 */

public class IntroDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View introView = View.inflate(getActivity(), R.layout.dialog_intro, null);
        builder.setView(introView);

        builder.setPositiveButton("Los gehts", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });

        return builder.create();
    }

    /**
    public LinearLayout getIntroLayout(){

        // horizontal layout with image (historian) on the left side and introductional text on the right
        LinearLayout horLayout = new LinearLayout(getActivity());
        horLayout.setOrientation(LinearLayout.HORIZONTAL);
        horLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

        // image of historian
        ImageView historianView = new ImageView(getActivity());
        historianView.setImageResource(R.drawable.historian);
        //historianView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1f));
        //historianView.setAdjustViewBounds(true);
        //historianView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // intro text
        ScrollView scrollView = new ScrollView(getActivity());
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.intro_text);
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setMovementMethod(new ScrollingMovementMethod());
        //textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));

        //scrollView.addView(textView);
        //scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1f));
        //scrollView.setScrollbarFadingEnabled(false);
       // scrollView.setFillViewport(true);

        horLayout.addView(historianView);
        horLayout.addView(textView);

        return horLayout;
    }*/
}

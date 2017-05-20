package com.example.georg.osmapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Georg on 20.05.2017.
 */

public class AchievementDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View achievementView = View.inflate(getActivity(), R.layout.dialog_achievement, null);
        builder.setView(achievementView);

        ImageView view = (ImageView)achievementView.findViewById(R.id.badge_dialog);
        view.setImageResource(getArguments().getInt("ID"));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });

        return builder.create();
    }
}

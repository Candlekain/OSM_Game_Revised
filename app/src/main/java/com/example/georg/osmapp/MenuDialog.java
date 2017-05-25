package com.example.georg.osmapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Georg on 24.05.2017.
 */

public class MenuDialog extends DialogFragment {

    private View dialogLayout;
    private String itemName;
    private Boolean game_1_activated;
    private Boolean game_2_activated;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        itemName = getArguments().getString("itemName");
        game_1_activated = getArguments().getBoolean("game_1_activated");
        game_2_activated = getArguments().getBoolean("game_2_activated");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogLayout = View.inflate(getActivity(), R.layout.dialog_menu, null);
        builder.setView(dialogLayout);

        addButtonFuncionality();

        return builder.create();
    }

    public void addButtonFuncionality(){
        Button buttonFillBlanks= (Button) dialogLayout.findViewById(R.id.button_game_1);
        if(!game_1_activated){
            buttonFillBlanks.setEnabled(false);
        } else {
            buttonFillBlanks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getDialog() != null){
                        getDialog().cancel();
                    }
                    Intent intent = new Intent(getActivity().getApplicationContext(), GameBlanksActivity.class);
                    intent.putExtra("Name",itemName);
                    getActivity().startActivityForResult(intent,1);
                }
            });
        }

        Button buttonExploration= (Button) dialogLayout.findViewById(R.id.button_game_2);
        if(!game_2_activated){
            buttonExploration.setEnabled(false);
        } else {
            buttonExploration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getDialog() != null){
                        getDialog().cancel();
                    }
                    Intent intent = new Intent(getActivity().getApplicationContext(), GameExplorationActivity.class);
                    intent.putExtra("Name",itemName);
                    getActivity().startActivityForResult(intent,1);
                }
            });
        }
    }
}

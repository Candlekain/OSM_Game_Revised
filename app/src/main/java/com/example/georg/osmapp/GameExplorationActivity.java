package com.example.georg.osmapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Georg on 24.05.2017.
 */

public class GameExplorationActivity extends Activity {

    private String itemName;
    private ImageView[] imageArray;
    private Drawable correctImage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_exploration);

        this.itemName = getIntent().getStringExtra("Name");

        setImages();
        assignImages();
        addImageFunctionality();
    }

    public void setImages(){
        ImageView image_1 = (ImageView)findViewById(R.id.exploration_image_1);
        ImageView image_2 = (ImageView)findViewById(R.id.exploration_image_2);
        ImageView image_3 = (ImageView)findViewById(R.id.exploration_image_3);
        ImageView image_4 = (ImageView)findViewById(R.id.exploration_image_4);
        imageArray = new ImageView[]{image_1,image_2,image_3,image_4};
    }

    public void assignImages(){
        try {
            String[] assets = getAssets().list("pictures");

            //File directory = new File(getAssets().open("images").toString());
            //File[] files = directory.listFiles();
            ArrayList<Drawable> allDrawables = new ArrayList<Drawable>();
            for(String asset:assets){
                if(asset.contains(itemName.toLowerCase())){
                    correctImage = Drawable.createFromStream(getAssets().open("pictures/"+asset),asset);
                } else {
                    allDrawables.add(Drawable.createFromStream(getAssets().open("pictures/"+asset),asset));
                    System.out.println(asset);
                }
            }
            Collections.shuffle(allDrawables);
            List<Drawable> pickedDrawables = Arrays.asList(correctImage,allDrawables.get(0),allDrawables.get(1),allDrawables.get(2));
            Collections.shuffle(pickedDrawables);

            for(int i=0;i<imageArray.length;i++){
                imageArray[i].setImageDrawable(pickedDrawables.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addImageFunctionality(){
        for(final ImageView image:imageArray){
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GameExplorationActivity.this);
                    final Intent intent = new Intent();
                    if(image.getDrawable().equals(correctImage)){
                        alertBuilder.setMessage("Richtige Antwort!");
                        intent.putExtra("points", 3);
                    } else {
                        alertBuilder.setMessage("Leider Falsch");
                        intent.putExtra("points", 0);
                    }

                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            GameExplorationActivity.this.setResult(2,intent);
                            GameExplorationActivity.this.finish();

                        }
                    });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();

                }
            });
        }
    }
}

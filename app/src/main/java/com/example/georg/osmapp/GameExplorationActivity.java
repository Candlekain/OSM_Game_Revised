package com.example.georg.osmapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Georg on 24.05.2017.
 */

public class GameExplorationActivity extends Activity {

    private String itemName;
    private ImageView[] imageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_exploration);

        this.itemName = getIntent().getStringExtra("Name");

        setImages();
        assignImages();
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
            String[] assets = getAssets().list("images");
            
            //File directory = new File(getAssets().open("images").toString());
            //File[] files = directory.listFiles();
            ArrayList<Drawable> drawables = new ArrayList<Drawable>();
            for(String asset:assets){
                System.out.println(asset);
                drawables.add(Drawable.createFromStream(getAssets().open("images/"+asset),asset));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

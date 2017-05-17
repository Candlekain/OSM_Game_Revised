package com.example.georg.osmapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class GameActivity extends AppCompatActivity {

    private String itemName;
    private Button[] buttonArray;
    private CheckBox[] boxArray;
    private HashMap<Integer,String> wordMap;
    private HashMap<Integer,String[]> alternatives;
    private int points;
    private int counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // set local variables
        this.itemName = getIntent().getStringExtra("Name");
        this.wordMap = new HashMap<Integer,String>();
        this.alternatives = new HashMap<Integer,String[]>();
        this.points = 0;
        this.counter = 0;

        setTitle(itemName);
        setButtons();
        setCheckBoxes();

        parseContent();
        addButtonLabels();
        addButtonFunctionality();

    }

    public void setButtons(){
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        this.buttonArray = new Button[]{button1,button2,button3};
    }

    public void setCheckBoxes(){
        CheckBox box1 = (CheckBox) findViewById(R.id.checkBox1);
        CheckBox box2 = (CheckBox) findViewById(R.id.checkBox2);
        CheckBox box3 = (CheckBox) findViewById(R.id.checkBox3);
        this.boxArray = new CheckBox[]{box1,box2,box3};
        setBoxAnimation(box1,true);
    }

    public void parseContent(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(getAssets().open(itemName+".txt"),"UTF-8");
            TextView text = (TextView) findViewById(R.id.textView1);
            text.setText("");
            text.setTextSize(24);
            while(scanner.hasNext()){
                String s = scanner.next();
                if(s.contains("[")){
                    String word = s.substring(0,s.indexOf("|"));
                    int number = Integer.parseInt(s.substring(s.indexOf("[")+1,s.indexOf("]")));
                    wordMap.put(number,word);
                    String[]options = s.substring(0,s.indexOf("[")).split("\\|");
                    alternatives.put(number,options);

                    s = "["+number+"]";
                    text.append(Html.fromHtml("<font color=\"red\"><b>"+s+"</b></font>"));
                    text.append(" ");
                } else {
                    text.append(s+" ");
                }
            }
            //System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../res/texts/"+itemName+".txt"));

            String line;
            while((line = reader.readLine()) != null){

                System.out.println(line);

            }
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void addButtonLabels(){
        // button labels need to be cleared at first
        for(Button button:buttonArray){
            button.setText("");
        }

        String[]words = alternatives.get(counter+1);
        for(String word:words){
            buttonArray[getRandomNumber()].setText(word);
        }
    }

    public int getRandomNumber(){
        Random rand = new Random();
        int randomNum = rand.nextInt(buttonArray.length);
        if(buttonArray[randomNum].getText().equals("")){
            System.out.print("Button free");
            return randomNum;
        } else {
            System.out.print("Button occupied");
            return getRandomNumber();
        }
    }

    public void addButtonFunctionality(){
        for(final Button currentButton:buttonArray){
            currentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check for correct answer and increase counter
                    if(wordMap.get(counter+1).equals(currentButton.getText().toString())){
                        points += 1;
                    }

                    // overwrite checkBox text
                    CheckBox box = boxArray[counter];
                    box.setText(box.getText()+" "+currentButton.getText());
                    setBoxAnimation(box,false);
                    if(counter<2){
                        setBoxAnimation(boxArray[counter+1],true);
                        counter += 1;
                        addButtonLabels();
                    } else {
                        // when 3 answers have been checked, points are shown and main activity resumed
                        ResultDialog dialog = new ResultDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt("points",points);
                        bundle.putString("itemName",itemName);
                        String[]wordArray = new String[]{wordMap.get(1),wordMap.get(2),wordMap.get(3)};
                        bundle.putStringArray("answers", wordArray);//wordMap.values().toArray(new String[wordMap.size()]));
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(),"Display Dialog");
                    }
                }
            });
        }

    }

    public void setBoxAnimation(CheckBox box, boolean activated){
        if(activated){
            box.setBackgroundResource(R.drawable.back);
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(100); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            box.startAnimation(anim);
        } else {
            box.clearAnimation();
            box.setBackgroundResource(0);
            box.setChecked(true);
        }

    }

}

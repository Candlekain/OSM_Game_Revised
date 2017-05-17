package com.example.georg.osmapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


/**
 * Created by Georg on 03.11.2016.
 */

public class ItemDialog extends DialogFragment {

    private String itemName;
    private String content;
    private ProgressBar timerBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        itemName = getArguments().getString("itemName");
        content = "";
        timerBar = new ProgressBar(getActivity(),null, android.R.attr.progressBarStyleHorizontal);
        // Deprecated: request Wiki article and define content
        /*
        WikiProvider provider = new WikiProvider();
        provider.execute(itemName);

        try {
            content = provider.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

        // Prepare content for game
        parseContent();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(itemName);

        builder.setView(getItemLayout());
        //ImageView image = new ImageView(getActivity());
        //image.setImageResource(getResources().getIdentifier("drawable/"+itemName.toLowerCase(),null,getActivity().getPackageName()));
        //builder.setView(image);

        //builder.setMessage(content);

        builder.setPositiveButton("Spiel starten", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Start the game in a new activity
                        startGame();

                    }
                });

        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Start timer
        setupTimer();

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void parseContent(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(getActivity().getAssets().open(itemName+".txt"),"UTF-8");
            while(scanner.hasNext()){
                String s = scanner.next();
                if(s.contains("[")){
                    String word = s.substring(0,s.indexOf("|"));
                    content = content+word+" ";
                } else {
                    content = content+s+" ";
                }
            }
            //System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public LinearLayout getItemLayout(){
        // Construction of general layout
        LinearLayout vertLayout = new LinearLayout(getActivity());
        vertLayout.setOrientation(LinearLayout.VERTICAL);


        // Content for upper part of dialog
        LinearLayout horLayout = new LinearLayout(getActivity());
        horLayout.setOrientation(LinearLayout.HORIZONTAL);
        horLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f ));

        // Content for horizontal layout
        ScrollView scrollText = new ScrollView(this.getActivity());
        TextView text = new TextView(getActivity());
        text.setText(content);
        text.setTextSize(24);
        text.setTextColor(Color.BLACK);

        scrollText.addView(text);
        scrollText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f));

        ImageView image = new ImageView(getActivity());
        image.setImageResource(getResources().getIdentifier("drawable/"+itemName.toLowerCase().replace(" ","").replace("-",""),null,getActivity().getPackageName()));
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1f));
        //image.setAdjustViewBounds(true);
        //image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        horLayout.addView(scrollText);
        horLayout.addView(image);

        // Content for lower part of dialog
        timerBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
        timerBar.setMax(90);
        timerBar.setProgress(0);
        timerBar.setScaleY(10f);


        // putting upper and lower part together
        vertLayout.addView(horLayout);
        vertLayout.addView(timerBar);
        return vertLayout;
    }

    public void startGame(){
        Intent intent = new Intent(getActivity().getApplicationContext(), GameActivity.class);
        intent.putExtra("Name",itemName);
        getActivity().startActivityForResult(intent,1);
        //startActivity(intent);
    }

    public void setupTimer(){
        new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerBar.setProgress(timerBar.getProgress()+1);
            }

            public void onFinish() {
                if(getDialog() != null){
                    getDialog().cancel();
                }
            }
        }.start();

    }

    /*
    public void requestArticle(){
        String url = "https://de.wikipedia.org/w/api.php?action=query&prop=extracts&titles="+itemName.replace(" ","%20")+"&redirects&format=json&exintro&explaintext";
        //String url = "http://www.wuerzburgwiki.de/w/api.php?action=query&prop=extracts&titles="+itemName.replace(" ","%20")+"&redirects&format=json&exintro&explaintext";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!

                        try {
                            String responseString = response.getJSONObject("query").getJSONObject("pages").names().getString(0);
                            response = response.getJSONObject("query").getJSONObject("pages").getJSONObject(responseString);
                            String content = response.getString("extract");
                            setContent(content);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this.getActivity()).add(jsonRequest);

    }

    public void setContent(String content){
        this.content = content;
    }


    public String parseContent(String content){

        String result = "";
        BufferedReader reader = new BufferedReader(new StringReader(content));
        try {
            while (reader.ready()){
                String regex = "";
                String line ="";
                if((line = reader.readLine()).matches(regex)){
                    result = line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }*/
}

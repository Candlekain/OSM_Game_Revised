package com.example.georg.osmapp;

import android.app.Activity;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Georg on 25.05.2017.
 */

public class MyOverlayItem extends OverlayItem {
    private  boolean game_1_activated;
    private  boolean game_2_activated;
    private int points;

    public MyOverlayItem(String aTitle, String aSnippet, IGeoPoint aGeoPoint) {
        super(aTitle, aSnippet, aGeoPoint);
        this.game_1_activated=true;
        this.game_2_activated=true;
        this.points = 0;
    }

    public boolean isGame_1_activated() {
        return game_1_activated;
    }

    public void setGame_1_activated(boolean game_1_activated) {
        this.game_1_activated = game_1_activated;
    }

    public boolean isGame_2_activated() {
        return game_2_activated;
    }

    public void setGame_2_activated(boolean game_2_activated) {
        this.game_2_activated = game_2_activated;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public int getPoints(){
        return this.points;
    }


}

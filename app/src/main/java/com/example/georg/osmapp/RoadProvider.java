package com.example.georg.osmapp;

import android.content.Context;
import android.os.AsyncTask;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

/**
 * Created by Georg on 19.05.2017.
 */

public class RoadProvider extends AsyncTask<ArrayList<GeoPoint>, Void, Polyline> {

    private Context context;

    public RoadProvider(Context context){
        this.context=context;
    }
    @Override
    protected Polyline doInBackground(ArrayList<GeoPoint>... params) {
        RoadManager roadManager = new OSRMRoadManager(context);
        Road road = roadManager.getRoad(params[0]);
        return RoadManager.buildRoadOverlay(road);
    }
}

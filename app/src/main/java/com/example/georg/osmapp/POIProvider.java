package com.example.georg.osmapp;

import android.os.AsyncTask;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by Georg on 28.10.2016.
 */

public class POIProvider extends AsyncTask <BoundingBox, Void, ArrayList<POI>> {

    @Override
    protected ArrayList<POI> doInBackground(BoundingBox... params) {
        ArrayList<POI> poiList = new ArrayList<POI>();
        OverpassAPIProvider  poiProvider = new OverpassAPIProvider();

        ArrayList<POI> churchList = poiProvider.getPOIsFromUrl( poiProvider.urlForPOISearch("amenity=place_of_worship",params[0],200,30));
        ArrayList<POI> memList = poiProvider.getPOIsFromUrl( poiProvider.urlForPOISearch("historic=memorial",params[0],200,30));

        // fake list for evaluation study:
        // ArrayList<POI> churchList = new ArrayList<POI>();

        //poiList.addAll(memList);
        if(churchList != null){
            poiList.addAll(churchList);
        }
        if(memList != null){
            poiList.addAll(memList);
        }
        return poiList;
    }

    @Override
    protected void onPostExecute(ArrayList<POI> poiList){

    }

}

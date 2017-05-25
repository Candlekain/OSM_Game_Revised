package com.example.georg.osmapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements LocationListener{

    private GeoPoint currentLocation;
    private MapView map;
    private BoundingBox box;
    private IMapController mapController;
    private LocationManager locationManager;
    private MyLocationNewOverlay locationOverlay;

    private ItemizedOverlayWithFocus<MyOverlayItem> itemOverlay;
    private MyOverlayItem currentItem;
    private String currentText;
    private int currentImage;
    private int currentPoints;
    private int stage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        setContentView(R.layout.activity_main);

        // Intro-Dialog is created
        IntroDialog introDialog = new IntroDialog();
        introDialog.show(getFragmentManager(),"Intro Dialog");

        //important! set your user agent to prevent getting banned from the osm servers
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        currentLocation = new GeoPoint(49.779836, 9.960033);
        currentPoints=0;
        stage=0;
        setupMapView();
        setupViewPoint();
        setupLocationOverlay();
        setupCompassOverlay();
        displayPOIs();

        // only for evaluation study purposes:
        setupEvaluation();
    }

    @Override
    public void onResume(){
        super.onResume();
        enableLocationUpdates();
    }

    @Override
    public void onPause(){
        super.onPause();
        disableLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location changed");

        //System.out.println(new GeoPoint(location).distanceTo(currentLocation));
        if(!box.contains(new GeoPoint(location))){
        //if(new GeoPoint(location).distanceTo(currentLocation) > 50){
            currentLocation = new GeoPoint(location);

            // fake location for evaluation testing
            //currentLocation = new GeoPoint(49.78159,9.97103);
            mapController.animateTo(currentLocation);
            displayPOIs();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("Status changed"+status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("Provider enabled"+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("Provider disabled"+provider);
    }


    public void setupMapView(){
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // set options for zoom with 2 fingers and buttons
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);
        map.setMaxZoomLevel(18);
        map.setMinZoomLevel(17);

    }

    public void setupViewPoint(){
        mapController = map.getController();
        mapController.setZoom(18);
        mapController.setCenter(currentLocation);
    }

    public void setupLocationOverlay(){
        GpsMyLocationProvider provider = new GpsMyLocationProvider(getApplicationContext());
        locationOverlay = new MyLocationNewOverlay(provider, map);
        locationOverlay.setDrawAccuracyEnabled(false);

        map.getOverlays().add(locationOverlay);
    }

    public void setupCompassOverlay(){
        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);
    }

    public void enableLocationUpdates(){
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000, 0, this);
            }
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000, 0, this);
            }
        } catch (SecurityException ex){
            System.err.println(ex);
        }
    }

    public void disableLocationUpdates(){
        locationOverlay.disableMyLocation();
        locationOverlay.disableFollowLocation();
        try{
            locationManager.removeUpdates(this);
        } catch (SecurityException ex){
            System.err.println(ex);
        }
    }

    public void displayPOIs(){

        box = new BoundingBox(currentLocation.getLatitude()+0.001,currentLocation.getLongitude()+0.001,currentLocation.getLatitude()-0.001,currentLocation.getLongitude()-0.001).increaseByScale(5f);
        map.setScrollableAreaLimitDouble(box);

        POIProvider poiProvider = new POIProvider();
        poiProvider.execute(box);
        ArrayList<MyOverlayItem> items = new ArrayList<MyOverlayItem>();
        try {
            ArrayList<POI> poiList = poiProvider.get();
            Iterator<POI> poiIterator = poiList.iterator();
            ArrayList<GeoPoint> roadList = new ArrayList<GeoPoint>();

            while(poiIterator.hasNext()){
                POI poi = poiIterator.next();
                roadList.add(poi.mLocation);
                MyOverlayItem item = new MyOverlayItem(poi.mType, poi.mDescription, poi.mLocation);
                item.setMarker(getIcon(poi.mType));
                items.add(item);
            }
            addItems(items);
            RoadProvider roadProvider = new RoadProvider(this);
            roadProvider.execute(roadList);
            Polyline roadOverlay = roadProvider.get();
            map.getOverlays().add(roadOverlay);
            map.invalidate();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void addItems(ArrayList<MyOverlayItem> itemList){
        itemOverlay =  new ItemizedOverlayWithFocus<MyOverlayItem>(this, itemList,
                new ItemizedIconOverlay.OnItemGestureListener<MyOverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final MyOverlayItem item) {

                        //if(item.equals(currentItem)){
                        if(item.getDrawable().getConstantState().equals(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_deactivated).getConstantState())){
                            return true;
                        } else {
                            currentItem = item;
                            currentText = getTextForItem(currentItem.getTitle());
                            currentImage = getResources().getIdentifier(currentItem.getTitle().toLowerCase().replace(" ","").replace("-",""),"drawable",getPackageName());
                            ItemDialog dialog = new ItemDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString("itemName",item.getTitle());
                            bundle.putString("itemContent",currentText);
                            bundle.putBoolean("game_1_activated",item.isGame_1_activated());
                            bundle.putBoolean("game_2_activated",item.isGame_2_activated());
                            dialog.setArguments(bundle);
                            dialog.show(getFragmentManager(),"Display Dialog");
                            return true;
                        }
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final MyOverlayItem item) {
                        return false;
                    }
                });
        itemOverlay.setFocusItemsOnTap(false);
        map.getOverlays().add(itemOverlay);
    }

    public String getTextForItem(String itemName){
        String content = "";
        Scanner scanner;
        try {
            scanner = new Scanner(getAssets().open(itemName+".txt"),"UTF-8");
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
        return content;
    }

    public Drawable getIcon(String type){
        return ContextCompat.getDrawable(this, R.drawable.marker_default);
        //getDrawable(R.drawable.marker_default);
        //getResources().getDrawable(R.drawable.marker_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // rating stars as feedback for single POI
        RatingBar stars = new RatingBar(this);
        stars.setNumStars(6);
        stars.setMax(6);
        stars.setIsIndicator(true);
        stars.setLayoutParams(new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,MapView.LayoutParams.WRAP_CONTENT,currentItem.getPoint(),MapView.LayoutParams.CENTER,0,0));
        stars.setScaleX(0.5f);
        stars.setScaleY(0.5f);

        int newPoints = data.getExtras().getInt("points");
        TextView pointsTotal = (TextView) findViewById(R.id.current_points);
        currentPoints = currentPoints+newPoints;
        pointsTotal.setText(String.valueOf(currentPoints));

        currentItem.setPoints(currentItem.getPoints()+newPoints);
        stars.setRating(currentItem.getPoints());

        map.addView(stars,stars.getLayoutParams());

        // update stage/badge according to current Points
        showAchievementDialog();

        if(resultCode==1){
            currentItem.setGame_1_activated(false);
        } else if(resultCode==2){
            currentItem.setGame_2_activated(false);
        }
        if(!currentItem.isGame_1_activated() && !currentItem.isGame_2_activated()){
            // deactivate game for current item
            currentItem.setMarker(ContextCompat.getDrawable(this, R.drawable.marker_deactivated));

        }
    }

    public void showAchievementDialog(){
        ImageView badge = (ImageView)findViewById(R.id.badge);
        AchievementDialog dialog = new AchievementDialog();
        Bundle bundle = new Bundle();

        if(currentPoints >=5 && currentPoints <10 && stage==0){
            badge.setImageResource(R.drawable.bronze_small);
            this.stage=1;
            bundle.putInt("ID",R.drawable.bronze);
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(),"Display Dialog");
        } else if(currentPoints >= 10 && currentPoints <15 && stage==1){
            badge.setImageResource(R.drawable.silver_small);
            this.stage=2;
            bundle.putInt("ID",R.drawable.silver);
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(),"Display Dialog");
        } else if(currentPoints >= 15 && stage==2){
            badge.setImageResource(R.drawable.gold_small);
            this.stage=3;
            bundle.putInt("ID",R.drawable.gold);
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(),"Display Dialog");
        }
    }

    public void setupEvaluation(){
        // Add Items for Evaluation study
        MyOverlayItem sonnenzeichen = new MyOverlayItem("Sonnenzeichen", "memorial", new GeoPoint(49.78237,9.96784));
        sonnenzeichen.setMarker(getIcon("memorial"));
        MyOverlayItem roterPlatz = new MyOverlayItem("Roter Platz", "memorial", new GeoPoint(49.78215,9.96807));
        roterPlatz.setMarker(getIcon("memorial"));
        MyOverlayItem minimalflaeche = new MyOverlayItem("Ennepersche Minimalflaeche", "memorial", new GeoPoint(49.78454,9.97312));
        minimalflaeche.setMarker(getIcon("memorial"));
        MyOverlayItem museum = new MyOverlayItem("Mineralogisches Museum", "museum", new GeoPoint(49.78235,9.97019));
        museum.setMarker(getIcon("museum"));
        MyOverlayItem kopf = new MyOverlayItem("Denker-Kopf", "sculpture", new GeoPoint(49.78331,9.97058));
        kopf.setMarker(getIcon("sculpture"));

        // Add order numbers for study
        List<Integer> orderList = Arrays.asList(1,2,3,4,5);
        Collections.shuffle(orderList);
        List<MyOverlayItem> studyItems = Arrays.asList(sonnenzeichen,roterPlatz,minimalflaeche,museum,kopf);

        for(int i=0;i<5;i++){
            MyOverlayItem item = studyItems.get(i);
            int order = orderList.get(i);

            TextView numberView = new TextView(this);
            numberView.setText(String.valueOf(order));
            numberView.setTextColor(Color.RED);
            numberView.setLayoutParams(new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,MapView.LayoutParams.WRAP_CONTENT,item.getPoint(),MapView.LayoutParams.BOTTOM_RIGHT,-15,0));
            numberView.setTextSize(30);
            map.addView(numberView,numberView.getLayoutParams());
        }

        itemOverlay.addItems(studyItems);
    }

}

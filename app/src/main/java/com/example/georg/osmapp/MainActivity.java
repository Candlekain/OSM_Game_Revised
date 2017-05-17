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
import android.widget.RatingBar;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements LocationListener{

    private GeoPoint currentLocation;
    private MapView map;
    private BoundingBox box;
    private IMapController mapController;
    private LocationManager locationManager;
    private MyLocationNewOverlay locationOverlay;

    private ItemizedOverlayWithFocus<OverlayItem> itemOverlay;
    private OverlayItem currentItem;

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

        //important! set your user agent to prevent getting banned from the osm servers
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        currentLocation = new GeoPoint(49.779836, 9.960033);
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
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        try {
            ArrayList<POI> poiList = poiProvider.get();
            Iterator<POI> poiIterator = poiList.iterator();
            while(poiIterator.hasNext()){
                POI poi = poiIterator.next();
                OverlayItem item = new OverlayItem(poi.mType, poi.mDescription, poi.mLocation);
                item.setMarker(getIcon(poi.mType));
                items.add(item);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(itemOverlay != null){
            itemOverlay.addItems(items);
        } else {
            itemOverlay =  new ItemizedOverlayWithFocus<OverlayItem>(this, items,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {

                            //if(item.equals(currentItem)){
                            if(item.getDrawable().getConstantState().equals(ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker_deactivated).getConstantState())){
                                return true;
                            } else {
                                currentItem = item;
                                Bundle bundle = new Bundle();
                                bundle.putString("itemName",item.getTitle());
                                ItemDialog dialog = new ItemDialog();
                                dialog.setArguments(bundle);
                                dialog.show(getFragmentManager(),"Display Dialog");
                                return true;
                            }

                        }
                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                            return false;
                        }
                    });
            itemOverlay.setFocusItemsOnTap(false);

            map.getOverlays().add(itemOverlay);
        }


    }

    public Drawable getIcon(String type){
        return ContextCompat.getDrawable(this, R.drawable.marker_default);
        //getDrawable(R.drawable.marker_default);
        //getResources().getDrawable(R.drawable.marker_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            int newPoints = data.getExtras().getInt("points");
            TextView pointsTotal = (TextView) findViewById(R.id.current_points);
            int oldPoints = Integer.parseInt(pointsTotal.getText().toString());
            int currentPoints = oldPoints+newPoints;
            pointsTotal.setText(String.valueOf(currentPoints));

            // deactivate game for current item
            currentItem.setMarker(ContextCompat.getDrawable(this, R.drawable.marker_deactivated));

            // rating stars as feedback
            RatingBar stars = new RatingBar(this);
            stars.setNumStars(3);
            stars.setMax(3);
            stars.setIsIndicator(true);
            stars.setRating(newPoints);
            stars.setLayoutParams(new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,MapView.LayoutParams.WRAP_CONTENT,currentItem.getPoint(),MapView.LayoutParams.CENTER,0,0));
            stars.setScaleX(0.5f);
            stars.setScaleY(0.5f);
            map.addView(stars,stars.getLayoutParams());


        }
    }

    public void setupEvaluation(){
        // Add Items for Evaluation study
        OverlayItem sonnenzeichen = new OverlayItem("Sonnenzeichen", "memorial", new GeoPoint(49.78237,9.96784));
        sonnenzeichen.setMarker(getIcon("memorial"));
        OverlayItem roterPlatz = new OverlayItem("Roter Platz", "memorial", new GeoPoint(49.78215,9.96807));
        roterPlatz.setMarker(getIcon("memorial"));
        OverlayItem minimalflaeche = new OverlayItem("Ennepersche Minimalflaeche", "memorial", new GeoPoint(49.78454,9.97312));
        minimalflaeche.setMarker(getIcon("memorial"));
        OverlayItem museum = new OverlayItem("Mineralogisches Museum", "museum", new GeoPoint(49.78235,9.97019));
        museum.setMarker(getIcon("museum"));
        OverlayItem kopf = new OverlayItem("Denker-Kopf", "sculpture", new GeoPoint(49.78331,9.97058));
        kopf.setMarker(getIcon("sculpture"));

        // Add order numbers for study
        List<Integer> orderList = Arrays.asList(1,2,3,4,5);
        Collections.shuffle(orderList);
        List<OverlayItem> studyItems = Arrays.asList(sonnenzeichen,roterPlatz,minimalflaeche,museum,kopf);

        for(int i=0;i<5;i++){
            OverlayItem item = studyItems.get(i);
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

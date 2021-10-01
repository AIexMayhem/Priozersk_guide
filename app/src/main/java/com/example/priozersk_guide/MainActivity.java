package com.example.priozersk_guide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private static final String KORELA_FORTRESS_ID = "korela", TANK_ID = "tank";


    Creator korelaFortress = new Creator(30.122570030590303, 61.030067073282005, KORELA_FORTRESS_ID),
            tank = new Creator(30.102570030590303, 61.050067073282005, TANK_ID);

    private static final String LAYER_ID = "layerId",
            SOURCE_ID = "sourceId";
    static final String ICON_ID = "iconId";

    static List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

    private Button findPriozerskButton;
    private MapView mapView;
    private MapboxMap mapboxMap;

    private double camLat, camLtg;

    Point activatedMarker = null;
    String idOfActivatedMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.map_layout);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        findPriozerskButton = findViewById(R.id.findPriozerskButton);

        findPriozerskButton.animate().translationY(-200);

        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        mapboxMap.setStyle(new Style.Builder().fromUri(getString(R.string.map_url))

                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))

                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        )
                ), new Style.OnStyleLoaded() {

                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        MainActivity.this.mapboxMap = mapboxMap;

                        mapboxMap.getUiSettings().setCompassEnabled(false);

                        mapboxMap.addOnMapClickListener(MainActivity.this);
                        mapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {
                                updateCordsOfCamera();
                                if (checkCamera())
                                    findPriozerskButton.animate().translationY(0).setDuration(100);
                                else
                                    findPriozerskButton.animate().translationY(-200).setDuration(100);

                            }
                        });
                    }
                }
        );
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> feature = mapboxMap.queryRenderedFeatures(screenPoint, LAYER_ID);

        if (!feature.isEmpty()) {
            idOfActivatedMarker = feature.get(0).properties().toString();
            idOfActivatedMarker = idOfActivatedMarker.substring(11, idOfActivatedMarker.length() - 2);
            Intent sight = new Intent(this, Sightpoint.class);
            sight.putExtra("id_of_point", idOfActivatedMarker);
            startActivity(sight);
            return true;
        }
        else {
            hideInterface();
        }
        return false;
    }

    private void hideInterface() {
        if (activatedMarker != null) {
            updateCordsOfCamera();
            if (checkCamera() && findPriozerskButton.getTranslationY() == 0.0)
                findPriozerskButton.animate().translationY(-200).setDuration(100);
            else if (checkCamera()) {
                findPriozerskButton.animate().translationY(0).setDuration(100);
            }
        }

        else if (findPriozerskButton.getTranslationY() == 0.0) {
            findPriozerskButton.animate().translationY(-200).setDuration(100);
        }

        else {
            updateCordsOfCamera();
            if (checkCamera())
                findPriozerskButton.animate().translationY(0).setDuration(100);
        }
    }

    private void setCameraPosition(double latitude, double longitude) {
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(12.5)
                .bearing(0)
                .build()),
                1500);
    }

    public void setDefaultCameraPosition(View view) {
        setCameraPosition(61.0362, 30.1132);
        findPriozerskButton.animate().translationY(-200).setDuration(100);
    }

    private void updateCordsOfCamera() {
        CameraPosition currentCameraPosition = mapboxMap.getCameraPosition();
        camLat = currentCameraPosition.target.getLatitude();
        camLtg = currentCameraPosition.target.getLongitude();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private boolean checkCamera() {
        return 60.9 > camLat || camLat > 61.15 || 29.8 > camLtg || camLtg > 30.2;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}

class Creator {
    private final double longitude, latitude;
    private final String id;

    Creator(double longitude, double latitude, String id) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        addPointToList();
    }

    private void addPointToList() {
        Feature singleFeature = Feature.fromGeometry(
                Point.fromLngLat(longitude, latitude));
        singleFeature.addStringProperty(MainActivity.ICON_ID, id);
        MainActivity.symbolLayerIconFeatureList.add(singleFeature);
    }
}

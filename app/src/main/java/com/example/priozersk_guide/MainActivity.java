package com.example.priozersk_guide;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

    private int peekHeight;

    private static final String KORELA_FORTRESS_ID = "korelaId";

    Creator korelaFortress = new Creator(30.122570030590303, 61.030067073282005, KORELA_FORTRESS_ID);

    private static final String LAYER_ID = "layerId", SOURCE_ID = "sourceId";
    static final String ICON_ID = "iconId";

    static List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

    private ImageButton setHalfStateButton;
    private BottomSheetBehavior behavior;
    private WebView web;
    private Button findPriozerskButton;
    private MapView mapView;
    private MapboxMap mapboxMap;

    private CameraPosition currentCameraPosition;
    private double camLat, camLtg;

    Point activatedMarker = null;
    String idOfActivatedMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.coordinator_layout);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        behavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        behavior.setHalfExpandedRatio((float) 0.7);
        web = findViewById(R.id.webview);
        setHalfStateButton = findViewById(R.id.setHalfStateButton);
        findPriozerskButton = findViewById(R.id.findPriozerskButton);

        findPriozerskButton.animate().translationY(-200);
        peekHeight = dpToPx(33);
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
                                updateCoordsOfCamera();
                                if (checkCamera())
                                    findPriozerskButton.animate().translationY(0).setDuration(100);
                                else
                                    findPriozerskButton.animate().translationY(-200).setDuration(100);

                            }
                        });

                        behavior.addBottomSheetCallback(
                                new BottomSheetBehavior.BottomSheetCallback() {
                                    @Override
                                    public void onStateChanged(View bottomSheet, int newState) {
                                        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                            setHalfStateButton.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            setHalfStateButton.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                                        if (slideOffset > 0.01)
                                            findPriozerskButton.setVisibility(View.INVISIBLE);
                                        else
                                            findPriozerskButton.setVisibility(View.VISIBLE);

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

        if (!feature.isEmpty() && behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setPeekHeight(peekHeight, true);
            behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

            activatedMarker = (Point) feature.get(0).geometry();
            setCameraPosition(activatedMarker.latitude() - 0.013, activatedMarker.longitude());
            return true;
        }
        else {
            hideInterface();
        }
        return false;
    }

    private void hideInterface() {
        if (behavior.getPeekHeight() == 0 && activatedMarker != null) {
            updateCoordsOfCamera();
            if (checkCamera() && findPriozerskButton.getTranslationY() == 0.0)
                findPriozerskButton.animate().translationY(-200).setDuration(100);
            else if (checkCamera()) {
                behavior.setPeekHeight(peekHeight, true);
                findPriozerskButton.animate().translationY(0).setDuration(100);
            }
            else
                behavior.setPeekHeight(peekHeight, true);
        }

        else if (behavior.getPeekHeight() == peekHeight) {

            findPriozerskButton.animate().translationY(-200).setDuration(100);
            behavior.setPeekHeight(0, true);
        }

        else if (findPriozerskButton.getTranslationY() == 0.0) {
            findPriozerskButton.animate().translationY(-200).setDuration(100);
        }

        else {
            updateCoordsOfCamera();
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

    public void setHalfState(View view) {
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
    }

    private void updateCoordsOfCamera() {
        currentCameraPosition = mapboxMap.getCameraPosition();
        camLat = currentCameraPosition.target.getLatitude();
        camLtg = currentCameraPosition.target.getLongitude();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private boolean checkCamera() {
        if (60.9 > camLat || camLat > 61.15 || 29.8 > camLtg || camLtg > 30.2)
            return true;
        return false;
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
    private double longitude, latitude;
    private String id;

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

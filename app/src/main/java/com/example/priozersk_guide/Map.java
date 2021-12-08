package com.example.priozersk_guide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
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

public class Map extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener {

    static final String ICON_ID = "iconId", DESCRIPTION = "desc";
    private static final String KORELA_FORTRESS_ID = "korela", RAILWAY_ID = "railway", KIRHA_FORTRESS_ID = "kirha",
            BRIDGE_ID = "bridge", CHURCH_ID = "church";

    private static final String LAYER_ID = "layerId",
            SOURCE_ID = "sourceId";
    Creator korelaFortress = new Creator(30.122570030590303, 61.030067073282005, KORELA_FORTRESS_ID,
            "    Корела — каменная крепость в городе Приозерске, " +
                    "на острове реки Вуоксы, сыгравшая значительную роль в истории Карельского перешейка и допетровской России. " +
                    "Сохранившиеся помещения крепости в настоящее время занимает историко-краеведческий музей «Крепость Корела».\n" +
                    "   Корела — каменная крепость в городе Приозерске, " +
                    "на острове реки Вуоксы, сыгравшая значительную роль в истории Карельского перешейка и допетровской России.\n" +
                    "   Сохранившиеся помещения крепости в настоящее время занимает историко-краеведческий музей «Крепость Корела»."),
            railway = new Creator(30.10279, 61.03591, RAILWAY_ID, "Приозерский вокзал — станция Октябрьской железной дороги, в городе Приозерск Ленинградской области.\n" +
                    "   Здание железнодорожного вокзала является выявленным объектом культурного наследия народов России.\n" +
                    "Впервые предложение о строительстве железной дороги, которая проходила бы через Кексгольм, " +
                    "поступило в 1887 году от помещика Финляндского княжества из Сумпула.\n" +
                    "Станция введена в промышленную эксплуатацию и открыта для пассажиров и перевозки грузов в 1916 году. " +
                    "Железная дорога от Санкт-Петербурга до Хийтолы (179 км.) была построена компанией «Suomen Valtion Rautatiet» и полностью введена в эксплуатацию к началу 1917 года.\n" +
                    "С 1918 по 1948 год станция именовалась как Кякисалми. Также, с 1916 по 1948 год использовалось и шведское название — Кексгольм. " +
                    "С 1918 по 1940 годы станция и город находились в составе Финляндии[2].\n" +
                    "Электрифицирована в 1975—1976 годах, в составе участка Сосново — Приозерск — Кузнечное."
            ),
            kirhaFortress = new Creator(30.1154, 61.03798, KIRHA_FORTRESS_ID, "sgdgs"),
            bridge = new Creator(30.15883, 61.04171, BRIDGE_ID, "sgsgdgds"),
            church = new Creator(30.11393, 61.03561, CHURCH_ID, "sdgsgdsgdsgsdgdsgsdgdgsdsgs");

    static List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    int cnt = 0;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button findPriozerskButton, openBar, toMenu;
    String dataPoint, idOfActivatedMarker, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.map_layout);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        findPriozerskButton = findViewById(R.id.findPriozerskButton);
        openBar = findViewById(R.id.openBar);
        toMenu = findViewById(R.id.returnToMainMenu);
        findPriozerskButton.animate().translationY(-200).setDuration(0);
        toMenu.animate().translationY(-200).setDuration(0);

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
                        Map.this.getResources(), R.drawable.mapbox_marker_icon_default))

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
                        Map.this.mapboxMap = mapboxMap;

                        mapboxMap.getUiSettings().setCompassEnabled(false);

                        mapboxMap.addOnMapClickListener(Map.this);

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
            dataPoint = feature.get(0).properties().toString();
            idOfActivatedMarker = dataPoint.substring(11, dataPoint.indexOf(",") - 1);
            description = dataPoint.substring(dataPoint.indexOf(",") + 9, dataPoint.length() - 2);
            Intent sight = new Intent(this, Sightpoint.class);

            sight.putExtra("id_of_point", idOfActivatedMarker);
            sight.putExtra("desc", description);
            startActivity(sight);
            return true;
        }

        return false;
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
        toMenu.animate().translationY(-200).setDuration(100);
        cnt = 1 - cnt;
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

    public void OpenBar(View view) {
        if (cnt == 0) {

            findPriozerskButton.animate().translationY(0).setDuration(200);
            toMenu.animate().translationY(0).setDuration(200);
            openBar.setRotation(180);
        } else {
            openBar.setRotation(90);
            findPriozerskButton.animate().translationY(-600).setDuration(200);
            toMenu.animate().translationY(-600).setDuration(200);
        }
        cnt = 1 - cnt;
    }

    public void returnToMainMenu(View view) {
        Intent sight = new Intent(this, MainMenu.class);
        startActivity(sight);
    }
}

class Creator {
    private final double longitude, latitude;
    private final String id;
    public String description;

    Creator(double longitude, double latitude, String id, String description) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.description = description;
        addPointToList();
    }

    private void addPointToList() {
        Feature singleFeature = Feature.fromGeometry(
                Point.fromLngLat(longitude, latitude));
        singleFeature.addStringProperty(Map.ICON_ID, id);
        singleFeature.addStringProperty(Map.DESCRIPTION, description);
        Map.symbolLayerIconFeatureList.add(singleFeature);
    }

}

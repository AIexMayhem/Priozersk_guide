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
            "   Корела — каменная крепость в городе Приозерске, " +
                    "на острове реки Вуоксы, сыгравшая значительную роль в истории Карельского перешейка и допетровской России. " +
                    "Сохранившиеся помещения крепости в настоящее время занимает историко-краеведческий музей «Крепость Корела». " +
                    "Средневековая Корела была самым северо-западным городом Руси. " +
                    "Крепость была заложена на рубеже XIII и XIV вв. новгородцами на острове реки Узерве (Вуоксы) для защиты северо-западных рубежей республики от шведов. " +
                    "Деревянные стены 1310 года постройки через 50 лет погибли в пожаре настолько сильном, что «городчане только душами осташася». " +
                    "В XIV веке Корела занимала площадь в 6 тыс. м²., состояла из сотни бревенчатых домов, а ее население состояло из 300 человек. " +
                    "Во время Ливонской войны, в 1580 году, обветшавший детинец был захвачен шведами, которые отстроили его заново. " +
                    "По Тявзинскому миру 1595 года крепость вернулась к России. Василий Шуйский пообещал Корелу с уездом Делагарди за помощь в усмирении Смуты. " +
                    "Однако местное население отказывалось признавать условия договора, и в итоге шведам пришлось силой подчинить себе крепость в 1610 году. " +
                    "В отсутствие регулярных войск, для защиты Корелы было собрано ополчение из местного населения. " +
                    "На защиту крепости встали 2000 ополченцев и 500 стрельцов под командованием воевод И. М. Пушкина, А. Безобразова, В. Абрамова и епископа Сильвестра. " +
                    "С сентября 1610 года по март 1611 года продолжалась осада крепости войсками Делагарди, завершившаяся полным истощением сил защитников и сдачей Корелы. " +
                    "В результате, на протяжении столетия, с 1611 по 1710 годы, крепость оставалась шведской и именовалась Кексгольм." +
                    "В 1710 году, во время Северной войны, крепость была отвоёвана у шведов, а к окончанию Русско-шведской войны 1808—1809 годов утратила своё приграничное значение." +
                    "В XVIII—XIX веках служила тюрьмой для политзаключённых (в частности, здесь содержались семья Емельяна Пугачёва и декабристы)." +
                    "Летом 1948 года были начаты раскопки на территории старой крепости. В октябре этого же года Кякисалми переименовали в Приозерск. " +
                    "С августа 1960 года начинаются работы по реставрации крепости Корела, а с 17 июня 1962 года начинается история этой крепости как историко-краеведческого музея. " +
                    "25 июля 1988 года Кексгольмский герб 1788 года был утверждён в качестве герба Приозерска."),
            railway = new Creator(30.10279, 61.03591, RAILWAY_ID,
                    "   Приозерский вокзал — станция Октябрьской железной дороги, в городе Приозерск Ленинградской области." +
                            "Здание железнодорожного вокзала является выявленным объектом культурного наследия народов России. " +
                            "Впервые предложение о строительстве железной дороги, которая проходила бы через Кексгольм, " +
                            "поступило в 1887 году от помещика Финляндского княжества из Сумпула." +
                            "Станция введена в промышленную эксплуатацию и открыта для пассажиров и перевозки грузов в 1916 году. " +
                            "Железная дорога от Санкт-Петербурга до Хийтолы (179 км.) была построена компанией «Suomen Valtion Rautatiet» и полностью введена в эксплуатацию к началу 1917 года. " +
                            "С 1918 по 1948 год станция именовалась как Кякисалми. Также, с 1916 по 1948 год использовалось и шведское название — Кексгольм. " +
                            "С 1918 по 1940 годы станция и город находились в составе Финляндии" +
                            "Электрифицирована в 1975—1976 годах, в составе участка Сосново — Приозерск — Кузнечное."
            ),
            kirhaFortress = new Creator(30.1154, 61.03798, KIRHA_FORTRESS_ID,
                    "   В разное время, за многовековую историю города, было построено 10 храмов, но проблема нехватки мест для прихожан всегда была актуальной. " +
                            "Предпоследняя кирха была возведена в 1759 году знаменитым финским архитектором Туомасом Суйкканеном во имя Святого Андреаса, " +
                            "но она по прежнему не могла вместить всех желающих, особенно в крупные церковные праздники." +
                            "В 1940 году, в ходе бомбардировок советской авиацией церковное здание получило серьёзные повреждения: одна из бомб, проломив крышу и перекрытия, " +
                            "взорвалась на полу и разметала скамейки. " +
                            "Храм был отремонтирован финнами, вернувшимися в свои дома, и снова оставлен при отступлении в 1944 году. " +
                            "Здание кирхи в 1945 году было передано под управление местной партийной ячейки. " +
                            "Была сооружена сцена, уничтожены следы религии(в том числе сдёрнули трактором массивный гранитный крест с бельведера здания), " +
                            "церковь преобразована в «Городской Дом культуры». В здании проводились политзанятия и концерты самодеятельности. " +
                            "Через 15 лет остро встал вопрос о капитальном ремонте здания кирхи. " +
                            "Осенью 1961 года, расширили сцену и переоборудовали фойе. Кроме того, кардинально были реконструированы подсобные помещения: туалеты, касса, кладовки. " +
                            "Под крышей сделали чердачный полуэтаж с классами для кружковой работы. В 1990 году Приозерск посетил ярославский поэт Василий Пономаренко. " +
                            "Он был так раздосадован дискотеками в храме, что написал сатирическое стихотворение, посвящённое этому визиту."),

    bridge = new Creator(30.15883, 61.04171, BRIDGE_ID,
            "   В месте впадения реки Вуокса в Ладожское озеро есть пешеходный деревянный мост. " +
                    "Он построен в 1916 году и был обновлен в октябре 2018 года. С моста открывается красивый вид на воду. " +
                    "Его длина составляет порядка 200 метров. Рядом есть яхт-клуб. " +
                    "С Бумовского моста открываются красивые виды. Виден Валаамский причал, с стоящими у него яхтами и корабликами. " +
                    "Виден яхт-клуб, с разнообразными лодками, баркасами, яхточками."),
            church = new Creator(30.11393, 61.03561, CHURCH_ID,
                    "   Храм Рождества Пресвятой Богородицы построен на центральной площади города в 1836-1847 гг., " +
                            "по проекту архитектора Д.Висконти. Работы по строительству собора возглавил Николай Лисицин. " +
                            "Это не первый храм в Приозерске, освященная в честь Рождества Пресвятой Богородицы. " +
                            "Именно в день празднования Рождества Пресвятой Богородицы, 8 сентября 1710 года, город Кексгольм (Приозерск) " +
                            "был взят русскими войсками под предводительством генерала Р.В.Брюса. После освобождения от шведского владычества на территории Новой крепости " +
                            "(бывший Спасский остров) под православный соборный храм была переосвящена каменная лютеранская кирха, возведенная еще в 1692 году. " +
                            "После учреждения в 1758 году Кексгольмской кафедры храм стал кафедральным собором Кексгольмской и Ладожской епархии, которую, с 1761 по 1763 гг. " +
                            "возглавлял будущий святитель Тихон Задонский (Соколов). Впоследствии старый, ветхий храм был разобран, " +
                            "а его имущество было перенесено в новый храм на Соборной площади. " +
                            "В 1940 г. храм Рождества Пресвятой Богородицы был закрыт, в 1992 году — возвращен верующим, а в 1995 году передан подворью Коневского монастыря. " +
                            "В 1999 году поставлена и освящена часовня во имя преподобного Арсения Коневского.");

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

package com.example.priozersk_guide;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {
    int cnt1 = 0, cnt2 = 0;
    TextView textBar;
    String text1 = "    Первое упоминание об укреплённом поселении на месте нынешнего города относится к 1295 году. В русской хронике оно называлось Корела, в шведской — Кексгольм.\n" +
            "   C XIV века по 1611 год город был известен как Корела. " +
            "C 1580 по 1595 и с 1611 по 1918 годы город назывался Кексгольм. " +
            "После введения с 1860-х годов в официальное делопроизводство Великого княжества Финляндского финского языка распространяется и финский вариант Кякисалми, " +
            "о чём свидетельствуют почтовые штампы. С 1918 года основным вариантом названия города, " +
            "в составе получившей независимость Финляндии, стало финское Кякисалми, наряду со шведским Кексгольм.\n" +
            "   В 1940 году после советско-финской войны город отошёл к Советскому Союзу, название Кексгольм было возвращено. " +
            "В 1941—1944 годах во время советско-финской войны город был занят войсками Финляндии и назывался Кякисалми. " +
            "В 1944 году после Московского перемирия город вторично отошёл к Советскому Союзу. В 1948 году был переименован в Приозерск.",

    text2 = "Информация взята из источника www.wikipedia.org";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        textBar = findViewById(R.id.textBar);
        textBar.setMovementMethod(new ScrollingMovementMethod());
        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void startTour(View view) {
        Intent sight = new Intent(this, Map.class);
        startActivity(sight);
        finish();
    }

    public void aboutTown(View view) {
        if (cnt2 == 0 && cnt1 == 0)
            textBar.setText(text1);
        else if (cnt1 == 1)
            textBar.setText("");
        else if (cnt2 == 1) {
            textBar.setText(text1);
            cnt2 = 0;
        }
        cnt1 = 1 - cnt1;

    }

    public void aboutApp(View view) {
        if (cnt2 == 0 && cnt1 == 0)
            textBar.setText(text2);
        else if (cnt2 == 1)
            textBar.setText("");
        else if (cnt1 == 1) {
            textBar.setText(text2);
            cnt1 = 0;
        }
        cnt2 = 1 - cnt2;
    }
}
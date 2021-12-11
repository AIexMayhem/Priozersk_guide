package com.example.priozersk_guide;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Sightpoint extends AppCompatActivity {
    TextView textBar;
    Button openTextBar, returnToMap, openBar, closeText;
    int cnt = 0, cnt2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sightpoint);

        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        textBar = findViewById(R.id.TextBar);
        openTextBar = findViewById(R.id.openTextBar);
        openBar = findViewById(R.id.openBar);
        returnToMap = findViewById(R.id.returnToMainScreen);
        closeText = findViewById(R.id.close);

        openBar.setRotation(90);
        textBar.setMovementMethod(new ScrollingMovementMethod());

        textBar.animate().translationY(-1000).setDuration(0);
        closeText.animate().translationX(-100).setDuration(0);
        closeText.animate().translationY(-1000).setDuration(0);
        openTextBar.animate().translationY(-600).setDuration(0);
        returnToMap.animate().translationY(-600).setDuration(0);

        Intent landmark = getIntent();


        WebView panorama = findViewById(R.id.Web);
        WebSettings settings = panorama.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDomStorageEnabled(true);

        String idOfActivatedMarker = landmark.getStringExtra("id_of_point");
        String description = landmark.getStringExtra("desc");
        panorama.loadUrl("http://cy32364.tmweb.ru/" + idOfActivatedMarker + ".html");
        textBar.setText(description);
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

    public void OpenBar(View view) {
        if (cnt == 0) {

            openTextBar.animate().translationY(0).setDuration(200);
            returnToMap.animate().translationY(0).setDuration(200);
            openBar.setRotation(180);
        } else {
            openBar.setRotation(90);
            openTextBar.animate().translationY(-600).setDuration(200);
            returnToMap.animate().translationY(-600).setDuration(200);
        }
        cnt = 1 - cnt;
    }

    public void OpenTextBar(View view) {
        if (cnt2 == 0) {
            textBar.animate().translationY(0).setDuration(200);
            closeText.animate().translationY(5).setDuration(200);

        } else {
            textBar.animate().translationY(-1000).setDuration(200);
            closeText.animate().translationY(-1000).setDuration(200);

        }
        openTextBar.animate().translationY(-600).setDuration(200);
        returnToMap.animate().translationY(-600).setDuration(200);
        openBar.setRotation(90);
        cnt2 = 1 - cnt2;
    }

    public void returnToMainScreen(View view) {
        finish();
    }

    public void closeText(View view) {

        cnt = 1 - cnt;
        cnt2 = 1 - cnt2;
        textBar.animate().translationY(-1000).setDuration(200);
        closeText.animate().translationY(-1000).setDuration(200);

    }
}
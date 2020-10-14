package com.example.ppp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import async_tasks.feeds.BuildGuideTask;
import preferences.Preferences;


public class buildGuideActivity extends AppCompatActivity {
    Preferences prefs;
    Context context;
    LinearLayout dialog;
    View loadingWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_search);
    }

    @Override
    protected void onResume(){
        prefs = new Preferences(buildGuideActivity.this);
        context = buildGuideActivity.this;
        dialog = findViewById(R.id.searchID);
        loadingWheel = findViewById(R.id.loading_wheel);
        loadingWheel.setVisibility(View.VISIBLE);
        // remove footer
        RelativeLayout footer = findViewById(R.id.footer);
        footer.setVisibility(View.GONE);
        dialog.removeAllViews();
        new BuildGuideTask(context, dialog, loadingWheel, prefs).execute();
        // addSavedBuilds();
        super.onResume();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            goToSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToSettings() {
        Intent intent = new Intent("com.iphonik.chameleon.Settings");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToSettings(View view){
        Intent intent = new Intent("com.iphonik.chameleon.Settings");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loadingDone(){
        findViewById(R.id.loading_wheel).setVisibility(View.GONE);
    }

    private void loadingNotDone(){
        findViewById(R.id.loading_wheel).setVisibility(View.VISIBLE);
    }

}
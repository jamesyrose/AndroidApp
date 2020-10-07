package com.example.ppp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import async_tasks.db.updateSQL;
import async_tasks.feeds.SavedBuildTask;
import pcpp_data.products.GeneralProduct;
import pcpp_data.sqllite.database;
import pcpp_data.sqllite.saveBuilds;
import preferences.Preferences;


public class savedBuildsActivity extends AppCompatActivity {
    Preferences prefs;
    Context context;
    LinearLayout dialog;
    View loadingWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(savedBuildsActivity.this);
        context = savedBuildsActivity.this;
        setContentView(R.layout.scroll_search);
        dialog = findViewById(R.id.searchID);
        loadingWheel = findViewById(R.id.loading_wheel);
        loadingWheel.setVisibility(View.VISIBLE);
        // remove footer
        RelativeLayout footer = findViewById(R.id.footer);
        footer.setVisibility(View.GONE);
    }

    @Override
    protected void onResume(){
        dialog.removeAllViews();
        new SavedBuildTask(context, dialog, loadingWheel, prefs).execute();
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
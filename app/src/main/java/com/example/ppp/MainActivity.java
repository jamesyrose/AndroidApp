package com.example.ppp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import async_tasks.db.checkDataBaseStruct;
import async_tasks.db.updateSQL;
import pcpp_data.products.MemoryProduct;
import pcpp_data.sqllite.database;
import pcpp_data.sqllite.saveBuilds;
import pcpp_data.sqllite.setDefaultDatabase;
import preferences.Preferences;


public class MainActivity extends AppCompatActivity {
    database db;
    Preferences prefs;
    Context context;
    LinearLayout dialog;
    static RelativeLayout loadingWheel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(MainActivity.this);
        context = MainActivity.this;
        setContentView(R.layout.activity_main);
        dialog = findViewById(R.id.main_vert_layout);

        loadingWheel = findViewById(R.id.loading_data);

        boolean newDB = new setDefaultDatabase(context).copyDbIfNotExists();

        if (!newDB){
            db = new database(MainActivity.this);
            // db.dropTable("SavedBuild");
            db.createSavedBuilds();
            String [] tables = new String[] {"BuildGuide", "ProductMain","Images","Rating","Price",
                    "CPU", "CPU_Cooler", "Memory", "Motherboard",  "PSU", "Cases", "ExchangeRates",
                    "GPU", "Storage"};
            for (String table : tables){
                System.out.println("# UPDATING DATA");
                updateSQL sqlTask = new updateSQL(MainActivity.this);
                sqlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, table );
            }
            new checkDataBaseStruct(context, loadingWheel, prefs).execute();
        }else{
            loadingWheel.setVisibility(View.GONE);
        }

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



    private void loadingDone(){
        loadingWheel.setVisibility(View.GONE);
    }

    private void loadingNotDone(){
        loadingWheel.setVisibility(View.VISIBLE);
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

    public void setComponentSearch(View view){
        Intent intent = new Intent("com.iphonik.chameleon.searchComponents");
        startActivity(intent);
         overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToSavedBuilds(View view){
        Intent intent = new Intent("com.iphonik.chameleon.savedBuild");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToBuildGuide(View view){
        Intent intent = new Intent("com.iphonik.chameleon.buildGuide");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }



    public void goToBuildPc(View view){
        Intent intent = new Intent("com.iphonik.chameleon.buildPc");
        String sql = "SELECT buildID FROM `SavedBuild` WHERE saved = 0 ORDER BY added DESC; ";
        JSONArray data = new database(context).getData(sql);
        String randomID = randomBuildID();
        try{
            JSONObject buff = (JSONObject) data.get(0);
            randomID = (String) buff.get("buildID");
        }catch(Exception e){

        }
        intent.putExtra("BUILD_ID", randomID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public String randomBuildID(){
        String SALTCHARS = "abcdefghijklmnopqrztuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
package com.example.ppp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Random;

import async_tasks.db.updateSQL;
import pcpp_data.products.MemoryProduct;
import pcpp_data.sqllite.database;
import pcpp_data.sqllite.saveBuilds;
import preferences.Preferences;


public class MainActivity extends AppCompatActivity {
    database db;
    Preferences prefs;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(MainActivity.this);
        context = MainActivity.this;
        setContentView(R.layout.activity_main);
        db = new database(MainActivity.this);
        // db.dropTable("SavedBuild");
        db.createSavedBuilds();
        if (prefs.dbNeedUpdate()){
            System.out.println("# UPDATING DATA");
            updateSQL sqlTask = new updateSQL(MainActivity.this);
            sqlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "" );
        } else{
            System.out.println("DOEST NEED UPDATE");
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
package com.example.ppp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import async_tasks.db.updateSQL;
import pcpp_data.sqllite.database;
import preferences.Preferences;


public class MainActivity extends AppCompatActivity {
    database db;
    Preferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(MainActivity.this);
        setContentView(R.layout.activity_main);
        db = new database(MainActivity.this);

        if (prefs.dbNeedUpdate()){
            System.out.println("# UPDATING DATA");
            updateSQL sqlTask = new updateSQL(MainActivity.this);
            sqlTask.execute();
        } else{
            System.out.println("DOEST NEED UPDATE");
        }
    }

    public void setComponentSearch(View view){
        Intent intent = new Intent("com.iphonik.chameleon.searchComponents");
        startActivity(intent);
         overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
}
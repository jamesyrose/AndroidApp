package com.example.ppp;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

import async_tasks.DownloadCpuSpecs;
import async_tasks.DownloadSellers;
import async_tasks.RetrieveCpuFeedTask;
import pcpp_data.queries.CpuSearch;
import pcpp_data.queries.SingleCpuQuery;
import preferences.Preferences;

public class cpuSearch extends AppCompatActivity {
    private static RetrieveCpuFeedTask cpuFeed;
    Preferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpu_search);
        loadingNotDone();
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.searchID);

        prefs = new Preferences(cpuSearch.this);
        cpuFeed = new RetrieveCpuFeedTask(cpuSearch.this, dialog, prefs);
        RetrieveCpuData();

        // Set filter
        Button filter = findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                filterPopup(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.cpu_filter_window, null);

        LinearLayout mainLayout  = popupView.findViewById(R.id.main_vert_layout);
        // Set the Branch Choices
        LinearLayout brandChoice = mainLayout.findViewById(R.id.brand_selection);
        final LinearLayout brandOptions = mainLayout.findViewById(R.id.brand_options);
        brandOptions.setVisibility(View.GONE);
        brandChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brandOptions.isShown()){
                    brandOptions.setVisibility(View.GONE);
                }else{
                    brandOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set price choices
        final RangeSeekBar<Integer> priceBar = popupView.findViewById(R.id.price_seek_bar);
        priceBar.setRangeValues(0, getHighestPriceProdcuct() + 10);
        LinearLayout priceChoice = popupView.findViewById(R.id.price_selection);
        priceBar.setVisibility(View.GONE);
        priceChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priceBar.isShown()){
                    priceBar.setVisibility(View.GONE);
                }else{
                    priceBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set Core Choices
        final RangeSeekBar<Integer> coresBar = popupView.findViewById(R.id.cores_seek_bar);
        coresBar.setRangeValues(0, 64);
        LinearLayout coresChoice = popupView.findViewById(R.id.cores_selection);
        coresBar.setVisibility(View.GONE);
        coresChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coresBar.isShown()){
                    coresBar.setVisibility(View.GONE);
                }else{
                    coresBar.setVisibility(View.VISIBLE);
                }
            }
        });


        // Set Clock Speed Choice
        final LinearLayout clockChoice = popupView.findViewById(R.id.clock_selection);
        final LinearLayout clockOptionSection= popupView.findViewById(R.id.clock_option_section);
        final RangeSeekBar<Double> baseClockBar = popupView.findViewById(R.id.base_clock_seek_bar);
        final RangeSeekBar<Double> boostClockBar = popupView.findViewById(R.id.boost_clock_seek_bar);
        baseClockBar.setRangeValues(0.0, 6.0);
        boostClockBar.setRangeValues(0.0, 6.0);
        clockOptionSection.setVisibility(View.GONE);
        clockChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clockOptionSection.isShown()){
                    clockOptionSection.setVisibility(View.GONE);
                }else{
                    clockOptionSection.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set Core Choices
        final RangeSeekBar<Integer> tdpBar = popupView.findViewById(R.id.tdp_seek_bar);
        tdpBar.setRangeValues(0, 500);
        LinearLayout tdpChoice = popupView.findViewById(R.id.tdp_selection);
        tdpBar.setVisibility(View.GONE);
        tdpChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tdpBar.isShown()){
                    tdpBar.setVisibility(View.GONE);
                }else{
                    tdpBar.setVisibility(View.VISIBLE);
                }
            }
        });


        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.product_description_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (CpuSearch prod: cpuFeed.getSearchData()){
            double bestPrice = prod.getBestPrice();
            if (bestPrice > maxPrice)
                maxPrice = bestPrice;
        }
        return (int) Math.floor(maxPrice);
    }

    public void RetrieveCpuData(){
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.searchID);

        if (cpuFeed.getSearchData().isEmpty()){
            cpuFeed.execute();
        }else {
            for (CpuSearch product:  cpuFeed.getSearchData()){
                cpuFeed.addProduct(product);
                dialog.setVisibility(LinearLayout.VISIBLE);
                Animation animation   =    AnimationUtils.loadAnimation(cpuSearch.this, R.anim.decompress);
                animation.setDuration(1000);
                dialog.setAnimation(animation);
                dialog.animate();
                loadingDone();
            }
        }
    }


    public void goToSettings(){
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


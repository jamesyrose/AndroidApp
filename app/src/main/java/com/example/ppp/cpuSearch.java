package com.example.ppp;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;

import async_tasks.RetrieveCpuFeedTask;
import pcpp_data.queries.CpuSearch;
import pcpp_data.sorters.PopularitySort;
import preferences.Preferences;

public class cpuSearch extends AppCompatActivity {
    RetrieveCpuFeedTask cpuFeed;
    Preferences prefs;
    LinearLayout dialog;
    ArrayList<CpuSearch> productsDisplayed;
    PopupWindow filterWindow;
    PopupWindow sortWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpu_search);
        loadingNotDone();
        dialog   = (LinearLayout)findViewById(R.id.searchID);

        prefs = new Preferences(cpuSearch.this);
        if (cpuFeed == null){
            System.out.println("#DKJSHKDSJLKFHSDF");
            cpuFeed = new RetrieveCpuFeedTask(cpuSearch.this, dialog, prefs);
        }
        RetrieveCpuData();

        // Set filter
        Button filter = findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                filterPopup(v);
            }
        });

        // Set Sort
        Button sortButton = findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sortPopup(v);
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


    public void RetrieveCpuData() {
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


    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.cpu_filter_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        if (filterWindow == null){
            filterWindow = new PopupWindow(popupView, width, height, focusable);
        }

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        filterWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        LinearLayout mainLayout  = popupView.findViewById(R.id.main_vert_layout);
        // Set the Branch Choices
        final RelativeLayout brandChoice = mainLayout.findViewById(R.id.brand_selection);
        final LinearLayout brandOptions = mainLayout.findViewById(R.id.brand_options);
        final CheckBox amd_option =  popupView.findViewById(R.id.amd_option);
        final CheckBox intel_option =  popupView.findViewById(R.id.intel_option);
        amd_option.setChecked(true);
        intel_option.setChecked(true);
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
        final int maxPrice = getHighestPriceProdcuct() + 10;
        priceBar.setRangeValues(0, maxPrice);
        final RelativeLayout priceChoice = popupView.findViewById(R.id.price_selection);
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
        RelativeLayout coresChoice = popupView.findViewById(R.id.cores_selection);
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
        final RelativeLayout clockChoice = popupView.findViewById(R.id.clock_selection);
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

        // Set tdp Choices
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

        // Reset Button
        Button resetButton = popupView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               // Set both brands selected
                amd_option.setChecked(true);
                intel_option.setChecked(true);
                // price options
                priceBar.setSelectedMinValue(0);
                priceBar.setSelectedMaxValue(maxPrice);
                // Cores
                coresBar.setSelectedMinValue(0);
                coresBar.setSelectedMaxValue(64);
                // Clock speeds
                baseClockBar.setSelectedMinValue(0.0);
                baseClockBar.setSelectedMaxValue(6.0);
                boostClockBar.setSelectedMinValue(0.0);
                boostClockBar.setSelectedMaxValue(6.0);
                // tdp
                tdpBar.setSelectedMinValue(0);
                tdpBar.setSelectedMaxValue(500);

                dialog.removeAllViews();
                for (CpuSearch product: cpuFeed.getSearchData()){
                    cpuFeed.addProduct(product);
                }
                filterWindow.dismiss();
            }
        });

        Button filterButton = popupView.findViewById(R.id.apply_button);
        filterButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                ArrayList<String> manufacturers = new ArrayList<>();
                if (amd_option.isChecked()){
                    manufacturers.add("AMD");
                }
                if (intel_option.isChecked()){
                    manufacturers.add("Intel");
                }

                dialog.removeAllViews();
                for (CpuSearch product: cpuFeed.getSearchData()){
                    if (manufacturers.contains(product.getManufacturer()) &&
                            priceBar.getSelectedMinValue() < product.getBestPrice() &&
                            priceBar.getSelectedMaxValue() > product.getBestPrice()  &&
                            coresBar.getSelectedMinValue() <= stringToValue(product.getCores()) &&
                            coresBar.getSelectedMaxValue() >= stringToValue(product.getCores()) &&
                            baseClockBar.getSelectedMinValue() <= stringToValue(product.getBaseClock()) &&
                            baseClockBar.getSelectedMaxValue() >= stringToValue(product.getBaseClock()) &&
                            boostClockBar.getSelectedMinValue() <= stringToValue(product.getBoostClock())  &&
                            boostClockBar.getSelectedMaxValue() >= stringToValue(product.getBoostClock()) &&
                            tdpBar.getSelectedMinValue() <= stringToValue(product.getTdp()) &&
                            tdpBar.getSelectedMaxValue() >= stringToValue(product.getTdp())
                    ){
                        cpuFeed.addProduct(product);
                    }
                }
                filterWindow.dismiss();
            }
        });

        // get starting values to set on popup close (non apply)
        final boolean amdCheck = amd_option.isChecked();
        final boolean intelCheck = intel_option.isChecked();
        final int priceMin = priceBar.getSelectedMinValue();
        final int priceMax = priceBar.getSelectedMaxValue();
        final int coresMin = coresBar.getSelectedMinValue();
        final int coresMax = coresBar.getSelectedMaxValue();
        final double baseClockMin = baseClockBar.getSelectedMinValue();
        final double baseClockMax = baseClockBar.getSelectedMaxValue();
        final double boostClockMin = boostClockBar.getSelectedMinValue();
        final double boostClockMax = boostClockBar.getSelectedMaxValue();
        final int tdpMin = tdpBar.getSelectedMinValue();
        final int tdpMax = tdpBar.getSelectedMaxValue();

        // close button
        ImageButton closeBtn = popupView.findViewById(R.id.close_button);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // reverting selections
                amd_option.setChecked(amdCheck);
                intel_option.setChecked(intelCheck);
                priceBar.setSelectedMinValue(priceMin);
                priceBar.setSelectedMaxValue(priceMax);
                coresBar.setSelectedMinValue(coresMin);
                coresBar.setSelectedMaxValue(coresMax);
                baseClockBar.setSelectedMinValue(baseClockMin);
                baseClockBar.setSelectedMaxValue(baseClockMax);
                boostClockBar.setSelectedMinValue(boostClockMin);
                boostClockBar.setSelectedMaxValue(boostClockMax);
                tdpBar.setSelectedMinValue(tdpMin);
                tdpBar.setSelectedMaxValue(tdpMax);
                filterWindow.dismiss();
                return true;
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                amd_option.setChecked(amdCheck);
                intel_option.setChecked(intelCheck);
                priceBar.setSelectedMinValue(priceMin);
                priceBar.setSelectedMaxValue(priceMax);
                coresBar.setSelectedMinValue(coresMin);
                coresBar.setSelectedMaxValue(coresMax);
                baseClockBar.setSelectedMinValue(baseClockMin);
                baseClockBar.setSelectedMaxValue(baseClockMax);
                boostClockBar.setSelectedMinValue(boostClockMin);
                boostClockBar.setSelectedMaxValue(boostClockMax);
                tdpBar.setSelectedMinValue(tdpMin);
                tdpBar.setSelectedMaxValue(tdpMax);
                filterWindow.dismiss();
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void sortPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.cpu_sort_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        if (sortWindow == null){
            sortWindow = new PopupWindow(popupView, width, height, focusable);
        }

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        sortWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        LinearLayout mainLayout  = popupView.findViewById(R.id.main_vert_layout);
        final RadioGroup sortOptions = popupView.findViewById(R.id.sort_group);

        // Apply Button
        Button applyButton = popupView.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int selectedId = sortOptions.getCheckedRadioButtonId();
                RadioButton radioButton = sortOptions.findViewById(selectedId);
                String selectedText = radioButton.getText().toString();
                ArrayList<CpuSearch> sorted = cpuFeed.getSearchData();
                if (selectedText.toLowerCase().contains("popularity")){
                    sorted = new PopularitySort().sortPopularity(cpuFeed.getSearchData());
                }

                dialog.removeAllViews();
                if (selectedText.toLowerCase().contains("descending")){
                    Collections.reverse(sorted);
                    for (CpuSearch product: sorted){
                        cpuFeed.addProduct(product);
                    }
                }else {
                    for (CpuSearch product : sorted) {
                        cpuFeed.addProduct(product);
                    }
                }
                sortWindow.dismiss();
            }
        });


        // close button
        ImageButton closeBtn = popupView.findViewById(R.id.close_button);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sortWindow.dismiss();
                return true;
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sortWindow.dismiss();
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

    private Double stringToValue(String val){
        if (val != null){
            String value = val.replaceAll("[^0-9.]", "");
            if (value == ""){
                return 0.0;
            }
            return Double.valueOf(value);
        }
        return 0.0;
    }

}


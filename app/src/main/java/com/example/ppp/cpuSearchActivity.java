package com.example.ppp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;

import async_tasks.general.RetrieveCpuFeedTask;
import pcpp_data.products.CpuSearchProduct;
import pcpp_data.sorters.CpuProductSort;
import preferences.Preferences;

public class cpuSearchActivity extends AppCompatActivity {
    static RetrieveCpuFeedTask cpuFeed;
    Preferences prefs;
    LinearLayout dialog;
    PopupWindow filterWindow;
    PopupWindow sortWindow;
    Context context; 

    // Data filters
    boolean amdSelected = true;
    boolean intelSelected = true;
    int priceMin = 0;
    int priceMax = 1000000;
    int coreMin = 0;
    int coreMax = 64;
    double baseClockMin = 0.0;
    double baseClockMax = 10.0;
    double boostClockMin = 0.0;
    double boostClockMax = 10.0;
    int tdpMin = 0;
    int tdpMax=10000;
    String sortFilter = "Popularity (Ascending)";

    // data
    ArrayList<CpuSearchProduct> filteredData;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_search);
        context = cpuSearchActivity.this;
        loadingNotDone();
        dialog = (LinearLayout) findViewById(R.id.searchID);

        prefs = new Preferences(context);

        cpuFeed = new RetrieveCpuFeedTask(context, dialog, prefs);
        cpuFeed.execute();

        // Set filter
        Button filter = findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                filterPopup(v);
            }
        });

        // Set Sort
        Button sortButton = findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sortPopup(v);
            }
        });

//        // Set Scroll listener
        final ScrollView dialogScroll = findViewById(R.id.scroll_window);
        dialogScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                dialogScroll.getY();
                int scrollY = dialogScroll.getScrollY() + dialogScroll.getHeight(); // For ScrollView
                View lastView = dialog.getChildAt(dialog.getChildCount() - 1 );
                float lastViewY = lastView.getY();
                System.out.println(dialog.getChildCount());
                if (scrollY > lastViewY){
                    loadingNotDone();
                    onLoadMore();
                    loadingDone();
                }

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


    public void onLoadMore(){
        int currentChildCount = dialog.getChildCount();
        ArrayList<CpuSearchProduct> data = cpuFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            cpuFeed.addProduct(data.get(i));
        }

    }


    @SuppressLint("ClickableViewAccessibility")
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

                filteredData = cpuFeed.getSearchData();
                dialog.removeAllViews();
                for (int i=0; i<30; i++){
                    cpuFeed.addProduct(filteredData.get(i));
                }
                filterWindow.dismiss();
            }
        });

        Button filterButton = popupView.findViewById(R.id.apply_button);
        filterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // setting filters as parameters
                amdSelected = amd_option.isChecked();
                intelSelected= intel_option.isChecked();
                priceMin = priceBar.getSelectedMinValue();
                priceMax = priceBar.getSelectedMaxValue();
                coreMin = coresBar.getSelectedMinValue();
                coreMax = coresBar.getSelectedMaxValue();
                baseClockMin = baseClockBar.getSelectedMinValue();
                baseClockMax = baseClockBar.getSelectedMaxValue();
                boostClockMin = boostClockBar.getSelectedMinValue();
                boostClockMax = boostClockBar.getSelectedMaxValue();
                tdpMin = tdpBar.getSelectedMinValue();
                tdpMax = tdpBar.getSelectedMaxValue();

                filterData();
                filterWindow.dismiss();
            }
        });

        // close button
        ImageButton closeBtn = popupView.findViewById(R.id.close_button);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // reverting selections
                amd_option.setChecked(amd_option.isChecked());
                intel_option.setChecked(intel_option.isChecked());
                priceBar.setSelectedMinValue(priceBar.getSelectedMinValue());
                priceBar.setSelectedMaxValue(priceBar.getSelectedMaxValue());
                coresBar.setSelectedMinValue(coresBar.getSelectedMinValue());
                coresBar.setSelectedMaxValue(coresBar.getSelectedMaxValue());
                baseClockBar.setSelectedMinValue(baseClockBar.getSelectedMinValue());
                baseClockBar.setSelectedMaxValue(baseClockBar.getSelectedMaxValue());
                boostClockBar.setSelectedMinValue(boostClockBar.getSelectedMinValue());
                boostClockBar.setSelectedMaxValue(boostClockBar.getSelectedMaxValue());
                tdpBar.setSelectedMinValue(tdpBar.getSelectedMinValue());
                tdpBar.setSelectedMaxValue(tdpBar.getSelectedMaxValue());
                filterWindow.dismiss();
                return true;
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                amd_option.setChecked(amd_option.isChecked());
                intel_option.setChecked(intel_option.isChecked());
                priceBar.setSelectedMinValue(priceBar.getSelectedMinValue());
                priceBar.setSelectedMaxValue(priceBar.getSelectedMaxValue());
                coresBar.setSelectedMinValue(coresBar.getSelectedMinValue());
                coresBar.setSelectedMaxValue(coresBar.getSelectedMaxValue());
                baseClockBar.setSelectedMinValue(baseClockBar.getSelectedMinValue());
                baseClockBar.setSelectedMaxValue(baseClockBar.getSelectedMaxValue());
                boostClockBar.setSelectedMinValue(boostClockBar.getSelectedMinValue());
                boostClockBar.setSelectedMaxValue(boostClockBar.getSelectedMaxValue());
                tdpBar.setSelectedMinValue(tdpBar.getSelectedMinValue());
                tdpBar.setSelectedMaxValue(tdpBar.getSelectedMaxValue());
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

        //Reset button
        Button resetButton = popupView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFilter = "Popularity (Descending)";
                filteredData = cpuFeed.getSearchData();
                dialog.removeAllViews();
                for (int i=0; i<30; i++){
                    cpuFeed.addProduct(filteredData.get(i));
                }
                sortWindow.dismiss();
            }
        });

        // Apply Button
        Button applyButton = popupView.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int selectedId = sortOptions.getCheckedRadioButtonId();
                RadioButton radioButton = sortOptions.findViewById(selectedId);
                String selectedText = radioButton.getText().toString();
                sortFilter = selectedText;
                filterData();
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

    public void filterData(){
        ArrayList<String> manufacturers = new ArrayList<>();
        ArrayList<CpuSearchProduct> filtered = new ArrayList<>();

        if (amdSelected){
            manufacturers.add("AMD");
        }
        if (intelSelected){
            manufacturers.add("Intel");
        }

        dialog.removeAllViews();
        for (CpuSearchProduct product: cpuFeed.getSearchData()){
            if (manufacturers.contains(product.getManufacturer()) &&
                    priceMin < product.getBestPrice() &&
                    priceMax > product.getBestPrice()  &&
                    coreMin<= stringToValue(product.getCores()) &&
                    coreMax >= stringToValue(product.getCores()) &&
                    baseClockMin <= stringToValue(product.getBaseClock()) &&
                    baseClockMax >= stringToValue(product.getBaseClock()) &&
                    boostClockMin <= stringToValue(product.getBoostClock())  &&
                    boostClockMax >= stringToValue(product.getBoostClock()) &&
                    tdpMin <= stringToValue(product.getTdp()) &&
                    tdpMax >= stringToValue(product.getTdp())
            ){
                filtered.add(product);
            }
        }
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        // Sorted
        CpuProductSort sorter = new CpuProductSort();
        if (sortFilter.toLowerCase().contains("popularity")) {
            sorted = sorter.sortPopularity(filtered);
        }else if (sortFilter.toLowerCase().contains("name")){
            sorted = sorter.sortName(filtered);
        }else if (sortFilter.toLowerCase().contains("price")){
            sorted = sorter.sortPrice(filtered);
        }else if (sortFilter.toLowerCase().contains("rating")){
            sorted = sorter.sortRating(filtered);
        }else if (sortFilter.toLowerCase().contains("cores")){
            sorted = sorter.sortCores(filtered);
        }else if (sortFilter.toLowerCase().contains("base")){
            sorted = sorter.sortBaseClock(filtered);
        }else if (sortFilter.toLowerCase().contains("boost")){
            sorted = sorter.sortBoostClock(filtered);
        }else if (sortFilter.toLowerCase().contains("tdp")){
            sorted = sorter.sortTDP(filtered);
        }else {
            sorted = sorter.sortPopularity(filtered);
        }

        // get children views
        ArrayList<View> children = cpuFeed.getProductLayoutView();

        loadingNotDone();
        dialog.removeAllViews();
        dialog.setVisibility(View.GONE);
        if (sortFilter.toLowerCase().contains("descending")){
            Collections.reverse(sorted);
            for (CpuSearchProduct product: sorted){
                int id = product.getViewID();
                for (View child: children){
                    if (child.getId() == id){
                        dialog.addView(child);
                    }
                }
            }
        }else {
            for (CpuSearchProduct product: sorted){
                int id = product.getViewID();
                for (View child: children){
                    if (child.getId() == id){
                        dialog.addView(child);
                    }
                }
            }
        }
        loadingDone();
        filteredData = sorted;
        dialog.setVisibility(View.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(context, R.anim.decompress);
        animation.setDuration(1000);
        dialog.setAnimation(animation);
        dialog.animate();
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (CpuSearchProduct prod: cpuFeed.getSearchData()){
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


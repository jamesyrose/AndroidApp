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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

import async_tasks.feeds.GpuFeedTask;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.GpuSearchProduct;
import preferences.Preferences;

public class gpuSearchActivity extends AppCompatActivity {
    String BUILD_ID = "";

    static GpuFeedTask gpuFeed;
    Preferences prefs;
    ScrollView dialogScroll;
    LinearLayout dialog;
    PopupWindow filterWindow;
    PopupWindow sortWindow;
    Context context;
    View loadingWheel;

    // Data filters
    int priceMin = 0;
    int priceMax = 1000000;
    int clockMin = 0;
    int clockMax = 3000;
    int memoryMin = 0;
    int memoryMax = 25;
    int tdpMin = 0;
    int tdpMax=10000;
    String sortFilter = "Popularity (Ascending)";
    ArrayList<CheckBox> brandList = new ArrayList<>();
    ArrayList<String> brandSelected = new ArrayList<>();


    // data
    ArrayList<GpuSearchProduct> filteredData;

    // Constants
    SqlConstants sqlConst = new SqlConstants();

    Bundle savedInstanceState;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.scroll_search);
        context = gpuSearchActivity.this;
        dialog = (LinearLayout) findViewById(R.id.searchID);
        loadingWheel = findViewById(R.id.loading_wheel);
        prefs = new Preferences(context);
        BUILD_ID = getIntent().getStringExtra("buildID");
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingNotDone();
        gpuFeed = new GpuFeedTask(context, dialog, prefs, BUILD_ID);
        gpuFeed.execute( sqlConst.GPU_SEARCH_LIST);

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

        // Set Scroll listener
        dialogScroll = findViewById(R.id.scroll_window);
        dialogScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            dialogScroll.getY();
            int scrollY = dialogScroll.getScrollY() + dialogScroll.getHeight(); // For ScrollView
            View lastView = dialog.getChildAt(dialog.getChildCount() - 1);
            if (lastView != null) {
                float lastViewY = lastView.getY();
                System.out.println(dialog.getChildCount());
                if (scrollY > lastViewY) {
                    Toast.makeText(context, "Loading More", Toast.LENGTH_SHORT).show();
                    try{
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onLoadMore();
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
        ArrayList<GpuSearchProduct> data = gpuFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            gpuFeed.addProduct(data.get(i));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.gpu_filter_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        if (filterWindow == null){
            filterWindow = new PopupWindow(popupView, width, height, focusable);
        }
        filterWindow.setAnimationStyle(R.style.popup_animation);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        filterWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        LinearLayout mainLayout  = popupView.findViewById(R.id.main_vert_layout);

        // Set the Branch Choices
        if (brandList.isEmpty()){
            RelativeLayout brand_selection = popupView.findViewById(R.id.brand_selection);
            final LinearLayout brandOptions = popupView.findViewById(R.id.brand_options);
            final LinearLayout brand_choice1 = popupView.findViewById(R.id.brand_options_1);
            final LinearLayout brand_choice2 = popupView.findViewById(R.id.brand_options_2);
            final CheckBox deselect = popupView.findViewById(R.id.deselect_all_brands); //Deselect all
            deselect.setChecked(true);
            deselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    brandList.stream().forEach(ch -> ch.setChecked(true));
                }else{
                    brandList.stream().forEach(ch -> ch.setChecked(false));

                }

            });

            brandOptions.setVisibility(View.GONE);
            ArrayList<String> brands = new ArrayList<>();
            for (GpuSearchProduct prod: gpuFeed.getSearchData()){
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$" + brandList.isEmpty());

                String brand = prod.getManufacturer();
                if (!brands.contains(brand) && brand != null){
                    brands.add(brand);
                    brandSelected.add(brand); // initialize as true
                }
            }
            int brandCount = brands.size();
            int midPoint = Math.floorDiv(brandCount, 2);
            for (int i=0; i<midPoint; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        brand_choice1,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(brands.get(i));
                brand_choice1.addView(checkBoxLayout);
                brandList.add(box);
            }
            for (int i=midPoint; i<brandCount; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        brand_choice2,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(brands.get(i));
                brand_choice2.addView(checkBoxLayout);
                brandList.add(box);
            }
            brand_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (brandOptions.isShown()){
                        brandOptions.setVisibility(View.GONE);
                    }else{
                        brandOptions.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

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



        // Set Clock Speed Choice
        final RelativeLayout clockChoice = popupView.findViewById(R.id.clock_selection);
        final LinearLayout clockOptionSection= popupView.findViewById(R.id.clock_option_section);
        final RangeSeekBar<Integer> baseClockBar = popupView.findViewById(R.id.clock_seek_bar);
        baseClockBar.setRangeValues(0, 3000);
        clockOptionSection.setVisibility(View.GONE);
        clockChoice.setOnClickListener(v -> {
            if (clockOptionSection.isShown()){
                clockOptionSection.setVisibility(View.GONE);
            }else{
                clockOptionSection.setVisibility(View.VISIBLE);
            }
        });

        // Set memory Speed Choice
        final RelativeLayout memoryChoice = popupView.findViewById(R.id.memory_selection);
        final RangeSeekBar<Integer> memoryBar = popupView.findViewById(R.id.memory_seek_bar);
        memoryBar.setRangeValues(0, 25);
        memoryBar.setVisibility(View.GONE);
        memoryChoice.setOnClickListener(v -> {
            if (memoryBar.isShown()){
                memoryBar.setVisibility(View.GONE);
            }else{
                memoryBar.setVisibility(View.VISIBLE);
            }
        });



        // Set tdp Choices
        final RangeSeekBar<Integer> tdpBar = popupView.findViewById(R.id.tdp_seek_bar);
        tdpBar.setRangeValues(0, 1000);
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

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
               // Set both brands selected
                // price options
                priceBar.setSelectedMinValue(0);
                priceBar.setSelectedMaxValue(maxPrice);

                // Clock speeds
                baseClockBar.setSelectedMinValue(0);
                baseClockBar.setSelectedMaxValue(3000);
                // tdp
                tdpBar.setSelectedMinValue(0);
                tdpBar.setSelectedMaxValue(500);
                brandList.stream().forEach(cb -> cb.setChecked(true));

                gpuFeed = new GpuFeedTask(context, dialog, prefs, BUILD_ID);
                dialog.removeAllViews();
                gpuFeed.execute(sqlConst.GPU_SEARCH_LIST);
                filterWindow.dismiss();
            }
        });

        Button filterButton = popupView.findViewById(R.id.apply_button);
        filterButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // setting filters as parameters
                priceMin = priceBar.getSelectedMinValue();
                priceMax = priceBar.getSelectedMaxValue();
                tdpMin = tdpBar.getSelectedMinValue();
                tdpMax = tdpBar.getSelectedMaxValue();

                brandSelected.clear();

                brandList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        brandSelected.add((String) cb.getText());
                    };
                });

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
                priceBar.setSelectedMinValue(priceBar.getSelectedMinValue());
                priceBar.setSelectedMaxValue(priceBar.getSelectedMaxValue());
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
                priceBar.setSelectedMinValue(priceBar.getSelectedMinValue());
                priceBar.setSelectedMaxValue(priceBar.getSelectedMaxValue());
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
        View popupView = inflater.inflate(R.layout.gpu_sort_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        if (sortWindow == null){
            sortWindow = new PopupWindow(popupView, width, height, focusable);
        }
        sortWindow.setAnimationStyle(R.style.popup_animation);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        sortWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        LinearLayout mainLayout  = popupView.findViewById(R.id.main_vert_layout);
        final RadioGroup sortOptions = popupView.findViewById(R.id.sort_group);

        //Reset button
        Button resetButton = popupView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            sortFilter = "Popularity (Descending)";
            filteredData = gpuFeed.getSearchData();


            dialog.removeAllViews();
            gpuFeed = new GpuFeedTask(context, dialog, prefs, BUILD_ID);
            gpuFeed.execute(sqlConst.GPU_SEARCH_LIST);
            sortWindow.dismiss();
        });

        // Apply Button
        Button applyButton = popupView.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int selectedId = sortOptions.getCheckedRadioButtonId();
                RadioButton radioButton = sortOptions.findViewById(selectedId);
                String selectedText = "Popularity (Ascending)";
                if (radioButton != null){
                    selectedText = radioButton.getText().toString();
                }
                sortFilter = selectedText;
                sortWindow.dismiss();
                loadingNotDone();
                filterData();
                loadingDone();
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
        if (brandSelected.isEmpty()){
            for (GpuSearchProduct prod: gpuFeed.getSearchData()){
                String prodBrand = prod.getManufacturer();
                if (!brandSelected.contains(prodBrand)){
                    brandSelected.add(prodBrand);
                }
            }
        }

        String brands = "";
        for (String brand: brandSelected){
            brands += String.format("'%s',", brand);
        }
        brands = brands.replaceAll(",$", "");



        String sortBy = "";
        String desc = "";
        if (sortFilter.toLowerCase().contains("descending")){
            desc = "DESC";
        }

        if (sortFilter.toLowerCase().contains("popularity")) {
            sortBy = String.format("Rating.Count %s, Rating.Average %s", desc, desc);
        }else if (sortFilter.toLowerCase().contains("name")){
            sortBy = "ProductMain.ProductName " + desc;
        }else if (sortFilter.toLowerCase().contains("price")){
            sortBy = "ProductMain.BestPrice " + desc;
        }else if (sortFilter.toLowerCase().contains("rating")){
            sortBy = "Rating.Average " + desc;
        }else if (sortFilter.toLowerCase().contains("clock")){
            sortBy = "CAST(GPU.`Core Clock` AS INT) " + desc;
        }else if (sortFilter.toLowerCase().contains("memory")){
            sortBy = "CAST(GPU.`Memory` AS INT) " + desc;
        }else if (sortFilter.toLowerCase().contains("length")){
            sortBy = "CAST(GPU.`Length` AS INT) " + desc;
        }else if (sortFilter.toLowerCase().contains("tdp")){
            sortBy = "CAST(GPU.`TDP` AS INT) " + desc;
        }

        String sqlStringBuilt = String.format(sqlConst.GPU_SEARCH_FILTER, brands,
                priceMin, priceMax, memoryMin, memoryMax, tdpMin, tdpMax, sortBy);
        System.out.println(sqlStringBuilt);
        dialogScroll.smoothScrollTo(0,0);
        dialog.removeAllViews();
        loadingNotDone();
        gpuFeed = new GpuFeedTask(context, dialog, prefs, BUILD_ID);
        gpuFeed.execute(sqlStringBuilt);
        loadingDone();
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (GpuSearchProduct prod: gpuFeed.getSearchData()){
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
       loadingWheel.setVisibility(View.GONE);
    }

    private void loadingNotDone(){
        loadingWheel.setVisibility(View.VISIBLE);
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


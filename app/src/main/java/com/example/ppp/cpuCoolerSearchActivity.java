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

import async_tasks.feeds.CpuCoolerFeedTask;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.CpuCoolerProduct;
import preferences.Preferences;

public class cpuCoolerSearchActivity extends AppCompatActivity {
    String BUILD_ID = "";
    String SQL_FILTER = "WHERE ";

    static CpuCoolerFeedTask cpuCoolerFeed;
    Preferences prefs;
    LinearLayout dialog;
    ScrollView dialogScroll;
    PopupWindow filterWindow;
    PopupWindow sortWindow;
    Context context;
    SqlConstants sqlConst;

    // Data filters
    int priceMin = 0;
    int priceMax = 1000000;

    String sortFilter = "Popularity (Ascending)";
    boolean waterCooled = true;
    boolean airCooled = true;
    ArrayList<CheckBox> brandList = new ArrayList<>();
    ArrayList<String> brandSelected = new ArrayList<String>();

    // data
    ArrayList<CpuCoolerProduct> filteredData;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_search);
        context = cpuCoolerSearchActivity.this;
        prefs = new Preferences(context);
        sqlConst = new SqlConstants();
        loadingNotDone();
        dialog = findViewById(R.id.searchID);
        dialogScroll = findViewById(R.id.scroll_window);
        BUILD_ID = getIntent().getStringExtra("buildID");
        String buff = getIntent().getStringExtra("sqlFilter");
        if (buff != null){
            SQL_FILTER = buff;
        }

        // Getting Data
        cpuCoolerFeed = new CpuCoolerFeedTask(context, dialog, prefs, BUILD_ID);
        cpuCoolerFeed.execute(sqlConst.CPU_COOLER_SEARCH_LIST.replace("WHERE", SQL_FILTER));

        // Set filter
        Button filter = findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
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
                if (lastView != null){
                    float lastViewY = lastView.getY();
                    System.out.println(dialog.getChildCount());
                    if (scrollY > lastViewY){
                        loadingNotDone();
                        onLoadMore();
                        loadingDone();
                    }
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
        ArrayList<CpuCoolerProduct> data = cpuCoolerFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            cpuCoolerFeed.addProduct(data.get(i));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.cpu_cooler_filter_window, null);

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
            for (CpuCoolerProduct prod: cpuCoolerFeed.getSearchData()){
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

        // Cooling type
        RelativeLayout typeSelection = popupView.findViewById(R.id.type_selection);
        LinearLayout typeOptions = popupView.findViewById(R.id.type_options);
        CheckBox air = popupView.findViewById(R.id.air_option);
        CheckBox water = popupView.findViewById(R.id.water_option);
        air.setChecked(true);
        water.setChecked(true);
        typeOptions.setVisibility(View.GONE);

        typeSelection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (typeOptions.isShown()){
                    typeOptions.setVisibility(View.GONE);
                }else{
                    typeOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        // Reset Button
        Button resetButton = popupView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // price options
                priceBar.setSelectedMinValue(0);
                priceBar.setSelectedMaxValue(maxPrice);
                brandSelected.clear();
                brandList.stream().forEach(ch -> ch.setChecked(true));
                brandList.stream().forEach(ch -> brandSelected.add((String) ch.getText()));
                air.setChecked(true);
                water.setChecked(true);
                airCooled = true;
                waterCooled = true;

                filteredData = cpuCoolerFeed.getSearchData();
                dialogScroll.smoothScrollTo(0,0);
                dialog.removeAllViews();
                cpuCoolerFeed = new CpuCoolerFeedTask(context, dialog, prefs, BUILD_ID);
                cpuCoolerFeed.execute(sqlConst.CPU_COOLER_SEARCH_LIST.replace("WHERE", SQL_FILTER));
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
                brandSelected.clear();
                brandList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        brandSelected.add((String) cb.getText());
                    };
                });
                waterCooled = water.isChecked();
                airCooled = air.isChecked();

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
        View popupView = inflater.inflate(R.layout.cpu_cooler_sort_window, null);

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
        resetButton.setOnClickListener(v -> {
            sortFilter = "Popularity (Descending)";
            filteredData = cpuCoolerFeed.getSearchData();
            dialog.removeAllViews();
            dialogScroll.smoothScrollTo(0, 0);
            cpuCoolerFeed = new CpuCoolerFeedTask(context, dialog, prefs, BUILD_ID);
            cpuCoolerFeed.execute(sqlConst.CPU_COOLER_SEARCH_LIST.replace("WHERE", SQL_FILTER));
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
        if (brandSelected.isEmpty()){
            for (CpuCoolerProduct prod: cpuCoolerFeed.getSearchData()){
                String prodBrand = prod.getManufacturer();
                if (!brandSelected.contains(prodBrand)){
                    brandSelected.add(prodBrand);
                }
            }
        };

        String wc = ""; // Water cooled
        String ac = ""; // Air cooled
        String brands = "";
        if (waterCooled){
            wc = "%Yes%";
        }
        if (airCooled){
            ac = "%No%";
        }

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
        }else if (sortFilter.toLowerCase().contains("cores")){
            sortBy = "CAST(CPU.`Core Count` AS INT) " + desc;
        }else if (sortFilter.toLowerCase().contains("base")){
            sortBy = "CAST(CPU.`Core Clock` AS FLOAT) " + desc;
        }else if (sortFilter.toLowerCase().contains("boost")){
            sortBy = "CAST(CPU.`Boost Clock` AS FLOAT" + desc;
        }else if (sortFilter.toLowerCase().contains("tdp")){
            sortBy = "CAST(CPU.`TDP` AS INT) " + desc;
        }

        String sqlStringBuilt = String.format(sqlConst.CPU_COOLER_SEARCH_LIST_FILTERED,
                brands, priceMin, priceMax, wc, ac, sortBy);
        System.out.println(sqlStringBuilt);
        dialog.removeAllViews();
        dialogScroll.smoothScrollTo(0, 0);
        cpuCoolerFeed = new CpuCoolerFeedTask(context, dialog, prefs, BUILD_ID);
        cpuCoolerFeed.execute(sqlStringBuilt.replace("WHERE", SQL_FILTER));
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (CpuCoolerProduct prod: cpuCoolerFeed.getSearchData()){
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


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

import async_tasks.feeds.StorageFeedTask;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.StorageProduct;
import preferences.Preferences;

public class storageSearchActivity extends AppCompatActivity {
    static StorageFeedTask storageFeed;
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
    int capcaityMin = 0;
    int capacityMax = 25000;
    String nvme = "Yes";
    String nonNvme = "No";

    ArrayList<CheckBox> brandList = new ArrayList<>();
    ArrayList<CheckBox> formFactorList = new ArrayList<>();
    ArrayList<CheckBox> typeList = new ArrayList<>();

    ArrayList<String> brandSelected = new ArrayList<String>();
    ArrayList<String> formFactorSelected = new ArrayList<String>();
    ArrayList<String> typeSelected = new ArrayList<String>();

    String sortFilter = "Popularity (Ascending)";

    // data
    ArrayList<StorageProduct> filteredData;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_search);
        context = storageSearchActivity.this;
        loadingNotDone();
        dialog = (LinearLayout) findViewById(R.id.searchID);
        dialogScroll = findViewById(R.id.scroll_window);
        sqlConst = new SqlConstants();

        prefs = new Preferences(context);

        storageFeed = new StorageFeedTask(context, dialog, prefs);
        storageFeed.execute(sqlConst.STORAGE_SEARCH_LIST);

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
        ArrayList<StorageProduct> data = storageFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            storageFeed.addProduct(data.get(i));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.storage_filter_window, null);

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
            for (StorageProduct prod: storageFeed.getSearchData()){
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

        if (formFactorList.isEmpty()){
            // Socket Types
            RelativeLayout form_factor_selection = popupView.findViewById(R.id.form_factor_selection);
            final LinearLayout form_factorOptions = popupView.findViewById(R.id.form_factor_options);
            ArrayList<String> form_factors = new ArrayList<>();
            for (StorageProduct prod: storageFeed.getSearchData()){
                String form_factor = prod.getFormFactor();
                if (!form_factors.contains(form_factor) && form_factor != null){
                    form_factors.add(form_factor);
                    formFactorSelected.add(form_factor); // initialize as true

                }
            }
            for(String form_factor: form_factors){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        form_factorOptions,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(form_factor);
                formFactorList.add(box);
                form_factorOptions.addView(checkBoxLayout);

            }
            form_factorOptions.setVisibility(View.GONE);
            form_factor_selection.setOnClickListener(v -> {
                if (form_factorOptions.isShown()){
                    form_factorOptions.setVisibility(View.GONE);
                }else{
                    form_factorOptions.setVisibility(View.VISIBLE);
                }
            });

        }

        // Select storage types 2.5in 5400rpm... etc
        if (typeList.isEmpty()){
            // Socket Types
            RelativeLayout type_selection = popupView.findViewById(R.id.type_selection);
            final LinearLayout typeOptions = popupView.findViewById(R.id.type_options);
            ArrayList<String> types = new ArrayList<>();
            for (StorageProduct prod: storageFeed.getSearchData()){
                String type = prod.getType();
                if (!types.contains(type) && type != null){
                    types.add(type);
                    typeSelected.add(type); // initialize as true

                }
            }
            for(String type: types){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        typeOptions,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(type);
                typeList.add(box);
                typeOptions.addView(checkBoxLayout);

            }
            typeOptions.setVisibility(View.GONE);
            type_selection.setOnClickListener(v -> {
                if (typeOptions.isShown()){
                    typeOptions.setVisibility(View.GONE);
                }else{
                    typeOptions.setVisibility(View.VISIBLE);
                }
            });

        }

        // nvme
        CheckBox nvmeBox = popupView.findViewById(R.id.nvme_option);
        CheckBox nonNvmeBox = popupView.findViewById(R.id.non_nvme_option);
        RelativeLayout nvmeSelection = popupView.findViewById(R.id.nvme_selection);
        LinearLayout nvmeOptions = popupView.findViewById(R.id.nvme_options);
        nonNvmeBox.setChecked(true);
        nvmeBox.setChecked(true);

        nvmeOptions.setVisibility(View.GONE);
        nvmeSelection.setOnClickListener(v -> {
            if ((nvmeOptions.isShown())) {
                nvmeOptions.setVisibility(View.GONE);
            } else {
                nvmeOptions.setVisibility(View.VISIBLE);
            }
        });



        // Capacity
        RelativeLayout capacitySelection = popupView.findViewById(R.id.capacity_selection);
        RangeSeekBar<Integer> capacityBar = popupView.findViewById(R.id.capacity_bar);
        capacityBar.setRangeValues(0, 25000);
        capacityBar.setVisibility(View.GONE);
        capacitySelection.setOnClickListener(v -> {
            if ((capacityBar.isShown())) {
                capacityBar.setVisibility(View.GONE);
            } else {
                capacityBar.setVisibility(View.VISIBLE);
            }
        });


        // Reset Button
        Button resetButton = popupView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // price options
                priceBar.setSelectedMinValue(0);
                priceBar.setSelectedMaxValue(maxPrice);
                brandList.stream().forEach(cb -> cb.setChecked(true));
                typeList.stream().forEach(cb -> cb.setChecked(true));
                typeList.stream().forEach(cb -> cb.setChecked(true));

                filteredData = storageFeed.getSearchData();
                loadingNotDone();
                dialog.removeAllViews();
                storageFeed = new StorageFeedTask(context, dialog, prefs);
                storageFeed.execute(sqlConst.STORAGE_SEARCH_LIST);
                loadingDone();
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
                formFactorSelected.clear();
                formFactorSelected.clear();
                capcaityMin = capacityBar.getSelectedMinValue();
                capacityMax = capacityBar.getSelectedMaxValue();
                nvme = (nvmeBox.isChecked()) ? "Yes" : "";
                nonNvme =  (nonNvmeBox.isChecked()) ? "No": "";

                brandList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        brandSelected.add((String) cb.getText());
                    };
                });
                formFactorList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        formFactorSelected.add((String) cb.getText());
                    };
                });
                formFactorList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        formFactorSelected.add((String) cb.getText());
                    };
                });

                loadingNotDone();
                filterData();
                loadingDone();
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
        View popupView = inflater.inflate(R.layout.storage_sort_window, null);

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
                filteredData = storageFeed.getSearchData();
                dialog.removeAllViews();
                loadingNotDone();
                storageFeed = new StorageFeedTask(context, dialog, prefs);
                storageFeed.execute(sqlConst.STORAGE_SEARCH_LIST);
                sortWindow.dismiss();
                loadingDone();
            }
        });

        // Apply Button
        Button applyButton = popupView.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int selectedId = sortOptions.getCheckedRadioButtonId();
                RadioButton radioButton = sortOptions.findViewById(selectedId);
                String selectedText = "Popularity (Ascending)";
                if (radioButton != null){
                    selectedText = radioButton.getText().toString();
                }
                sortFilter = selectedText;
                loadingNotDone();
                filterData();
                sortWindow.dismiss();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterData(){
        if (brandSelected.isEmpty()){
            for (StorageProduct prod: storageFeed.getSearchData()){
                String prodBrand = prod.getManufacturer();
                if (!brandSelected.contains(prodBrand)){
                    brandSelected.add(prodBrand);
                }
            }
        }
        if (formFactorSelected.isEmpty()){
            for (StorageProduct prod: storageFeed.getSearchData()){
                String formFactor = prod.getFormFactor();
                if (!formFactorSelected.contains(formFactor)){
                    formFactorSelected.add(formFactor);
                }
            }
        }
        if (typeSelected.isEmpty()){
            for (StorageProduct prod: storageFeed.getSearchData()){
                String type = prod.getType();
                if (!typeSelected.contains(type)){
                    typeSelected.add(type);
                }
            }
        }


        String brands = "";
        for (String brand: brandSelected){
            brands += String.format("'%s',", brand);
        }
        brands = brands.replaceAll(",$", "");

        String formFactors = "";
        for (String formFactor: formFactorSelected){
            formFactors += String.format("'%s',", formFactor);
        }
        formFactors = formFactors.replaceAll(",$", "");


        String type = "";
        for (String prod: typeSelected){
            type += String.format("'%s',", prod);
        }
        type = type.replaceAll(",$", "");

        String sortBy = "ProductMain.ProductName";
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
        }else if (sortFilter.toLowerCase().contains("rating")) {
            sortBy = "Rating.Average " + desc;
        }else if (sortFilter.toLowerCase().contains("capacity")){
            sortBy = "CAST(Storage.Capacity AS INT) " + desc;
        }

        String sqlStringBuilt = String.format(sqlConst.STORAGE_SEARCH_FILTER, brands, formFactors,
                type, nvme, nonNvme, priceMin, priceMax, capcaityMin, capacityMax, sortBy);

        System.out.println(sqlStringBuilt);
        loadingNotDone();
        dialog.removeAllViews();
        storageFeed = new StorageFeedTask(context, dialog, prefs);
        storageFeed.execute(sqlStringBuilt);
        loadingDone();
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (StorageProduct prod: storageFeed.getSearchData()){
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

    private Double stringToInteger(String val){
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


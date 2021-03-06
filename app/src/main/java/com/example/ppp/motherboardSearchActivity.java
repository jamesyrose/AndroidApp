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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

import async_tasks.feeds.MotherboardFeedTask;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.MotherboardProduct;
import preferences.Preferences;

public class motherboardSearchActivity extends AppCompatActivity {
    String BUILD_ID = "";
    String SQL_FILTER = "WHERE";

    static MotherboardFeedTask motherboardFeed;
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
    int memoryMin = 0;
    int memoryMax = 2000;
    int memorySlotMin = 0;
    int memorySlotMax = 16;
    ArrayList<CheckBox> brandList = new ArrayList<>();
    ArrayList<CheckBox> socketList = new ArrayList<>();
    ArrayList<CheckBox> formFactorList = new ArrayList<>();
    ArrayList<String> brandSelected = new ArrayList<String>();
    ArrayList<String> socketSelected = new ArrayList<String>();
    ArrayList<String> formFactorSelected = new ArrayList<String>();

    String sortFilter = "Popularity (Ascending)";

    // data
    ArrayList<MotherboardProduct> filteredData;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_search);
        context = motherboardSearchActivity.this;
        loadingNotDone();
        dialog = (LinearLayout) findViewById(R.id.searchID);
        dialogScroll = findViewById(R.id.scroll_window);
        sqlConst = new SqlConstants();
        prefs = new Preferences(context);
        BUILD_ID = getIntent().getStringExtra("buildID");
        String buff = getIntent().getStringExtra("sqlFilter");
        if (buff!= null){
            SQL_FILTER = buff;
        }


        motherboardFeed = new MotherboardFeedTask(context, dialog, prefs, BUILD_ID);
        motherboardFeed.execute(sqlConst.MOTHERBOARD_SEARCH_LIST.replace("WHERE", SQL_FILTER));

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
                        Toast.makeText(context, "Loading More", Toast.LENGTH_SHORT).show();
                        try{
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        onLoadMore();
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
        ArrayList<MotherboardProduct> data = motherboardFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            motherboardFeed.addProduct(data.get(i));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.motherboard_filter_window, null);

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
            brandOptions.setVisibility(View.GONE);
            ArrayList<String> brands = new ArrayList<>();
            for (MotherboardProduct prod: motherboardFeed.getSearchData()){
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


        // Set memory range
        final RangeSeekBar<Integer> memoryBar = popupView.findViewById(R.id.memory_seek_bar);
        memoryBar.setRangeValues(0, 2000);
        final RelativeLayout memoryChoice = popupView.findViewById(R.id.max_memory_selection);
        memoryBar.setVisibility(View.GONE);
        memoryChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memoryBar.isShown()){
                    memoryBar.setVisibility(View.GONE);
                }else{
                    memoryBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set memory slot range
        final RangeSeekBar<Integer> memorySlotBar = popupView.findViewById(R.id.memory_slot_seek_bar);
        memorySlotBar.setRangeValues(0, 16);
        final RelativeLayout memorySlotChoice = popupView.findViewById(R.id.memory_slot_selection);
        memorySlotBar.setVisibility(View.GONE);
        memorySlotChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memorySlotBar.isShown()){
                    memorySlotBar.setVisibility(View.GONE);
                }else{
                    memorySlotBar.setVisibility(View.VISIBLE);
                }
            }
        });


        if (socketList.isEmpty()){
            // Socket Types
            RelativeLayout socket_selection = popupView.findViewById(R.id.socket_selection);
            final LinearLayout socketOptions = popupView.findViewById(R.id.socket_options);
            socketOptions.setVisibility(View.GONE);
            ArrayList<String> sockets = new ArrayList<>();
            for (MotherboardProduct prod: motherboardFeed.getSearchData()){
                String socket = prod.getSocketType();
                if (!sockets.contains(socket) && socket != null){
                    sockets.add(socket);
                    socketSelected.add(socket); // initialize as true

                }
            }
            for(String socket: sockets){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        socketOptions,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(socket);
                socketList.add(box);
                socketOptions.addView(checkBoxLayout);

            }

            socket_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (socketOptions.isShown()){
                        socketOptions.setVisibility(View.GONE);
                    }else{
                        socketOptions.setVisibility(View.VISIBLE);
                    }
                }
            });

        }


        if (formFactorList.isEmpty()){
            // Form factor Types
            RelativeLayout form_factor_selection = popupView.findViewById(R.id.form_factor_selection);
            final LinearLayout form_factorOptions = popupView.findViewById(R.id.form_factor_options);
            form_factorOptions.setVisibility(View.GONE);
            ArrayList<String> form_factors = new ArrayList<>();
            for (MotherboardProduct prod: motherboardFeed.getSearchData()){
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
                System.out.println(box.getText());
                formFactorList.add(box);
                form_factorOptions.addView(checkBoxLayout);
            }

            form_factor_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (form_factorOptions.isShown()){
                        form_factorOptions.setVisibility(View.GONE);
                    }else{
                        form_factorOptions.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        // Reset Button
        Button resetButton = popupView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // price options
                priceBar.setSelectedMinValue(0);
                priceBar.setSelectedMaxValue(maxPrice);
                memoryBar.setSelectedMinValue(0);
                memoryBar.setSelectedMaxValue(2000);
                memorySlotBar.setSelectedMinValue(0);
                memorySlotBar.setSelectedMaxValue(16);
                brandList.stream().forEach(cb -> cb.setChecked(true));
                socketList.stream().forEach(cb -> cb.setChecked(true));
                formFactorList.stream().forEach(cb -> cb.setChecked(true));

                filteredData = motherboardFeed.getSearchData();
                loadingNotDone();
                dialog.removeAllViews();
                motherboardFeed = new MotherboardFeedTask(context, dialog, prefs, BUILD_ID);
                motherboardFeed.execute(sqlConst.MOTHERBOARD_SEARCH_LIST.replace("WHERE", SQL_FILTER));
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
                memoryMin = memoryBar.getSelectedMinValue();
                memoryMax = memoryBar.getSelectedMaxValue();
                memorySlotMin = memorySlotBar.getSelectedMinValue();
                memorySlotMax = memorySlotBar.getSelectedMaxValue();
                brandSelected.clear();
                socketSelected.clear();
                formFactorSelected.clear();

                brandList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        brandSelected.add((String) cb.getText());
                    };
                });
                socketList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        socketSelected.add((String) cb.getText());
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
                memoryBar.setSelectedMinValue(priceBar.getSelectedMinValue());
                memoryBar.setSelectedMaxValue(priceBar.getSelectedMaxValue());
                memorySlotBar.setSelectedMinValue(memorySlotBar.getSelectedMinValue());
                memorySlotBar.setSelectedMaxValue(memorySlotBar.getSelectedMaxValue());
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
                memoryBar.setSelectedMinValue(priceBar.getSelectedMinValue());
                memoryBar.setSelectedMaxValue(priceBar.getSelectedMaxValue());
                memorySlotBar.setSelectedMinValue(memorySlotBar.getSelectedMinValue());
                memorySlotBar.setSelectedMaxValue(memorySlotBar.getSelectedMaxValue());
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
        View popupView = inflater.inflate(R.layout.motherboard_sort_window, null);

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
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFilter = "Popularity (Descending)";
                filteredData = motherboardFeed.getSearchData();
                dialog.removeAllViews();
                loadingNotDone();
                motherboardFeed = new MotherboardFeedTask(context, dialog, prefs, BUILD_ID);
                motherboardFeed.execute(sqlConst.MOTHERBOARD_SEARCH_LIST.replace("WHERE", SQL_FILTER));
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
            for (MotherboardProduct prod: motherboardFeed.getSearchData()){
                String prodBrand = prod.getManufacturer();
                if (!brandSelected.contains(prodBrand)){
                    brandSelected.add(prodBrand);
                }
            }
        }
        if (socketSelected.isEmpty()){
            for (MotherboardProduct prod: motherboardFeed.getSearchData()){
                String prodBrand = prod.getSocketType();
                if (!socketSelected.contains(prodBrand)){
                    socketSelected.add(prodBrand);
                }
            }
        }
        if (formFactorSelected.isEmpty()){
            for (MotherboardProduct prod: motherboardFeed.getSearchData()){
                String prodBrand = prod.getFormFactor();
                if (!formFactorSelected.contains(prodBrand)){
                    formFactorSelected.add(prodBrand);
                }
            }
        }

        String brands = "";
        for (String brand: brandSelected){
            brands += String.format("'%s',", brand);
        }
        brands = brands.replaceAll(",$", "");

        String sockets = "";
        for (String socket: socketSelected){
            sockets += String.format("'%s',", socket);
        }
        sockets = sockets.replaceAll(",$", "");

        String formFactor = "";
        for (String form: formFactorSelected){
            formFactor += String.format("'%s',", form);
        }
        formFactor = formFactor.replaceAll(",$", "");

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
        }else if (sortFilter.toLowerCase().contains("rating")) {
            sortBy = "Rating.Average " + desc;
        }

        String sqlStringBuilt = String.format(sqlConst.MOTHERBOARD_SEARCH_FILTER, brands, sockets,
                formFactor, priceMin, priceMax, memoryMin, memoryMax, memorySlotMin, memorySlotMax,
                sortBy);
        System.out.println(sqlStringBuilt);
        loadingNotDone();
        dialog.removeAllViews();
        motherboardFeed = new MotherboardFeedTask(context, dialog, prefs, BUILD_ID);
        motherboardFeed.execute(sqlStringBuilt.replace("WHERE", SQL_FILTER));
        loadingDone();
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (MotherboardProduct prod: motherboardFeed.getSearchData()){
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

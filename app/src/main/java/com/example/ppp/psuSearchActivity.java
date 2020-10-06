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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

import async_tasks.feeds.PsuFeedTask;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.PsuProduct;
import preferences.Preferences;

public class psuSearchActivity extends AppCompatActivity {
    String BUILD_ID = "";
    String SQL_FILTER = "WHERE ";

    static PsuFeedTask psuFeed;
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
    int wattageMin = 0;
    int wattageMax = 10000;
    boolean yesModular = true;
    boolean noModular = true;
    String sortFilter = "Popularity (Ascending)";
    ArrayList<CheckBox> brandList = new ArrayList<>();
    ArrayList<String> brandSelected = new ArrayList<>();
    ArrayList<CheckBox> effRatingList = new ArrayList<>();
    ArrayList<String> effRatingSelected = new ArrayList<>();
    ArrayList<CheckBox> formFactorList = new ArrayList<>();
    ArrayList<String> formFactorSelected = new ArrayList<>();
    ArrayList<CheckBox> modularList = new ArrayList<>();
    ArrayList<String> modularSelected = new ArrayList<>();

    // data
    ArrayList<PsuProduct> filteredData;

    // Constants
    SqlConstants sqlConst = new SqlConstants();

    Bundle savedInstanceState;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.scroll_search);
        context = psuSearchActivity.this;
        dialog = (LinearLayout) findViewById(R.id.searchID);
        loadingWheel = findViewById(R.id.loading_wheel);
        prefs = new Preferences(context);
        BUILD_ID = getIntent().getStringExtra("buildID");
        String buff = getIntent().getStringExtra("sqlFilter");
        if (buff != null){
            SQL_FILTER = buff;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingNotDone();
        psuFeed = new PsuFeedTask(context, dialog, prefs, BUILD_ID);
        psuFeed.execute( sqlConst.PSU_SEARCH_LIST.replace("WHERE", SQL_FILTER));

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
        ArrayList<PsuProduct> data = psuFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            psuFeed.addProduct(data.get(i));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.psu_filter_window, null);

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

        // Set the Brand Choices
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
            for (PsuProduct prod: psuFeed.getSearchData()){
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


        // Set the Efficency  Choices
        if (effRatingList.isEmpty()){
            RelativeLayout eff_rating_selection = popupView.findViewById(R.id.eff_rating_selection);
            final LinearLayout eff_ratingOptions = popupView.findViewById(R.id.eff_rating_options);
            final LinearLayout eff_rating_choice1 = popupView.findViewById(R.id.eff_rating_options_1);
            final LinearLayout eff_rating_choice2 = popupView.findViewById(R.id.eff_rating_options_2);
            final CheckBox deselect = popupView.findViewById(R.id.deselect_all_eff_ratings); //Deselect all
            deselect.setChecked(true);
            deselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    effRatingList.stream().forEach(ch -> ch.setChecked(true));
                }else{
                    effRatingList.stream().forEach(ch -> ch.setChecked(false));

                }

            });

            eff_ratingOptions.setVisibility(View.GONE);
            ArrayList<String> eff_ratings = new ArrayList<>();
            for (PsuProduct prod: psuFeed.getSearchData()){
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$" + effRatingList.isEmpty());

                String eff_rating = prod.getEfficiency();
                if (!eff_ratings.contains(eff_rating) && eff_rating != null){
                    eff_ratings.add(eff_rating);
                    effRatingSelected.add(eff_rating); // initialize as true
                }
            }
            int eff_ratingCount = eff_ratings.size();
            int midPoint = Math.floorDiv(eff_ratingCount, 2);
            for (int i=0; i<midPoint; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        eff_rating_choice1,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(eff_ratings.get(i));
                eff_rating_choice1.addView(checkBoxLayout);
                effRatingList.add(box);
            }
            for (int i=midPoint; i<eff_ratingCount; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        eff_rating_choice2,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(eff_ratings.get(i));
                eff_rating_choice2.addView(checkBoxLayout);
                effRatingList.add(box);
            }
            eff_rating_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (eff_ratingOptions.isShown()){
                        eff_ratingOptions.setVisibility(View.GONE);
                    }else{
                        eff_ratingOptions.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        // Set the Form Factors Choices
        if (formFactorList.isEmpty()){
            RelativeLayout form_factor_selection = popupView.findViewById(R.id.form_factor_selection);
            final LinearLayout form_factorOptions = popupView.findViewById(R.id.form_factor_options);
            final LinearLayout form_factor_choice1 = popupView.findViewById(R.id.form_factor_options_1);
            final LinearLayout form_factor_choice2 = popupView.findViewById(R.id.form_factor_options_2);
            final CheckBox deselect = popupView.findViewById(R.id.deselect_all_form_factors); //Deselect all
            deselect.setChecked(true);
            deselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    formFactorList.stream().forEach(ch -> ch.setChecked(true));
                }else{
                    formFactorList.stream().forEach(ch -> ch.setChecked(false));

                }

            });

            form_factorOptions.setVisibility(View.GONE);
            ArrayList<String> form_factors = new ArrayList<>();
            for (PsuProduct prod: psuFeed.getSearchData()){
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$" + formFactorList.isEmpty());

                String form_factor = prod.getFormFactor();
                if (!form_factors.contains(form_factor) && form_factor != null){
                    form_factors.add(form_factor);
                    formFactorSelected.add(form_factor); // initialize as true
                }
            }
            int form_factorCount = form_factors.size();
            int midPoint = Math.floorDiv(form_factorCount, 2);
            for (int i=0; i<midPoint; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        form_factor_choice1,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(form_factors.get(i));
                form_factor_choice1.addView(checkBoxLayout);
                formFactorList.add(box);
            }
            for (int i=midPoint; i<form_factorCount; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        form_factor_choice2,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(form_factors.get(i));
                form_factor_choice2.addView(checkBoxLayout);
                formFactorList.add(box);
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

        // modular
        if (modularList.isEmpty()){
            RelativeLayout modular_selection = popupView.findViewById(R.id.modular_selection);
            final LinearLayout modularOptions = popupView.findViewById(R.id.modular_options);
            final LinearLayout modular_choice1 = popupView.findViewById(R.id.modular_options_1);
            final LinearLayout modular_choice2 = popupView.findViewById(R.id.modular_options_2);
            final CheckBox deselect = popupView.findViewById(R.id.deselect_all_modular); //Deselect all
            deselect.setChecked(true);
            deselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    modularList.stream().forEach(ch -> ch.setChecked(true));
                }else{
                    modularList.stream().forEach(ch -> ch.setChecked(false));

                }

            });

            modularOptions.setVisibility(View.GONE);
            ArrayList<String> modulars = new ArrayList<>();
            for (PsuProduct prod: psuFeed.getSearchData()){
                String modular = prod.getModular();
                if (!modulars.contains(modular) && modular != null){
                    modulars.add(modular);
                    modularSelected.add(modular); // initialize as true
                }
            }
            int modularCount = modulars.size();
            int midPoint = Math.floorDiv(modularCount, 2);
            for (int i=0; i<midPoint; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        modular_choice1,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(modulars.get(i));
                modular_choice1.addView(checkBoxLayout);
                modularList.add(box);
            }
            for (int i=midPoint; i<modularCount; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        modular_choice2,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(modulars.get(i));
                modular_choice2.addView(checkBoxLayout);
                modularList.add(box);
            }
            modular_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (modularOptions.isShown()){
                        modularOptions.setVisibility(View.GONE);
                    }else{
                        modularOptions.setVisibility(View.VISIBLE);
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

        // Set wattage choices
        final RangeSeekBar<Integer> wattageBar = popupView.findViewById(R.id.wattage_seek_bar);
        final int maxWattage = getHighestWattageProdcuct() + 10;
        wattageBar.setRangeValues(0, maxWattage);
        final RelativeLayout wattageChoice = popupView.findViewById(R.id.wattage_selection);
        wattageBar.setVisibility(View.GONE);
        wattageChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wattageBar.isShown()){
                    wattageBar.setVisibility(View.GONE);
                }else{
                    wattageBar.setVisibility(View.VISIBLE);
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

                brandList.stream().forEach(cb -> cb.setChecked(true));
                formFactorList.stream().forEach(cb -> cb.setChecked(true));
                effRatingList.stream().forEach(cb -> cb.setChecked(true));

                psuFeed = new PsuFeedTask(context, dialog, prefs, BUILD_ID);
                dialog.removeAllViews();
                psuFeed.execute(sqlConst.PSU_SEARCH_LIST.replace("WHERE", SQL_FILTER));
                filterWindow.dismiss();
            }
        });

        // Apply button
        Button filterButton = popupView.findViewById(R.id.apply_button);
        filterButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // setting filters as parameters
                priceMin = priceBar.getSelectedMinValue();
                priceMax = priceBar.getSelectedMaxValue();
                wattageMin = wattageBar.getSelectedMinValue();
                wattageMax = wattageBar.getSelectedMaxValue();


                brandSelected.clear();
                formFactorSelected.clear();
                effRatingSelected.clear();
                modularSelected.clear();

                brandList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        brandSelected.add((String) cb.getText());
                    };
                });

                effRatingList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        effRatingSelected.add((String) cb.getText());
                    };
                });

                formFactorList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        formFactorSelected.add((String) cb.getText());
                    };
                });

                modularList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        modularSelected.add((String) cb.getText());
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
        View popupView = inflater.inflate(R.layout.psu_sort_window, null);

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
            filteredData = psuFeed.getSearchData();


            dialog.removeAllViews();
            psuFeed = new PsuFeedTask(context, dialog, prefs, BUILD_ID);
            psuFeed.execute(sqlConst.PSU_SEARCH_LIST.replace("WHERE", SQL_FILTER));
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
            for (PsuProduct prod: psuFeed.getSearchData()){
                String prodBrand = prod.getManufacturer();
                if (!brandSelected.contains(prodBrand)){
                    brandSelected.add(prodBrand);
                }
            }
        }
        if (effRatingSelected.isEmpty()){
            for (PsuProduct prod: psuFeed.getSearchData()){
                String prodBrand = prod.getEfficiency();
                if (!effRatingSelected.contains(prodBrand)){
                    effRatingSelected.add(prodBrand);
                }
            }
        }
        if (formFactorSelected.isEmpty()){
            for (PsuProduct prod: psuFeed.getSearchData()){
                String prodBrand = prod.getFormFactor();
                if (!formFactorSelected.contains(prodBrand)){
                    formFactorSelected.add(prodBrand);
                }
            }
        }

        if (modularSelected.isEmpty()){
            for (PsuProduct prod: psuFeed.getSearchData()){
                String prodBrand = prod.getModular();
                if (!modularSelected.contains(prodBrand)){
                    modularSelected.add(prodBrand);
                }
            }
        }

        String brands = "";
        for (String brand: brandSelected){
            brands += String.format("'%s',", brand);
        }
        brands = brands.replaceAll(",$", "");

        String effRatings = "";
        for (String buff: effRatingSelected){
            effRatings += String.format("'%s',", buff);
        }
        effRatings = effRatings.replaceAll(",$", "");

        String formFactors = "";
        for (String buff: formFactorSelected){
            formFactors += String.format("'%s',", buff);
        }
        formFactors = formFactors.replaceAll(",$", "");

        String modulars = "";
        for (String buff: modularSelected){
            modulars += String.format("'%s',", buff);
        }
        modulars = modulars.replaceAll(",$", "");

        String modYes = (yesModular) ? "Yes" : "";
        String modNo = (noModular) ? "No": "";


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
        }else if (sortFilter.toLowerCase().contains("wattage")){
            sortBy = "CAST(PSU.`Wattage` AS INT) " + desc;
        }

        String sqlStringBuilt = String.format(sqlConst.PSU_SEARCH_FILTER, brands,
                 effRatings, formFactors, modulars, priceMin, priceMax,
                 wattageMin, wattageMax ,sortBy);
        System.out.println(sqlStringBuilt);
        dialogScroll.smoothScrollTo(0,0);
        dialog.removeAllViews();
        loadingNotDone();
        psuFeed = new PsuFeedTask(context, dialog, prefs, BUILD_ID);
        psuFeed.execute(sqlStringBuilt.replace("WHERE", SQL_FILTER));
        loadingDone();
    }

    private int getHighestWattageProdcuct() {
        int max = 0;
        for (PsuProduct prof: psuFeed.getSearchData()){
            String buff = prof.getWattage();
            if (buff != null){
                int wattage = stringToInteger(buff);
                if (wattage > max){
                    max = wattage;
                }
            }
        }
        return max + 10;
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (PsuProduct prod: psuFeed.getSearchData()){
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

    private Double stringToDouble(String val){
        if (val != null){
            String value = val.replaceAll("[^0-9.]", "");
            if (value == ""){
                return 0.0;
            }
            return Double.valueOf(value);
        }
        return 0.0;
    }

    private Integer stringToInteger(String val){
        if (val != null){
            String value = val.replaceAll("[^0-9]", "");
            if (value == ""){
                return 0;
            }
            return Integer.valueOf(value);
        }
        return 0;
    }


}


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

import async_tasks.feeds.CaseFeedTask;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.CaseProduct;
import preferences.Preferences;

public class caseSearchActivity extends AppCompatActivity {
    static CaseFeedTask caseFeed;
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
    ArrayList<CheckBox> psuShroudList = new ArrayList<>();
    ArrayList<String> psuShroudSelected = new ArrayList<>();
    ArrayList<CheckBox> sidePanelList = new ArrayList<>();
    ArrayList<String> sidePanelSelected = new ArrayList<>();
    ArrayList<CheckBox> towerList = new ArrayList<>();
    ArrayList<String> towerSelected = new ArrayList<>();

    // data
    ArrayList<CaseProduct> filteredData;

    // Constants
    SqlConstants sqlConst = new SqlConstants();

    Bundle savedInstanceState;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.scroll_search);
        context = caseSearchActivity.this;
        dialog = (LinearLayout) findViewById(R.id.searchID);
        loadingWheel = findViewById(R.id.loading_wheel);
        prefs = new Preferences(context);


    }

    @Override
    public void onStart() {
        super.onStart();
        loadingNotDone();
        caseFeed = new CaseFeedTask(context, dialog, prefs);
        caseFeed.execute( sqlConst.CASE_SEARCH_LIST);

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
        ArrayList<CaseProduct> data = caseFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            caseFeed.addProduct(data.get(i));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.case_filter_window, null);

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
            for (CaseProduct prod: caseFeed.getSearchData()){
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


        // Set the PSU Shroud  Choices
        if (psuShroudList.isEmpty()){
            RelativeLayout psu_shroud_selection = popupView.findViewById(R.id.psu_shroud_selection);
            final LinearLayout psu_shroudOptions = popupView.findViewById(R.id.psu_shroud_options);
            final LinearLayout psu_shroud_choice1 = popupView.findViewById(R.id.psu_shroud_options_1);
            final LinearLayout psu_shroud_choice2 = popupView.findViewById(R.id.psu_shroud_options_2);
            final CheckBox deselect = popupView.findViewById(R.id.deselect_all_psu_shrouds); //Deselect all
            deselect.setChecked(true);
            deselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    psuShroudList.stream().forEach(ch -> ch.setChecked(true));
                }else{
                    psuShroudList.stream().forEach(ch -> ch.setChecked(false));

                }

            });

            psu_shroudOptions.setVisibility(View.GONE);
            ArrayList<String> psu_shrouds = new ArrayList<>();
            for (CaseProduct prod: caseFeed.getSearchData()){
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$" + psuShroudList.isEmpty());

                String psu_shroud = prod.getPsuShroud();
                if (!psu_shrouds.contains(psu_shroud) && psu_shroud != null){
                    psu_shrouds.add(psu_shroud);
                    psuShroudSelected.add(psu_shroud); // initialize as true
                }
            }
            int psu_shroudCount = psu_shrouds.size();
            int midPoint = Math.floorDiv(psu_shroudCount, 2);
            for (int i=0; i<midPoint; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        psu_shroud_choice1,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(psu_shrouds.get(i));
                psu_shroud_choice1.addView(checkBoxLayout);
                psuShroudList.add(box);
            }
            for (int i=midPoint; i<psu_shroudCount; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        psu_shroud_choice2,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(psu_shrouds.get(i));
                psu_shroud_choice2.addView(checkBoxLayout);
                psuShroudList.add(box);
            }
            psu_shroud_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (psu_shroudOptions.isShown()){
                        psu_shroudOptions.setVisibility(View.GONE);
                    }else{
                        psu_shroudOptions.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        // Set the Side panel Choices
        if (sidePanelList.isEmpty()){
            RelativeLayout side_panel_selection = popupView.findViewById(R.id.side_panel_selection);
            final LinearLayout side_panelOptions = popupView.findViewById(R.id.side_panel_options);
            final LinearLayout side_panel_choice1 = popupView.findViewById(R.id.side_panel_options_1);
            final LinearLayout side_panel_choice2 = popupView.findViewById(R.id.side_panel_options_2);
            final CheckBox deselect = popupView.findViewById(R.id.deselect_all_side_panels); //Deselect all
            deselect.setChecked(true);
            deselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    sidePanelList.stream().forEach(ch -> ch.setChecked(true));
                }else{
                    sidePanelList.stream().forEach(ch -> ch.setChecked(false));

                }

            });

            side_panelOptions.setVisibility(View.GONE);
            ArrayList<String> side_panels = new ArrayList<>();
            for (CaseProduct prod: caseFeed.getSearchData()){
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$" + sidePanelList.isEmpty());

                String side_panel = prod.getSidePanel();
                if (!side_panels.contains(side_panel) && side_panel != null){
                    side_panels.add(side_panel);
                    sidePanelSelected.add(side_panel); // initialize as true
                }
            }
            int side_panelCount = side_panels.size();
            int midPoint = Math.floorDiv(side_panelCount, 2);
            for (int i=0; i<midPoint; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        side_panel_choice1,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(side_panels.get(i));
                side_panel_choice1.addView(checkBoxLayout);
                sidePanelList.add(box);
            }
            for (int i=midPoint; i<side_panelCount; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        side_panel_choice2,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(side_panels.get(i));
                side_panel_choice2.addView(checkBoxLayout);
                sidePanelList.add(box);
            }
            side_panel_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (side_panelOptions.isShown()){
                        side_panelOptions.setVisibility(View.GONE);
                    }else{
                        side_panelOptions.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        // Tower 
        if (towerList.isEmpty()){
            RelativeLayout tower_selection = popupView.findViewById(R.id.tower_selection);
            final LinearLayout towerOptions = popupView.findViewById(R.id.tower_options);
            final LinearLayout tower_choice1 = popupView.findViewById(R.id.tower_options_1);
            final LinearLayout tower_choice2 = popupView.findViewById(R.id.tower_options_2);
            final CheckBox deselect = popupView.findViewById(R.id.deselect_all_towers); //Deselect all
            deselect.setChecked(true);
            deselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    towerList.stream().forEach(ch -> ch.setChecked(true));
                }else{
                    towerList.stream().forEach(ch -> ch.setChecked(false));

                }

            });

            towerOptions.setVisibility(View.GONE);
            ArrayList<String> towers = new ArrayList<>();
            for (CaseProduct prod: caseFeed.getSearchData()){
                String tower = prod.getTower();
                if (!towers.contains(tower) && tower != null){
                    towers.add(tower);
                    towerSelected.add(tower); // initialize as true
                }
            }
            int towerCount = towers.size();
            int midPoint = Math.floorDiv(towerCount, 2);
            for (int i=0; i<midPoint; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        tower_choice1,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(towers.get(i));
                tower_choice1.addView(checkBoxLayout);
                towerList.add(box);
            }
            for (int i=midPoint; i<towerCount; i++){
                View checkBoxLayout = LayoutInflater.from(context).inflate(R.layout.checkbox_template,
                        tower_choice2,
                        false);
                CheckBox box = checkBoxLayout.findViewById(R.id.checkBox);
                box.setChecked(true);
                box.setText(towers.get(i));
                tower_choice2.addView(checkBoxLayout);
                towerList.add(box);
            }
            tower_selection.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (towerOptions.isShown()){
                        towerOptions.setVisibility(View.GONE);
                    }else{
                        towerOptions.setVisibility(View.VISIBLE);
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
                sidePanelList.stream().forEach(cb -> cb.setChecked(true));
                psuShroudList.stream().forEach(cb -> cb.setChecked(true));

                caseFeed = new CaseFeedTask(context, dialog, prefs);
                dialog.removeAllViews();
                caseFeed.execute(sqlConst.CASE_SEARCH_LIST);
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

                brandSelected.clear();
                sidePanelSelected.clear();
                psuShroudSelected.clear();
                towerSelected.clear();

                brandList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        brandSelected.add((String) cb.getText());
                    };
                });

                psuShroudList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        psuShroudSelected.add((String) cb.getText());
                    };
                });

                sidePanelList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        sidePanelSelected.add((String) cb.getText());
                    };
                });

                towerList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        towerSelected.add((String) cb.getText());
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
        View popupView = inflater.inflate(R.layout.case_sort_window, null);

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
            filteredData = caseFeed.getSearchData();


            dialog.removeAllViews();
            caseFeed = new CaseFeedTask(context, dialog, prefs);
            caseFeed.execute(sqlConst.CASE_SEARCH_LIST);
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
            for (CaseProduct prod: caseFeed.getSearchData()){
                String prodBrand = prod.getManufacturer();
                if (!brandSelected.contains(prodBrand)){
                    brandSelected.add(prodBrand);
                }
            }
        }
        if (psuShroudSelected.isEmpty()){
            for (CaseProduct prod: caseFeed.getSearchData()){
                String prodBrand = prod.getPsuShroud();
                if (!psuShroudSelected.contains(prodBrand)){
                    psuShroudSelected.add(prodBrand);
                }
            }
        }
        if (sidePanelSelected.isEmpty()){
            for (CaseProduct prod: caseFeed.getSearchData()){
                String prodBrand = prod.getSidePanel();
                if (!sidePanelSelected.contains(prodBrand)){
                    sidePanelSelected.add(prodBrand);
                }
            }
        }

        if (towerSelected.isEmpty()){
            for (CaseProduct prod: caseFeed.getSearchData()){
                String prodBrand = prod.getTower();
                if (!towerSelected.contains(prodBrand)){
                    towerSelected.add(prodBrand);
                }
            }
        }

        String brands = "";
        for (String brand: brandSelected){
            brands += String.format("'%s',", brand);
        }
        brands = brands.replaceAll(",$", "");

        String psuShrouds = "";
        for (String buff: psuShroudSelected){
            psuShrouds += String.format("'%s',", buff);
        }
        psuShrouds = psuShrouds.replaceAll(",$", "");

        String sidePanels = "";
        for (String buff: sidePanelSelected){
            sidePanels += String.format("'%s',", buff);
        }
        sidePanels = sidePanels.replaceAll(",$", "");

        String towers = "";
        for (String buff: towerSelected){
            towers += String.format("'%s',", buff);
        }
        towers = towers.replaceAll(",$", "");

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
        }

        String sqlStringBuilt = String.format(sqlConst.CASE_SEARCH_FILTER, brands,
                 psuShrouds, sidePanels, towers, priceMin, priceMax, sortBy);

        System.out.println(sqlStringBuilt);
        dialogScroll.smoothScrollTo(0,0);
        dialog.removeAllViews();
        loadingNotDone();
        caseFeed = new CaseFeedTask(context, dialog, prefs);
        caseFeed.execute(sqlStringBuilt);
        loadingDone();
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (CaseProduct prod: caseFeed.getSearchData()){
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


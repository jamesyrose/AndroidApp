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

import async_tasks.general.RetrieveMemoryFeedTask;
import pcpp_data.products.MemoryProduct;
import pcpp_data.sorters.MemoryProductSort;
import preferences.Preferences;

public class memorySearchActivity extends AppCompatActivity {
    static RetrieveMemoryFeedTask memoryFeed;
    Preferences prefs;
    LinearLayout dialog;
    PopupWindow filterWindow;
    PopupWindow sortWindow;
    Context context;

    // Data filters
    int priceMin = 0;
    int priceMax = 1000000;
    int memoryMin = 0;
    int memoryMax = 256;
    int memorySpeedMin = 0;
    int memorySpeedMax = 10000;
    int numModsMin = 0;
    int numModsMax = 16;
    double pricePerGbMin = 0.0;
    double pricePerGbMax = 20.0;
    boolean eccMemory = true;
    boolean nonEccMemory = true;

    ArrayList<CheckBox> brandList = new ArrayList<>();
    ArrayList<String> brandSelected = new ArrayList<>();

    String sortFilter = "Popularity (Ascending)";

    // data
    ArrayList<MemoryProduct> filteredData;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_search);
        context = memorySearchActivity.this;
        loadingNotDone();
        dialog = (LinearLayout) findViewById(R.id.searchID);

        prefs = new Preferences(context);

        memoryFeed = new RetrieveMemoryFeedTask(context, dialog, prefs);
        memoryFeed.execute();

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
        ArrayList<MemoryProduct> data = memoryFeed.getSearchData();
        if (filteredData != null) {
            data = filteredData;
        }
        int end = currentChildCount + 30;
        end = (data.size() > end) ? end : data.size();
        for (int i=currentChildCount; i<end; i++){
            memoryFeed.addProduct(data.get(i));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.memory_filter_window, null);

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
            brandOptions.setVisibility(View.GONE);
            ArrayList<String> brands = new ArrayList<>();
            for (MemoryProduct prod: memoryFeed.getSearchData()){
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
        final RangeSeekBar<Integer> memoryBar = popupView.findViewById(R.id.memory_per_mod_seek_bar);
        memoryBar.setRangeValues(0, 256);
        final RelativeLayout memoryChoice = popupView.findViewById(R.id.memory_per_mod_selection);
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
        final RangeSeekBar<Integer> memoryModulesBar = popupView.findViewById(R.id.memory_modules_seek_bar);
        memoryModulesBar.setRangeValues(0, 16);
        final RelativeLayout memorySlotChoice = popupView.findViewById(R.id.memory_modules_selection);
        memoryModulesBar.setVisibility(View.GONE);
        memorySlotChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memoryModulesBar.isShown()){
                    memoryModulesBar.setVisibility(View.GONE);
                }else{
                    memoryModulesBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set memory speed range
        final RangeSeekBar<Integer> memorySpeedBar = popupView.findViewById(R.id.memory_speed_seek_bar);
        memorySpeedBar.setRangeValues(0, 10000);
        final RelativeLayout memorySpeedChoice = popupView.findViewById(R.id.memory_speed_selection);
        memorySpeedBar.setVisibility(View.GONE);
        memorySpeedChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memorySpeedBar.isShown()){
                    memorySpeedBar.setVisibility(View.GONE);
                }else{
                    memorySpeedBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set price Per gb range
        final RangeSeekBar<Double> pricePerGbBar = popupView.findViewById(R.id.price_gb_seek_bar);
        pricePerGbBar.setRangeValues(0.0, 20.0);
        final RelativeLayout pricePerGbChoice = popupView.findViewById(R.id.price_gb_selection);
        pricePerGbBar.setVisibility(View.GONE);
        pricePerGbChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pricePerGbBar.isShown()){
                    pricePerGbBar.setVisibility(View.GONE);
                }else{
                    pricePerGbBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // ECC and heat spreader;
        final RelativeLayout otherSelection = popupView.findViewById(R.id.others_selection);
        final LinearLayout otherOptions = popupView.findViewById(R.id.other_options);
        otherOptions.setVisibility(View.GONE);
        final CheckBox eccOption = popupView.findViewById(R.id.ecc_option);
        final CheckBox nonEccOption = popupView.findViewById(R.id.non_ecc_option);
        eccOption.setChecked(true);
        nonEccOption.setChecked(true);
        otherSelection.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (otherOptions.isShown()){
                    otherOptions.setVisibility(View.GONE);
                }else{
                    otherOptions.setVisibility(View.VISIBLE);
                }
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
                memoryBar.setSelectedMinValue(0);
                memoryBar.setSelectedMaxValue(256);
                memoryModulesBar.setSelectedMinValue(0);
                memoryModulesBar.setSelectedMaxValue(16);
                memorySpeedBar.setSelectedMinValue(0);
                memorySpeedBar.setSelectedMaxValue(10000);
                pricePerGbBar.setSelectedMinValue(0.0);
                pricePerGbBar.setSelectedMaxValue(20.0);
                eccOption.setChecked(true);
                nonEccOption.setChecked(true);
                brandList.stream().forEach(cb -> cb.setChecked(true));

                filteredData = memoryFeed.getSearchData();
                dialog.removeAllViews();
                for (int i=0; i<30; i++){
                    memoryFeed.addProduct(filteredData.get(i));
                }
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
                memorySpeedMin = memorySpeedBar.getSelectedMinValue();
                memorySpeedMax = memorySpeedBar.getSelectedMaxValue();
                numModsMin = memoryModulesBar.getSelectedMinValue();
                numModsMax = memoryModulesBar.getSelectedMaxValue();
                pricePerGbMin  = pricePerGbBar.getSelectedMinValue();
                pricePerGbMax = pricePerGbBar.getSelectedMaxValue();
                eccMemory = eccOption.isChecked();
                nonEccMemory = nonEccOption.isChecked();

                brandSelected.clear();
                brandList.stream().forEach(cb -> {
                    if (cb.isChecked()){
                        System.out.println(cb.getText());
                        brandSelected.add((String) cb.getText());
                    };
                });

                System.out.println("###################22222");

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
                memoryBar.setSelectedMinValue(priceBar.getSelectedMinValue());
                memoryBar.setSelectedMaxValue(priceBar.getSelectedMaxValue());
                memoryModulesBar.setSelectedMinValue(memoryModulesBar.getSelectedMinValue());
                memoryModulesBar.setSelectedMaxValue(memoryModulesBar.getSelectedMaxValue());
                memorySpeedBar.setSelectedMinValue(memorySpeedBar.getSelectedMinValue());
                memorySpeedBar.setSelectedMaxValue(memorySpeedBar.getSelectedMaxValue());
                pricePerGbBar.setSelectedMinValue(pricePerGbBar.getSelectedMinValue());
                pricePerGbBar.setSelectedMaxValue(pricePerGbBar.getSelectedMaxValue());
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
                memoryModulesBar.setSelectedMinValue(memoryModulesBar.getSelectedMinValue());
                memoryModulesBar.setSelectedMaxValue(memoryModulesBar.getSelectedMaxValue());
                memorySpeedBar.setSelectedMinValue(memorySpeedBar.getSelectedMinValue());
                memorySpeedBar.setSelectedMaxValue(memorySpeedBar.getSelectedMaxValue());
                pricePerGbBar.setSelectedMinValue(pricePerGbBar.getSelectedMinValue());
                pricePerGbBar.setSelectedMaxValue(pricePerGbBar.getSelectedMaxValue());
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
        View popupView = inflater.inflate(R.layout.memory_sort_window, null);

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
                filteredData = memoryFeed.getSearchData();
                dialog.removeAllViews();
                for (int i=0; i<30; i++){
                    memoryFeed.addProduct(filteredData.get(i));
                }
                sortWindow.dismiss();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterData(){
        ArrayList<MemoryProduct> filtered = new ArrayList<>();
        System.out.println(sortFilter);
        dialog.removeAllViews();

        System.out.println(brandSelected.toString());
        for (MemoryProduct product: memoryFeed.getSearchData()){
            if ((brandSelected.contains(product.getManufacturer()) || brandSelected.isEmpty()) &&
                    priceMin <= product.getBestPrice() &&
                    priceMax >= product.getBestPrice()  &&
                    memoryMin <= getModuleSize(product) &&
                    memoryMax >= getModuleSize(product) &&
                    memorySpeedMin <= getMemorySpeed(product) &&
                    memorySpeedMax >= getMemorySpeed(product)  &&
                    numModsMin <= getNumModules(product) &&
                    numModsMax >= getNumModules(product) &&
                    pricePerGbMin <= getPricePerGb(product) &&
                    pricePerGbMax >= getPricePerGb(product)
             ){
                if (eccMemory){
                    if (isECC(product)){
                        filtered.add(product);
                    }
                }
                if (nonEccMemory){
                    if (!isECC(product)){
                        filtered.add(product);
                    }
                }
            }
        }
        System.out.println("######" + filtered.size());
        ArrayList<MemoryProduct> sorted = new ArrayList<>();
        // Sorted
        MemoryProductSort sorter = new MemoryProductSort();
        if (sortFilter.toLowerCase().contains("popularity")) {
            sorted = sorter.sortPopularity(filtered);
        }else if (sortFilter.toLowerCase().contains("name")){
            sorted = sorter.sortName(filtered);
        }else if (sortFilter.toLowerCase().contains("price")){
            sorted = sorter.sortPrice(filtered);
        }else if (sortFilter.toLowerCase().contains("rating")){
            sorted = sorter.sortRating(filtered);
        }else if (sortFilter.toLowerCase().contains("price / gb")){
            sorted = sorter.sortPricePerGB(filtered);
        }else if (sortFilter.toLowerCase().contains("speed")) {
            sorted = sorter.sortSpeed(filtered);
        }else {
            sorted = sorter.sortPopularity(filtered);
        }

        // get children views
        ArrayList<View> children = memoryFeed.getProductLayoutView();
        loadingNotDone();
        dialog.removeAllViews();
        dialog.setVisibility(View.GONE);
        if (sortFilter.toLowerCase().contains("descending")){
            Collections.reverse(sorted);
            for (MemoryProduct product: sorted){
                int id = product.getViewID();
                for (View child: children){
                    if (child.getId() == id){
                        dialog.addView(child);
                    }
                }
            }
        }else {
            for (MemoryProduct product: sorted){
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
        for (MemoryProduct prod: memoryFeed.getSearchData()){
            double bestPrice = prod.getBestPrice();
            if (bestPrice > maxPrice)
                maxPrice = bestPrice;
        }
        return (int) Math.floor(maxPrice);
    }

    public int getNumModules(MemoryProduct prod){
        String modString = prod.getModules();
        if (modString != null){
            String mod = modString.split("x")[0];
            return stringToInteger(mod);
        }
        return 1;
    }

    public int getModuleSize(MemoryProduct prod){
        String modString = prod.getModules();
        if (modString != null){
            String mod = modString.split("x")[1];
            return stringToInteger(mod);
        }
        return 0;
    }

    public boolean isECC(MemoryProduct prod){
        String buff = prod.getEcc();
        if (buff != null){
            if (buff.toLowerCase().contains("non-ecc")){
                return false;
            }
        }
        return true;
    }

    public double getPricePerGb(MemoryProduct prod){
        String buff = prod.getPricePerGB();
        if (buff != null) {
            return stringToDouble(buff);
        }
        return 100.0; // Arbitrary for sort to put this last
    }

    public int getMemorySpeed(MemoryProduct prod){
        String buff = prod.getMemorySpeed();
        if (buff != null){
            return stringToInteger(buff);
        }
        return 0;
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

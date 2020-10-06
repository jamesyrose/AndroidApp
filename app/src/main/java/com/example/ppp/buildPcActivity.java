package com.example.ppp;

import android.animation.Animator;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import async_tasks.feeds.ProductPopup;
import async_tasks.general.DownloadImageGallery;
import async_tasks.general.DownloadSellers;
import async_tasks.general.DownloadSpecs;
import pcpp_data.products.GeneralProduct;
import pcpp_data.queries.SingleProductQuery;
import pcpp_data.sqllite.database;
import pcpp_data.sqllite.saveBuilds;
import preferences.Preferences;
import async_tasks.general.DownloadImageTask;

public class buildPcActivity extends AppCompatActivity {
    Context context;
    Preferences prefs;
    LinearLayout dialog;
    ScrollView dialogScroll;
    private double total = 0.0;
    private int wattage = 0;
    private String compatible = "";

    String BUILD_ID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.build_a_pc);
        //
        context = buildPcActivity.this;
        prefs = new Preferences(context);
        dialog = findViewById(R.id.main_vert_layout);
        dialogScroll = findViewById(R.id.scroll_window);
        //
        BUILD_ID = getIntent().getStringExtra("BUILD_ID");

        // Save, new and delete
        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            // inflate
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View delWindow = inflater.inflate(R.layout.you_sure_window, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(delWindow, width, height, focusable);
            popupWindow.setAnimationStyle(R.style.popup_animation);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            Button delBtn = delWindow.findViewById(R.id.delete_button);
            Button saveBtn = delWindow.findViewById(R.id.save_button);
            ImageButton closeBtn = delWindow.findViewById(R.id.close_button);

            delBtn.setOnClickListener(v2 -> {
                new saveBuilds(context, BUILD_ID).deleteBuild();
                BUILD_ID = randomBuildID();
                removeAllOldViews();
                changesMade();
                Toast.makeText(getApplicationContext(), "Build Has Been Deleted", Toast.LENGTH_LONG).show();
                resetTitleNameToActionBar();
                popupWindow.dismiss();
            });

            delWindow.setOnTouchListener((v1, event) -> {
                popupWindow.dismiss();
                return true;
            });

            saveBtn.setOnClickListener(v2 ->{
                popupWindow.dismiss();
            });

            closeBtn.setOnClickListener(v2 ->{
                popupWindow.dismiss();
            });
        });

        Button newButton = findViewById(R.id.new_button);
        newButton.setOnClickListener(v ->{
            BUILD_ID = randomBuildID();
            resetTitleNameToActionBar();
            removeAllOldViews();
            changesMade();
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            // inflate
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View saveWindow = inflater.inflate(R.layout.save_window, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(saveWindow, width, height, focusable);
            popupWindow.setAnimationStyle(R.style.popup_animation);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            // dismiss the popup window when touched
            saveWindow.setOnTouchListener((v1, event) -> {
                popupWindow.dismiss();
                return true;
            });

            // close button
            ImageButton closeButton = saveWindow.findViewById(R.id.close_button);
            closeButton.setOnClickListener(cb -> popupWindow.dismiss());

            // save funtion
            EditText editName = saveWindow.findViewById(R.id.build_name);
            editName.setText(new saveBuilds(context, BUILD_ID).getName());
            Button saveWithName = saveWindow.findViewById(R.id.save_button);

            saveWithName.setOnClickListener(saveView -> {
                String buildName = editName.getText().toString();
                saveBuilds buff = new saveBuilds(context, BUILD_ID);
                buff.setName(buildName);
                buff.saveBuild();

                Toast.makeText(getApplicationContext(), "Build Has Been Saved", Toast.LENGTH_LONG).show();
                addTitleNameToActionBar();
                popupWindow.dismiss();
            });
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        // Initialize add buttons
        addTitleNameToActionBar();
        removeAllOldViews();
        changesMade();
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

    private void addTitleNameToActionBar(){
        // changing title name for saved build
        ActivityInfo activityInfo = null;
        try {
            activityInfo = getPackageManager().getActivityInfo(
                    getComponentName(), PackageManager.GET_META_DATA);
            String title = activityInfo.loadLabel(getPackageManager())
                    .toString();
            String name = new saveBuilds(context, BUILD_ID).getName();
            if (!name.equals("")){
                setTitle(String.format("%s - %s", title, name));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void resetTitleNameToActionBar() {
        setTitle("Build PC");
    }

    private void removeAllOldViews(){
        String[] options = {"CPU", "CPU_Cooler", "Motherboard", "Memory", "Storage", "GPU", "Power_Supply", "Case"};
        for (String option: options){
            removeOldViews(option);
            addOption(option);
        }
    }

    private void removeOldViews(String product){
        int id_option = getResources().getIdentifier(
                String.format("%s_option", product.toLowerCase()),
                "id",
                context.getPackageName());
        LinearLayout option = findViewById(id_option);

        int id_add = getResources().getIdentifier(
                String.format("add_%s", product.toLowerCase()),
                "id",
                context.getPackageName());
        LinearLayout add = findViewById(id_add);

        option.removeAllViews();
        System.out.println(product);
        option.addView(add);
    }

    private void addOption(String product){
        // Modify resource
        int id_selection = getResources().getIdentifier(
                String.format("%s_selection", product.toLowerCase()),
                "id",
                context.getPackageName());
        LinearLayout selection = findViewById(id_selection);
        int id_option = getResources().getIdentifier(
                String.format("%s_option", product.toLowerCase()),
                "id",
                context.getPackageName());
        LinearLayout option = findViewById(id_option);

        // Set actions
        int id_text = getResources().getIdentifier(
                String.format("add_product_%s", product.toLowerCase()),
                "id",
                context.getPackageName());
        TextView addButton = findViewById(id_text);
        addButton.setText(String.format("Choose %s", product.replace("_", " ")));
        selection .setOnClickListener(v -> {
            if (option.isShown()){
                option.setVisibility(View.GONE);
            } else {
                option.setVisibility(View.VISIBLE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (product.toLowerCase()){
                    case "cpu":
                        goToCpuSearch(v);
                        break;
                    case "cpu_cooler":
                        goToCpuCoolerSearch(v);
                        break;
                    case "motherboard":
                        goToMotherboardSearch(v);
                        break;
                    case "memory":
                        goToMemorySearch(v);
                        break;
                    case "storage":
                        goToStorageSearch(v);
                        break;
                    case "gpu":
                        goToGpuSearch(v);
                        break;
                    case "power_supply":
                        goToPsuSearch(v);
                        break;
                    case "case":
                        goToCaseSearch(v);
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void addProduct(int productID){

        // Getting product
        GeneralProduct product = new GeneralProduct(context, productID);
        String productTypeBuff = product.getProductType();
        String productType = (productTypeBuff.equals("PSU")) ? "Power Supply" : productTypeBuff;
        productType = productType.replace("Cases", "Case");
        String productName = product.getProductName();
        // finding the view to place it in
        int id_option = getResources().getIdentifier(
                String.format("%s_option", productType.toLowerCase().replace(" ", "_")),
                "id",
                context.getPackageName());
        LinearLayout option = findViewById(id_option);

        // Layout Template
        View newView = LayoutInflater.from(context).inflate(R.layout.build_a_pc_added_product,
                option,
                false);

        // Modify values
        TextView productNameLabel  = newView.findViewById(R.id.product_name_label);
        RatingBar ratingBar = newView.findViewById(R.id.rating_bar);
        TextView ratingValue = newView.findViewById(R.id.rating_count);
        TextView price = newView.findViewById(R.id.price_value);
        TextView shipping = newView.findViewById(R.id.shipping_value);
        productNameLabel.setText(productName);
        ratingBar.setRating((float) product.getRatingAverage());
        ratingValue.setText(String.format("(%d)", product.getRatingCount()));
        double priceBuff = product.getPrice();
        String priceString = (priceBuff< 0) ? "N/A" : String.valueOf(priceBuff);

        price.setText(String.format("%s %s", prefs.getCurrencySymbol(), priceString));
        double buff = product.getShipping();
        String shipBuff = "N/A";
        if (buff == -1){
            shipBuff = "Amazon Prime";
        }else if (buff == 0 ){
            shipBuff = "Free";
        }else if (buff > 0){
            shipBuff = String.valueOf(buff);
        }
        shipping.setText(shipBuff);

        // Add Image
        ImageView img = newView.findViewById(R.id.product_image);
        System.out.println(product.getDispImage());
        new DownloadImageTask(img).execute(product.getDispImage());

        // Buy Button
        Button buyButton = newView.findViewById(R.id.buy_button);
        buyButton.setOnClickListener(v -> {
            new ProductPopup().productPopup(context, prefs, v, productID, productName, product.getProductType());;
        });
        newView.setOnClickListener(v -> {
            new ProductPopup().productPopup(context, prefs, v, productID, productName, product.getProductType());;
        });

        // remove item
        TextView removeButton = newView.findViewById(R.id.delete_button);
        removeButton.setOnClickListener(v -> {
            removeAllOldViews();
            option.setVisibility(View.VISIBLE);
            new saveBuilds(context, BUILD_ID).removeFromBuild(productID);
            changesMade();
        });
        option.addView(newView, 0);
    }

    public void changesMade(){
        ArrayList<Integer> buildProducts = new saveBuilds(context, BUILD_ID).getAddedProducts();
        total = 0;
        wattage = 0;
        for (int prod: buildProducts){
            addProduct(prod);
            GeneralProduct product = new GeneralProduct(context, prod);
            total += (product.getPrice() < 0) ? 0 : product.getPrice();
            wattage += Math.max(product.getTdp(), 0);
        }
        String compats = checkCompat();
        if (compats.equals("")){
            TextView compatButton = findViewById(R.id.compatibility);
            compatButton.setOnClickListener(null);
            compatButton.setBackgroundColor(Color.TRANSPARENT);
            compatible = "No Issues";
        }else {
            TextView compatButton = findViewById(R.id.compatibility);
            compatButton.setBackground(ContextCompat.getDrawable(context, R.drawable.build_part_button_green));
            compatButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    // inflate the layout of the popup window
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.compat_issue_layout, null);
                    // create the popup window
                    int width = LinearLayout.LayoutParams.MATCH_PARENT;
                    int height = LinearLayout.LayoutParams.MATCH_PARENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                    popupWindow.setAnimationStyle(R.style.popup_animation);

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                    // Set Text
                    TextView text = popupView.findViewById(R.id.compat_text);
                    text.setText(compats);

                    // Close Button
                    ImageButton closeButton = popupView.findViewById(R.id.close_button);
                    closeButton.setOnClickListener(v2 -> popupWindow.dismiss());

                    // dismiss the popup window when touched
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });
                }
            });
            compatible = "Yes issues";
        }

        updateFooterInfo();
    }

    public void updateFooterInfo(){
        TextView priceView = findViewById(R.id.price);
        TextView wattageView = findViewById(R.id.wattage);
        TextView compatView = findViewById(R.id.compatibility);
        priceView.setText(String.format("%s %.2f", prefs.getCurrencySymbol(), total));
        wattageView.setText(String.format("%d watts", wattage));
        compatView.setText(compatible);
    }

    public String checkCompat(){
        StringBuilder results = new StringBuilder("");
        saveBuilds build = new saveBuilds(context, BUILD_ID);
        ArrayList<Integer> products = build.getAddedProducts();
        // defining these so I Dont have to later
        String sql = "";
        JSONArray buff;
        JSONObject obj;
        for (int prodID: products){
            String productType = build.getProductType(prodID);
            // Checking CPU compatibility
            if (productType.equals("CPU")){
                sql = String.format("SELECT `Socket` FROM CPU WHERE ProductID = %d", prodID);
                buff = new database(context).getData(sql);
                obj = (JSONObject) buff.get(0);
                String cpuSocket = (String) obj.get("Socket");
                for (int prodIDComp: products) {
                    if (build.getProductType(prodIDComp).equals("Motherboard")) {
                        try {
                            sql = String.format("SELECT `Socket / CPU` FROM Motherboard WHERE ProductID = %d", prodIDComp);
                            buff = new database(context).getData(sql);
                            obj = (JSONObject) buff.get(0);
                            String moboSocket = (String) obj.get("Socket / CPU");
                            if (!cpuSocket.equals(moboSocket)) {
                                results.append("Motherboard and CPU may not Be Compatible \n");
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            if (productType.equals("CPU_Cooler")){
                sql = String.format("SELECT `CPU Socket` FROM CPU_Cooler WHERE ProductID = %d", prodID);
                buff = new database(context).getData(sql);
                obj = (JSONObject) buff.get(0);
                String cpuCoolerSocket = (String) obj.get("CPU Socket");
                for (int prodIDComp: products){
                    if (build.getProductType(prodIDComp).equals("Motherboard")){
                        try{
                            sql = String.format("SELECT `Socket / CPU` " +
                                    "FROM Motherboard WHERE ProductID = %d", prodIDComp);
                            buff = new database(context).getData(sql);
                            obj = (JSONObject) buff.get(0);
                            String moboSocket = (String) obj.get("Socket / CPU");
                            if (!Arrays.asList(cpuCoolerSocket.split(";")).contains(moboSocket)){
                                results.append("Motherboard and CPU Cooler may not Be Compatible\n");
                            }
                        } catch (Exception e) {
                            results.append("Motherboard and CPU Cooler may not Be Compatible\n");
                        }
                    }
                    if (build.getProductType(prodIDComp).equals("CPU")){
                        try{
                            sql = String.format("SELECT `Socket` " +
                                    "FROM  CPU WHERE ProductID = %d", prodIDComp);
                            buff = new database(context).getData(sql);
                            obj = (JSONObject) buff.get(0);
                            String cpuSocket = (String) obj.get("Socket");
                            if (!Arrays.asList(cpuCoolerSocket.split(";")).contains(cpuSocket)){
                                results.append("CPU and CPU Cooler may not Be Compatible\n");
                            }
                        } catch (Exception e) {
                            results.append("CPU and CPU Cooler may not Be Compatible\n");
                        }
                    }
                }
            }
            if (productType.equals("Motherboard")){
                sql = String.format("SELECT CASE " +
                        "WHEN `Memory Type` IS NOT NULL " +
                        "THEN `Memory Type` " +
                        "ELSE '' END AS `Memory Type`, " +
                        "CASE WHEN `Memory Max` IS NOT NULL " +
                        "THEN CAST(`Memory Max` AS INT) " +
                        "ELSE 0 " +
                        "END AS `Memory Max`, " +
                        "CASE " +
                        "WHEN `Form Factor` IS NOT NULL " +
                        "THEN `Form Factor` " +
                        "ELSE '' " +
                        "END `Form Factor` " +
                        "FROM Motherboard WHERE ProductID = %d", prodID);
                buff = new database(context).getData(sql);
                obj = (JSONObject) buff.get(0);
                String memoryType = (String) obj.get("Memory Type");
                String buffMem = (String) obj.get("Memory Max");
                String moboFormFactor = (String) obj.get("Form Factor");
                int maxMemory = Integer.valueOf(buffMem);
                for (int prodIDComp: products) {
                    if (build.getProductType(prodIDComp).equals("Memory")) {
                        try {
                            sql = String.format("SELECT CASE " +
                                    "WHEN `Type` IS NOT NULL " +
                                    "THEN `Type` " +
                                    "ELSE '' " +
                                    "END AS `Type`, " +
                                    "CASE WHEN `Modules` IS NOT NULL " +
                                    "THEN REPLACE(`Modules`, 'GB', '') " +
                                    "ELSE '0x0' " +
                                    "END AS Modules " +
                                    "FROM Memory WHERE ProductID = %d", prodIDComp);
                            buff = new database(context).getData(sql);
                            obj = (JSONObject) buff.get(0);
                            String memType = (String) obj.get("Type");
                            String modules = (String) obj.get("Modules");
                            String[] splitBuff = modules.split("x");

                            int memory = Integer.valueOf(splitBuff[0].trim()) * Integer.valueOf(splitBuff[1].trim());

                            System.out.println(memory + "dfgsdfgsdfgsdfg" + maxMemory);
                            if (memory > maxMemory){
                                results.append("Motherboard may not support that much memory\n");
                            }
                            if (!memoryType.equals(memType)){
                                results.append("Memory Type may not be Compatible\n");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (build.getProductType(prodIDComp).equals("Cases")){
                        try{
                            sql = String.format("SELECT CASE " +
                                    "WHEN `Motherboard Form Factor` IS NOT NULL " +
                                    "THEN `Motherboard Form Factor` " +
                                    "ELSE '' " +
                                    "END `Motherboard Form Factor FROM Cases WHERE ProductID = %d", prodIDComp);
                            buff = new database(context).getData(sql);
                            obj  = (JSONObject) buff.get(0);
                            String caseFormFactors = (String) obj.get("Motherboard Form Factor");
                            if (Arrays.asList(caseFormFactors.split(";")).contains(moboFormFactor)){
                                results.append("Motherboard may not fit case");
                            }
                        }catch (Exception e){
                        }
                    }
                }
            }
            if (productType.equals("PSU")){
                sql = String.format("SELECT CASE " +
                        "WHEN `Wattage` IS NOT NULL " +
                        "THEN CAST(`Wattage` AS INT) " +
                        "ELSE 0 " +
                        "END AS `Wattage`" +
                        " FROM PSU WHERE ProductID = %d", prodID);
                buff = new database(context).getData(sql);
                obj = (JSONObject) buff.get(0);
                String wattageBuff = (String) obj.get("Wattage");
                System.out.println( wattage + "##############" + wattageBuff);
                int psuWatts = Integer.valueOf(wattageBuff);
                if ((psuWatts * 1.1) < wattage){
                    System.out.println("$jkofdsgkjsdhf ugyisdhfg");
                    results.append("Power Supply may need higher wattage\n");

                }
            }
        }
        return results.toString();
    }

    public void goToSettings(){
        Intent intent = new Intent("com.iphonik.chameleon.Settings");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToCpuSearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.cpuSearch");

        // Compat Filter
        ArrayList<Integer> buildProducts = new saveBuilds(context, BUILD_ID).getAddedProducts();
        String sqlFilter = "WHERE ";
        for (int prodID: buildProducts){
            String type = new saveBuilds(context, BUILD_ID).getProductType(prodID);
            if (type.equals("Motherboard")){
                String sql = String.format("SELECT `Socket / CPU` FROM Motherboard WHERE ProductID = %d", prodID);
                JSONArray buff = new database(context).getData(sql);
                try{
                    JSONObject obj = (JSONObject) buff.get(0);
                    String socket = (String) obj.get("Socket / CPU");
                    sqlFilter = String.format("%s CPU.Socket = '%s' AND", sqlFilter, socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Passing variables
        intent.putExtra("sqlFilter", sqlFilter);
        intent.putExtra("buildID", BUILD_ID);
        // Start Activity
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToCpuCoolerSearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.cpuCoolerSearch");

        // Compat Filter
        ArrayList<Integer> buildProducts = new saveBuilds(context, BUILD_ID).getAddedProducts();
        String sqlFilter = "WHERE ";
        for (int prodID: buildProducts){
            String type = new saveBuilds(context, BUILD_ID).getProductType(prodID);
            if (type.equals("Motherboard")){
                String sql = String.format("SELECT `Socket / CPU` FROM Motherboard WHERE ProductID = %d", prodID);
                JSONArray buff = new database(context).getData(sql);
                try{
                    JSONObject obj = (JSONObject) buff.get(0);
                    String socket = (String) obj.get("Socket / CPU");
                    sqlFilter += " CPU_Cooler.`CPU Socket` LIKE '%" +  socket + ";%' AND ";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (type.equals("CPU")){
                String sql = String.format("SELECT `Socket` FROM CPU WHERE ProductID = %d", prodID);
                JSONArray buff = new database(context).getData(sql);
                try{
                    JSONObject obj = (JSONObject) buff.get(0);
                    String socket = (String) obj.get("Socket");
                    sqlFilter += " CPU_Cooler.`CPU Socket` LIKE '%" + socket + ";%' AND ";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(sqlFilter);
        // Pass Vari
        intent.putExtra("sqlFilter", sqlFilter);
        intent.putExtra("buildID", BUILD_ID);
        // start activity
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToMotherboardSearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.motherboardSearch");

        // Compat Filter
        ArrayList<Integer> buildProducts = new saveBuilds(context, BUILD_ID).getAddedProducts();
        String sqlFilter = "WHERE ";
        for (int prodID: buildProducts){
            String type = new saveBuilds(context, BUILD_ID).getProductType(prodID);
            if (type.equals("CPU")){
                String sql = String.format("SELECT `Socket` FROM CPU WHERE ProductID = %d", prodID);
                JSONArray buff = new database(context).getData(sql);
                try{
                    JSONObject obj = (JSONObject) buff.get(0);
                    String socket = (String) obj.get("Socket");
                    sqlFilter = String.format("%s Motherboard.`Socket / CPU` = '%s' AND", sqlFilter, socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Passing variables
        intent.putExtra("sqlFilter", sqlFilter);
        intent.putExtra("buildID", BUILD_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToMemorySearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.memorySearch");


        // Compat Filter
        ArrayList<Integer> buildProducts = new saveBuilds(context, BUILD_ID).getAddedProducts();
        String sqlFilter = "WHERE ";
        for (int prodID: buildProducts){
            String type = new saveBuilds(context, BUILD_ID).getProductType(prodID);
            if (type.equals("Motherboard")){
                String sql = String.format("SELECT `Memory Type` FROM Motherboard WHERE ProductID = %d", prodID);
                JSONArray buff = new database(context).getData(sql);
                try{
                    JSONObject obj = (JSONObject) buff.get(0);
                    String memType = (String) obj.get("Memory Type");
                    sqlFilter = String.format("%s (Memory.Type = '%s' OR Memory.Type = NULL) AND", sqlFilter, memType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Passing variables
        intent.putExtra("sqlFilter", sqlFilter);
        intent.putExtra("buildID", BUILD_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToStorageSearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.storageSearch");
        intent.putExtra("buildID", BUILD_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToGpuSearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.gpuSearch");
        intent.putExtra("buildID", BUILD_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToPsuSearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.psuSearch");
        String sqlFilter = String.format("WHERE CAST(PSU.Wattage AS INT) > %d AND ", wattage);
        intent.putExtra("buildID", BUILD_ID);
        intent.putExtra("sqlFilter", sqlFilter);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToCaseSearch(View view) {
        Intent intent = new Intent("com.iphonik.chameleon.caseSearch");
        intent.putExtra("buildID", BUILD_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public String randomBuildID(){
        String SALTCHARS = "abcdefghijklmnopqrztuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}


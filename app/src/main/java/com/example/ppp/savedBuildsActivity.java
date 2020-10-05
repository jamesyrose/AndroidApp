package com.example.ppp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import async_tasks.db.updateSQL;
import pcpp_data.products.GeneralProduct;
import pcpp_data.sqllite.database;
import pcpp_data.sqllite.saveBuilds;
import preferences.Preferences;


public class savedBuildsActivity extends AppCompatActivity {
    database db;
    Preferences prefs;
    Context context;
    LinearLayout dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(savedBuildsActivity.this);
        context = savedBuildsActivity.this;
        setContentView(R.layout.scroll_search);
        dialog = findViewById(R.id.searchID);

        // remove footer
        RelativeLayout footer = findViewById(R.id.footer);
        footer.setVisibility(View.GONE);
    }

    @Override
    protected void onResume(){
        dialog.removeAllViews();
        addSavedBuilds();
        super.onResume();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSavedBuilds(){
        // params
        String buildID = "";
        String buildName = "";


        String sql = "SELECT DISTINCT buildID, name FROM `SavedBuild` WHERE saved = 1;";
        JSONArray buff = new database(context).getData(sql);
        for (Object o: buff){
            JSONObject obj = (JSONObject) o;
            buildID = (String) obj.get("buildID");
            buildName = (String) obj.get("name");
            createBuildView(buildID, buildName);
        }
        loadingDone();
    }

    private void createBuildView(String buildID, String buildName){
        // Price and Products
        double totalPrice = 0.0;
        ArrayList<String> productStrings = new ArrayList<>();
        String productStringList = "";

        String sql2 = String.format("SELECT ProductID " +
                "FROM `SavedBuild` WHERE buildID = '%s'", buildID);
        JSONArray products = new database(context).getData(sql2);
        for (Object p: products){
            JSONObject product = (JSONObject) p;
            int productID = Integer.valueOf((String) product.get("ProductID"));
            GeneralProduct prod = new GeneralProduct(context, productID);
            totalPrice += (prod.getPrice() < 0) ? 0 : prod.getPrice();
            productStrings.add(String.format("(%s) %s", prod.getProductType(), prod.getProductName()));
        }
        Collections.sort(productStrings);
        for (String prod: productStrings){
            productStringList  += String.format("%s \n", prod);
        }
        final String BUILD_ID = buildID;

        // Layout Template
        View newView = LayoutInflater.from(context).inflate(R.layout.saved_builds_selection,
                dialog,
                false);

        TextView name = newView.findViewById(R.id.build_name);
        name.setText(buildName);
        TextView textPrice = newView.findViewById(R.id.totalPrice);
        textPrice.setText(String.format("%s %.2f", prefs.getCurrencySymbol(), totalPrice));
        TextView textProduct = newView.findViewById(R.id.components);
        textProduct.setText(productStringList);

        // Buttons
        Button deleteButton = newView.findViewById(R.id.delete_button);
        Button editButton = newView.findViewById(R.id.edit_button);

        deleteButton.setOnClickListener(v -> {
            // inflate
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View delWindow = inflater.inflate(R.layout.you_sure_window, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(delWindow, width, height, focusable);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            Button delBtn =delWindow.findViewById(R.id.delete_button);
            Button saveBtn = delWindow.findViewById(R.id.save_button);
            ImageButton closeBtn = delWindow.findViewById(R.id.close_button);

            delWindow.setOnTouchListener((v1, event) -> {
                popupWindow.dismiss();
                return true;
            });

            delBtn.setOnClickListener(v2 -> {
                new saveBuilds(context, BUILD_ID).deleteBuild();
                dialog.removeView(newView);
                Toast.makeText(getApplicationContext(), "Build Has Been Deleted", Toast.LENGTH_LONG).show();
                popupWindow.dismiss();
            });

            saveBtn.setOnClickListener(v2 ->{
                popupWindow.dismiss();
            });

            closeBtn.setOnClickListener(v2 ->{
                popupWindow.dismiss();
            });

        });

        editButton.setOnClickListener(v -> {goToBuildPc(v, BUILD_ID);});
        dialog.addView(newView);
    }

    public void goToBuildPc(View view, String buildID){
        Intent intent = new Intent("com.iphonik.chameleon.buildPc");
        intent.putExtra("BUILD_ID", buildID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToSettings() {
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

}
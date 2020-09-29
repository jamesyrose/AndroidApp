package com.example.ppp;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import pcpp_data.queries.CpuSearch;
import pcpp_data.queries.GetSearchLists;
import preferences.Preferences;

public class cpuSearch extends AppCompatActivity {
    private static ArrayList<CpuSearch> searchData;
    Preferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpu_search);
        loadingNotDone();
        if (searchData == null){
            searchData = new ArrayList<>();
        }
        prefs = new Preferences(cpuSearch.this);
        RetrieveCpuData();

        // Set filter
        Button filter = findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                filterPopup(v);
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

    public void filterPopup(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.cpu_filter_window, null);

        LinearLayout mainLayout  = popupView.findViewById(R.id.main_vert_layout);
        // Set the Branch Choices
        LinearLayout brandChoice = mainLayout.findViewById(R.id.brand_selection);
        final LinearLayout brandOptions = mainLayout.findViewById(R.id.brand_options);
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
        priceBar.setRangeValues(0, getHighestPriceProdcuct() + 10);
        LinearLayout priceChoice = popupView.findViewById(R.id.price_selection);
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
        LinearLayout coresChoice = popupView.findViewById(R.id.cores_selection);
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
        final LinearLayout clockChoice = popupView.findViewById(R.id.clock_selection);
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

        // Set Core Choices
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


        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.product_description_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public int getHighestPriceProdcuct(){
        double maxPrice = 0.0;
        for (CpuSearch prod: searchData){
            double bestPrice = prod.getBestPrice();
            if (bestPrice > maxPrice)
                maxPrice = bestPrice;
        }
        return (int) Math.floor(maxPrice);
    }

    public void RetrieveCpuData(){
        if (searchData.isEmpty()){
            new RetrieveFeedTask().execute();
        }else {
            for (CpuSearch product:  searchData){
                addProduct(product);
                LinearLayout dialog   = (LinearLayout)findViewById(R.id.searchID);
                dialog.setVisibility(LinearLayout.VISIBLE);
                Animation animation   =    AnimationUtils.loadAnimation(cpuSearch.this, R.anim.decompress);
                animation.setDuration(1000);
                dialog.setAnimation(animation);
                dialog.animate();
                loadingDone();
            }
        }

    }

    public void addProduct(CpuSearch data){


        // Parent vertical layout
        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.searchID);

        // Inflator
        View productLayout = LayoutInflater.from(this).inflate(R.layout.cpu_selection_template,
                parentLayout,
                false);

        // Labels
        TextView productName = productLayout.findViewById(R.id.product_name_label);
        productName.setPaintFlags(productName.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        productName.setText(data.getProductName());
        RatingBar ratingBar = productLayout.findViewById(R.id.rating_bar);
        double rating = data.getRatingAverage();
        double averageRating = (rating > 0) ? rating : 0; // Accounts for -1 (set b/c null)
        ratingBar.setRating((float) averageRating);
        TextView ratingCount = productLayout.findViewById(R.id.rating_count);
        int count = data.getRatingCount();
        int cnt = (count >= 0) ? count : 0;
        ratingCount.setText(String.format("(%d)", cnt));
        TextView currencySymbol =  productLayout.findViewById(R.id.currency_symbol);
        currencySymbol.setText(prefs.getCurrencySymbol());
        TextView price = productLayout.findViewById(R.id.price_value);
        double buff = data.getBestPrice();
        double bestPrice = (buff > 0) ? buff : 0.0;
        price.setText(String.format("%.2f", bestPrice));

        TextView socket = productLayout.findViewById(R.id.socket_value);
        socket.setText(data.getSocketType()); // Keep only the first part
        TextView tdp = productLayout.findViewById(R.id.tdp_value);
        tdp.setText(data.getTdp());
        TextView cores = productLayout.findViewById(R.id.core_value);
        cores.setText(data.getCores());
        TextView clock = productLayout.findViewById(R.id.clock_value);
        clock.setText(data.getBaseClock().replace(" GHz", "")
                + "/" + data.getBoostClock());


        // Image
        ImageView img = productLayout.findViewById(R.id.product_image);
        new DownLoadImageTask(img).execute(data.getDisplayImg());
        // Setting the horizontal layout to be clickable to include clickability on both image and text
        productLayout.setClickable(true);
        productLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(v);
            }
        });

        parentLayout.addView(productLayout);

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

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;
        ImageButton btn;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
            this.btn = null;
        }

        public DownLoadImageTask(ImageButton imageView){
            this.btn = imageView;
            this.imageView = null;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            if (btn == null) {
                imageView.setImageBitmap(result);
            }else if (imageView == null){
                btn.setImageBitmap(result);
            }
        }
    }


    private class RetrieveFeedTask extends AsyncTask<String, Void, ArrayList<CpuSearch>>{
        @Override
        protected ArrayList<CpuSearch> doInBackground(String... strings) {

            try {
                GetSearchLists obj = new GetSearchLists();
                obj.getCPUsearchList();
                ArrayList<CpuSearch> data = obj.getCPUsearchList();
                return data;
            } catch (Exception e){
                System.out.println("failed to load");
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<CpuSearch> data){
            System.out.println("Query Complete");
            LinearLayout dialog   = (LinearLayout)findViewById(R.id.searchID);
            dialog.setVisibility(LinearLayout.VISIBLE);
            Animation animation   =    AnimationUtils.loadAnimation(cpuSearch.this, R.anim.decompress);
            animation.setDuration(1000);
            dialog.setAnimation(animation);
            for (CpuSearch buff: data){
                addProduct(buff);
            }
            searchData = data;
            dialog.animate();
            System.out.println("Loading Complete");
            loadingDone();
        }
    }

    private void loadingDone(){
        findViewById(R.id.loading_wheel).setVisibility(View.GONE);
        findViewById(R.id.loading_text).setVisibility(View.GONE);
    }

    private void loadingNotDone(){
        findViewById(R.id.loading_wheel).setVisibility(View.VISIBLE);
        findViewById(R.id.loading_text).setVisibility(View.VISIBLE);
    }


}


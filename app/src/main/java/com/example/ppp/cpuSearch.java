package com.example.ppp;

import android.os.AsyncTask;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import pcpp_data.queries.GetSearchLists;
import pcpp_data.queries.MainSearch;

public class cpuSearch extends AppCompatActivity {
    private ArrayList<MainSearch> searchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpu_search);

        new RetrieveFeedTask().execute();
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

    public void animateProductLayout(){
        // Decompress products in scroll window
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.searchID);
        dialog.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(cpuSearch.this, R.anim.decompress);
        animation.setDuration(1000);
        dialog.setAnimation(animation);
        dialog.animate();
    }

    public void addProduct(MainSearch data){
        // Parent vertical layout
        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.searchID);

        // Inflator
        View productLayout = LayoutInflater.from(this).inflate(R.layout.cpu_selection_template,
                parentLayout,
                false);

        // Image
        ImageView img = productLayout.findViewById(R.id.product_image);
        new DownLoadImageTask(img).execute(data.getDisplayImg());
        // Setting the horizontal layout to be clickable to include clickability on both image and text
        productLayout.setClickable(true);
        productLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToSettings(v);
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


    private class RetrieveFeedTask extends AsyncTask<String, Void, ArrayList<MainSearch>>{
        @Override
        protected ArrayList<MainSearch> doInBackground(String... strings) {

            try {
                GetSearchLists obj = new GetSearchLists();
                obj.getCPUsearchList();
                ArrayList<MainSearch> data = obj.getSearchOptions();
                return data;
            } catch (Exception e){
                System.out.println("failed to load");
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MainSearch> data){
            searchData = data;
            for (MainSearch buff: data){
                addProduct(buff);
            }
            animateProductLayout();
            findViewById(R.id.loading_wheel).setVisibility(View.GONE);
            findViewById(R.id.loading_text).setVisibility(View.GONE);
        }
    }
}


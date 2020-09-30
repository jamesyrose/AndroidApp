package async_tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ppp.R;
import com.example.ppp.cpuSearch;

import java.util.ArrayList;

import pcpp_data.queries.CpuSearch;
import pcpp_data.queries.GetSearchLists;
import pcpp_data.queries.SingleCpuQuery;
import preferences.Preferences;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RetrieveCpuFeedTask extends AsyncTask<String, Void, ArrayList<CpuSearch>> {
    Context context;
    LinearLayout dialog;
    static ArrayList<CpuSearch> searchData;
    Preferences prefs;
    View root;
    boolean dataFetched;


    public RetrieveCpuFeedTask(Context context, LinearLayout dialog, Preferences prefs){
        this.context = context;
        this.dialog = dialog;
        this.prefs = prefs;
        this.root = ((Activity) context).getWindow().getDecorView();
        this.dataFetched = false;
        if (this.searchData == null){
            this.searchData = new ArrayList<>();
        }
    }


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
        searchData = data;
        dataFetched = true;
        System.out.println("Query Complete");
        dialog.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(context, R.anim.decompress);
        animation.setDuration(1000);
        dialog.setAnimation(animation);
        for (CpuSearch buff: data){
            addProduct(buff);
        }
        dialog.animate();
        System.out.println("Loading Complete");
        loadingDone();
    }

    public boolean isDataFetched(){
        return dataFetched;
    }

    public void addProduct(final CpuSearch data){
        // Product id
        final int productID = data.getProductID();

        // Parent vertical layout
        LinearLayout parentLayout = dialog;

        // Inflator
        View productLayout = LayoutInflater.from(context).inflate(R.layout.cpu_selection_template,
                parentLayout,
                false);

        // Labels
        final TextView productName = productLayout.findViewById(R.id.product_name_label);
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
        new DownloadImageTask(img).execute(data.getDisplayImg());
        // Setting the horizontal layout to be clickable to include clickability on both image and text
        productLayout.setClickable(true);
        productLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                productPopup(v, productID, data.getProductName());
            }
        });

        parentLayout.addView(productLayout);

    }

    public void productPopup(View view, int productID, String productName){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.cpu_detail_window, null);

        // Set product Name
        TextView name = popupView.findViewById(R.id.product_name);
        name.setText(productName);

        // Initiate Spec object
        SingleCpuQuery query = new SingleCpuQuery(productID);

        //Image Gallery
        LinearLayout gallery = popupView.findViewById(R.id.image_gallery);
        new DownloadImageGallery(context, query, gallery).execute();

        //Spec Values
        final LinearLayout specButton = popupView.findViewById(R.id.specs);
        final LinearLayout specGallery = popupView.findViewById(R.id.spec_values);
        new DownloadCpuSpecs(context, query, specGallery).execute();

        specGallery.setVisibility(View.GONE);
        specButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (specGallery.isShown()){
                    specGallery.setVisibility(View.GONE);
                }else{
                    specGallery.setVisibility(View.VISIBLE);
                }
            }
        });

        // Buy links
        final LinearLayout buyButton = popupView.findViewById(R.id.buy);
        final LinearLayout buyGallery = popupView.findViewById(R.id.buy_options);
        new DownloadSellers(context, query, buyGallery, prefs).execute();

        buyGallery.setVisibility(View.GONE);
        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (buyGallery.isShown()){
                    buyGallery.setVisibility(View.GONE);
                }else{
                    buyGallery.setVisibility(View.VISIBLE);
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

        // close window by button
        ImageButton closeButton = popupView.findViewById(R.id.close_button);
        closeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void loadingDone(){
        root.findViewById(R.id.loading_wheel).setVisibility(View.GONE);
    }

    private void loadingNotDone(){
        root.findViewById(R.id.loading_wheel).setVisibility(View.VISIBLE);
    }

    public ArrayList<CpuSearch> getSearchData(){
        return searchData;
    }

}

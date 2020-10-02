package async_tasks.feeds;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import async_tasks.general.DownloadImageGallery;
import async_tasks.general.DownloadImageTask;
import async_tasks.general.DownloadSellers;
import async_tasks.general.DownloadSpecs;
import pcpp_data.constants.Constants;
import pcpp_data.queries.GetSearchLists;
import pcpp_data.products.MotherboardProduct;
import pcpp_data.queries.SingleProductQuery;
import preferences.Preferences;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RetrieveMotherboardFeedTask extends AsyncTask<String, Void, ArrayList<MotherboardProduct>> {
    Context context;
    LinearLayout dialog;
    static ArrayList<MotherboardProduct> searchData;
    static ArrayList<View> productLayoutView;
    Preferences prefs;
    View root;
    boolean dataFetched;


    public RetrieveMotherboardFeedTask(Context context, LinearLayout dialog, Preferences prefs){
        this.context = context;
        this.dialog = dialog;
        this.prefs = prefs;
        this.root = ((Activity) context).getWindow().getDecorView();
        this.dataFetched = false;
        if (this.searchData == null){
            this.searchData = new ArrayList<>();
        }
        if (this.productLayoutView == null){
            this.productLayoutView = new ArrayList<>();
        }
    }


    @Override
    protected ArrayList<MotherboardProduct> doInBackground(String... strings) {

        try {
            String sql = strings[0];
            GetSearchLists obj = new GetSearchLists(context);
            ArrayList<MotherboardProduct> data = obj.getMotherboardSearchList(sql);
            return data;
        } catch (Exception e){
            System.out.println("failed to load");
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MotherboardProduct> data){
        searchData = data;
        dataFetched = true;
        System.out.println("Query Complete");
        dialog.setVisibility(View.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(context, R.anim.decompress);
        animation.setDuration(1000);
        dialog.setAnimation(animation);
        int max_initial_load = new Constants().max_initial_load;
        max_initial_load = (data.size() > max_initial_load) ? max_initial_load: data.size();
        for (int i=0; i<max_initial_load; i++){
            addProduct(data.get(i));
        }
        dialog.animate();
        System.out.println("Loading Complete");
        loadingDone();
    }

    public boolean isDataFetched(){
        return dataFetched;
    }

    public void addProduct(final MotherboardProduct data) {
        // Product id
        final int productID = data.getProductID();

        // Parent vertical layout
        LinearLayout parentLayout = dialog;

        // Inflator
        View productLayout = LayoutInflater.from(context).inflate(R.layout.motherboard_selection_template,
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
        TextView tdp = productLayout.findViewById(R.id.form_value);
        tdp.setText(data.getFormFactor());
        TextView cores = productLayout.findViewById(R.id.memory_value);
        cores.setText(data.getMaxMemory());
        TextView clock = productLayout.findViewById(R.id.chipset_value);
        clock.setText(data.getChipSet());


        // Image
        ImageView img = productLayout.findViewById(R.id.product_image);
        // checking if url is null, if not, get the image
        String url = data.getDisplayImg();
        if (url != null){
            try {
                new URL(url); // check the url before opening another thread
                new DownloadImageTask(img).execute(url);
            }catch (MalformedURLException e){}
        }

        // Setting the horizontal layout to be clickable to include clickability on both image and text
        productLayout.setClickable(true);
        productLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                productPopup(v, productID, data.getProductName());
            }
        });

        // Set id
        productLayout.setId(data.getViewID());
        // adding productLayout to an array
        productLayoutView.add(productLayout);

        // Add to layout
        parentLayout.addView(productLayout);

    }

    public void productPopup(View view, int productID, String productName){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.product_detail_window, null);

        // Set product Name
        TextView name = popupView.findViewById(R.id.product_name);
        name.setText(productName);

        // Initiate Spec object
        SingleProductQuery query = new SingleProductQuery(productID, context);

        //Image Gallery
        LinearLayout gallery = popupView.findViewById(R.id.image_gallery);
        new DownloadImageGallery(context, query, gallery).execute();

        //Spec Values
        final LinearLayout specButton = popupView.findViewById(R.id.specs);
        final LinearLayout specGallery = popupView.findViewById(R.id.spec_values);
        new DownloadSpecs(context, query, specGallery).execute("Motherboard");

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

    public ArrayList<MotherboardProduct> getSearchData(){
        return searchData;
    }

    public ArrayList<View> getProductLayoutView(){
        return productLayoutView;
    }

}

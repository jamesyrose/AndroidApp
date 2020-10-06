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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppp.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import async_tasks.general.DownloadImageGallery;
import async_tasks.general.DownloadImageTask;
import async_tasks.general.DownloadSellers;
import async_tasks.general.DownloadSpecs;
import pcpp_data.constants.Constants;
import pcpp_data.products.CaseProduct;
import pcpp_data.queries.GetSearchLists;
import pcpp_data.queries.SingleProductQuery;
import pcpp_data.sqllite.saveBuilds;
import preferences.Preferences;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CaseFeedTask extends AsyncTask<String, Void, ArrayList<CaseProduct>> {
    Context context;
    LinearLayout dialog;
    static ArrayList<CaseProduct> searchData;
    static ArrayList<View> productLayoutView;
    Preferences prefs;
    View root;
    boolean dataFetched;
    String BUILD_ID = "";

    public CaseFeedTask(Context context, LinearLayout dialog, Preferences prefs, String buildID){
        this.context = context;
        this.dialog = dialog;
        this.prefs = prefs;
        this.root = ((Activity) context).getWindow().getDecorView();
        this.BUILD_ID = buildID;
        this.dataFetched = false;
        if (this.searchData == null){
            this.searchData = new ArrayList<>();
        }
        if (this.productLayoutView == null){
            this.productLayoutView = new ArrayList<>();
        }
    }


    @Override
    protected ArrayList<CaseProduct> doInBackground(String... strings) {

        try {
            String sql = strings[0];
            System.out.println(sql);
            GetSearchLists obj = new GetSearchLists(context);
            ArrayList<CaseProduct> data = obj.getCaseSearchList(sql);
            return data;
        } catch (Exception e){
            System.out.println("failed to load");
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<CaseProduct> data){
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

    public void addProduct(final CaseProduct data) {
        System.out.println(data.toString());
        // Product id
        final int productID = data.getProductID();

        // Parent vertical layout
        LinearLayout parentLayout = dialog;

        // Inflator
        View productLayout = LayoutInflater.from(context).inflate(R.layout.case_selection_template,
                parentLayout,
                false);

        // Add button (for creating build)
        if (BUILD_ID != null){ // If there is a build id it wont be blank
            Button addButton = productLayout.findViewById(R.id.add_to_build);
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(v -> {
                new saveBuilds(context, BUILD_ID).addToBuild(productID);
                Toast.makeText(context, String.format("%s Added", data.getProductName()), Toast.LENGTH_LONG).show();
                ((Activity) context).finish();
            });
        }

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

        TextView socket = productLayout.findViewById(R.id.tower_value);
        socket.setText(data.getTower()); // Keep only the first part
        TextView tdp = productLayout.findViewById(R.id.shroud_value);
        tdp.setText(data.getPsuShroud());
        TextView cores = productLayout.findViewById(R.id.side_panel_value);
        cores.setText(data.getSidePanel());
        TextView clock = productLayout.findViewById(R.id.max_gpu_value);
        clock.setText(data.getGpuLength() + " mm") ;

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
                new ProductPopup().productPopup(context, prefs, v, productID, data.getProductName(), "Cases");
            }
        });

        // Set id
        productLayout.setId(data.getViewID());
        // adding productLayout to an array
        productLayoutView.add(productLayout);

        // Add to layout
        parentLayout.addView(productLayout);

    }


    private void loadingDone(){
        root.findViewById(R.id.loading_wheel).setVisibility(View.GONE);
    }

    private void loadingNotDone(){
        root.findViewById(R.id.loading_wheel).setVisibility(View.VISIBLE);
    }

    public ArrayList<CaseProduct> getSearchData(){
        return searchData;
    }

    public ArrayList<View> getProductLayoutView(){
        return productLayoutView;
    }

}

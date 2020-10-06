package async_tasks.feeds;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ppp.R;

import async_tasks.general.DownloadImageGallery;
import async_tasks.general.DownloadSellers;
import async_tasks.general.DownloadSpecs;
import pcpp_data.queries.SingleProductQuery;
import preferences.Preferences;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProductPopup {

    public void productPopup(Context context, Preferences prefs, View view, int productID, String productName, String prodType){
        if (prodType.toLowerCase().replace(" ", "")
                .replace("_", "")
                .equals("powersupply")){
            prodType = "PSU";
        }

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
        new DownloadSpecs(context, query, specGallery).execute(prodType);

        // Buy links
        final LinearLayout buyButton = popupView.findViewById(R.id.buy);
        final LinearLayout buyGallery = popupView.findViewById(R.id.buy_options);
        new DownloadSellers(context, query, buyGallery, prefs).execute();

        // Set buy and spec button actions
        specButton.setBackgroundColor(Color.parseColor("#191b2b"));
        specButton.setOnClickListener(v -> {
            if (!specGallery.isShown()){
                specGallery.setVisibility(View.VISIBLE);
                buyGallery.setVisibility(View.GONE);
                specButton.setBackgroundColor(Color.parseColor("#191b2b"));
                buyButton.setBackgroundColor(Color.parseColor("#B34C4D7E"));
            }
        });


        buyGallery.setVisibility(View.GONE);
        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!buyGallery.isShown()){
                    buyGallery.setVisibility(View.VISIBLE);
                    specGallery.setVisibility(View.GONE);
                    buyButton.setBackgroundColor(Color.parseColor("#191b2b"));
                    specButton.setBackgroundColor(Color.parseColor("#B34C4D7E"));
                }
            }
        });


        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.popup_animation);

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

}

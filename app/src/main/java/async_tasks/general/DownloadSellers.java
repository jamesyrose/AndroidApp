package async_tasks.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ppp.R;

import java.util.ArrayList;

import pcpp_data.products.PriceObj;
import pcpp_data.queries.SingleProductQuery;
import preferences.Preferences;

public class DownloadSellers extends AsyncTask<String, Void, ArrayList<PriceObj>> {
    SingleProductQuery query;
    LinearLayout gallery;
    Preferences prefs;
    Context context;

    public DownloadSellers(Context context, SingleProductQuery query, LinearLayout gallery, Preferences prefs){
        this.context = context;
        this.query = query;
        this.gallery = gallery;
        this.prefs = prefs;
    }

    @Override
    protected ArrayList<PriceObj> doInBackground(String... strings) {
        ArrayList<PriceObj> sellers = query.getPrice();
        return sellers;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onPostExecute(ArrayList<PriceObj> sellers){
        for (PriceObj price: sellers){
            if (price != null){
                View layout = LayoutInflater.from(context).inflate(R.layout.buy_option_template,
                        gallery,
                        false);
                TextView available_value = layout.findViewById(R.id.available_value);
                TextView shipping_value = layout.findViewById(R.id.shipping_value);
                TextView price_value = layout.findViewById(R.id.price_value);
                @SuppressLint("DefaultLocale") String sellingPrice = String.format("%s %.2f" ,
                        prefs.getCurrencySymbol(),
                        price.getBasePrice());
                double shipPrice = price.getShipping();
                String shippingPrice = "Unknown";
                if  (shipPrice != -2){
                    if (shipPrice == -1){
                        shippingPrice = "Amazon Price";
                    } else if (shipPrice == 0 ) {
                        shippingPrice = "Free Shipping";
                    } else{
                        shippingPrice = String.format("%s %.2f",
                                prefs.getCurrencySymbol(),
                                shipPrice);
                    }
                }

                String avail = (price.isAvail()) ? "Yes" : "No";
                String merchant = price.getMerchant();
                final String purchaseLink = price.getPurchaseLink();

                // changing values
                price_value.setText(sellingPrice);
                available_value.setText(avail);
                shipping_value.setText(shippingPrice);

                // clickable value for url
                LinearLayout buyBtn =  layout.findViewById(R.id.seller_button);
                buyBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Uri uriUrl = Uri.parse(purchaseLink);
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        context.startActivity(launchBrowser);
                    }
                });
                ImageButton btn =  layout.findViewById(R.id.realbutton);
                String merch = merchant.toLowerCase().replace(" ", "").replace("&", "and") + "_logo";
                int resource = getDrawableIdentifier(context, merch);
                btn.setImageResource(resource);
                btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Uri uriUrl = Uri.parse(purchaseLink);
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        context.startActivity(launchBrowser);
                    }
                });

                gallery.addView(layout);
            }
        }
    }


    public static int getDrawableIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

}
package async_tasks.feeds;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ppp.R;

import java.util.ArrayList;
import java.util.HashMap;

import pcpp_data.queries.SingleProductQuery;


public class DownloadSpecs extends AsyncTask<String, Void, HashMap<String, String>> {
    SingleProductQuery query;
    LinearLayout gallery;
    ArrayList<String> order;
    Context context;

    public DownloadSpecs(Context context, SingleProductQuery query, LinearLayout gallery){
        this.query = query;
        this.gallery = gallery;
        this.context = context;

    }

    @Override
    protected HashMap<String, String> doInBackground(String...strings) {
        String table = strings[0];
        HashMap<String, String> specs = query.getSpecs(table);
        ArrayList<String> order = query.getSpecOrder(table);
        this.order = order;
        return specs;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> specs){
        for (String key: order){
            String value = specs.get(key);
            if (value != null){
                View layout = LayoutInflater.from(context).inflate(R.layout.spec_template,
                        gallery,
                        false);
                TextView spec_key = layout.findViewById(R.id.spec_key);
                TextView spec_value = layout.findViewById(R.id.spec_value);
                spec_key.setPaintFlags(spec_key.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                spec_key.setText(key + ":");
                spec_value.setText(value.replace(";", "\n"));
                gallery.addView(layout);
            }
        }
    }
}
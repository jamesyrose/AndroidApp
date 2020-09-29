package async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ppp.R;
import com.example.ppp.cpuSearch;

import java.util.ArrayList;
import java.util.HashMap;

import pcpp_data.queries.SingleCpuQuery;


public class DownloadCpuSpecs extends AsyncTask<String, Void, HashMap<String, String>> {
    SingleCpuQuery query;
    LinearLayout gallery;
    ArrayList<String> order;
    Context context;

    public DownloadCpuSpecs(Context context, SingleCpuQuery query, LinearLayout gallery){
        this.query = query;
        this.gallery = gallery;
        this.context = context;
    }

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        HashMap<String, String> specs = query.getSpecs();
        ArrayList<String> order = query.getSpecOrder();
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
                spec_key.setText(key);
                spec_value.setText(value.replace(";", "\n"));
                gallery.addView(layout);
            }
        }
    }
}
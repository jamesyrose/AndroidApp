package async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ppp.R;
import com.example.ppp.cpuSearch;

import java.util.ArrayList;

import pcpp_data.queries.SingleCpuQuery;

public class DownloadImageGallery extends AsyncTask<String, Void, ArrayList<String>> {
    SingleCpuQuery query;
    LinearLayout gallery;
    Context context;

    public DownloadImageGallery(Context context, SingleCpuQuery query, LinearLayout gallery){
        this.query = query;
        this.gallery = gallery;
        this.context = context;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        ArrayList<String> images = query.getImageGallery();
        return images;
    }

    @Override
    protected void onPostExecute(ArrayList<String> images){
        for (String url: images){
            View layout = LayoutInflater.from(context).inflate(R.layout.image_gallery_template,
                    gallery,
                    false);
            ImageView img = layout.findViewById(R.id.image);
            new DownloadImageTask(img).execute(url);
            gallery.addView(layout);
        }
    }
}

package async_tasks.general;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String,Void, Bitmap> {
    ImageView imageView;
    ImageButton btn;

    public DownloadImageTask(ImageView imageView){
        this.imageView = imageView;
        this.btn = null;
    }

    public DownloadImageTask(ImageButton imageView){
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

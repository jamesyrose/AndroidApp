package async_tasks.db;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ppp.R;

import org.json.simple.JSONArray;

import java.util.List;

import pcpp_data.sqllite.database;
import preferences.Preferences;

public class checkDataBaseStruct extends AsyncTask<String, Integer, String> {
    Context  context;
    RelativeLayout loadingWheel;
    Preferences prefs;

    public checkDataBaseStruct(Context context, RelativeLayout loading, Preferences prefs){
        this.context = context;
        this.loadingWheel = loading;
        this.prefs = prefs;
    }

    @Override
    protected  void onPreExecute(){
        loadingNotDone();
    }

    @Override
    protected String doInBackground(String... strings) {
        List<String> tables = List.of("BuildGuide", "ProductMain","Images","Rating","Price",
                "CPU", "CPU_Cooler", "Memory", "Motherboard",  "PSU", "Cases", "ExchangeRates",
                "GPU", "Storage");
        double count = 0;
        while (true){
            count = 0;
            for (String table : tables){
                if (!prefs.dbNeedUpdate(table)){
                    count += 7.25;
                }
            }
            try{
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress((int) Math.floor(count));
            if (count > 100){
                break;
            }
        }
        return "";
    };

    @Override
    protected void onProgressUpdate(Integer... vals){
        int count = vals[0];
        ProgressBar prog = loadingWheel.findViewById(R.id.progressBar);
        TextView progText = loadingWheel.findViewById(R.id.progress_percent);
        prog.setProgress(count);
        progText.setText(count + "%");
    }


    @Override
    protected void onPostExecute(String res){
        loadingDone();
    }


    private void loadingDone(){
        loadingWheel.setVisibility(View.GONE);
    }

    private void loadingNotDone(){
        loadingWheel.setVisibility(View.VISIBLE);
    }
}

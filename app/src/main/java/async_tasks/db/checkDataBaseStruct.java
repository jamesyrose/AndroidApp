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

public class checkDataBaseStruct extends AsyncTask<String, Integer, String> {
    Context  context;
    RelativeLayout loadingWheel;

    public checkDataBaseStruct(Context context, RelativeLayout loading){
        this.context = context;
        this.loadingWheel = loading;
    }

    @Override
    protected  void onPreExecute(){
        loadingNotDone();
    }

    @Override
    protected String doInBackground(String... strings) {
        List<String> tables = List.of("ProductMain",
                "Images","Rating","Price", "CPU", "CPU_Cooler",
                "Memory", "Motherboard",  "PSU", "Cases", "ExchangeRates", "GPU",
                "Storage");
        int count = 0;
        while (true){
            for (String table : tables){
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name not" +
                        " IN ('android_metadata', 'sqlite_sequence', 'SavedBuild'); ";
                try{
                    JSONArray data = new database(context).getData(sql);
                    count = data.size() * 8;
                    System.out.println("######################################" + count);
                    tables.remove(table);
                } catch (Exception e) {
                    // Passes if the table does not extist
                }
            }
            try{
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(count);
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

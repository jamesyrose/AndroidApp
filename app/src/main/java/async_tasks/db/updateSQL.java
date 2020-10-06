package async_tasks.db;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppp.R;

import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;

import pcpp_data.conn.Conn;
import pcpp_data.sqllite.database;
import preferences.Preferences;

public class updateSQL  extends AsyncTask<String, Integer, String>{
    String updateUrl;
    Context  context;
    Preferences prefs;
    LinearLayout dialog;



    public updateSQL(Context context) {
        this.updateUrl = "https://pcpp.verlet.io/updateData.php";
        this.context = context;
        this.prefs = new Preferences(context);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... strings) {
        String table = strings[0];
        Conn conn = new Conn();

        String sqlData = "";
        try {
            if (prefs.dbNeedUpdate(table)){
                long start = System.currentTimeMillis();
                String url = String.format("%s?table=%s", this.updateUrl, table);
                System.out.println(url);
                sqlData = conn.getDataAsString(url);
                long end = System.currentTimeMillis();
                System.out.println(String.format("%s String Took %.2f", table, (double)(end - start)  / 1000 ));
                start = System.currentTimeMillis();
                database db = new database(context);
                db.dropTable(table);
                db.buildDatabase(sqlData);
                end = System.currentTimeMillis();
                System.out.println(String.format("%s Rest Took %.2f", table,  (double)(end - start)  / 1000 ));
                prefs.updateDbUpdateData(table);
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sqlData;
    };


    @Override
    protected void onPostExecute(String sql){
    }

}

package async_tasks.db;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import pcpp_data.conn.Conn;
import pcpp_data.sqllite.database;
import preferences.Preferences;

public class updateSqlTask extends AsyncTask<String, Integer, String> {
    String updateUrl;
    Context context;
    Preferences prefs;

    public updateSqlTask(Context context){
        this.updateUrl = "https://pcpp.verlet.io/updateData.php";
        this.context = context;
        this.prefs = new Preferences(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        Conn conn = new Conn();
        String sqlData = "";
        try {
            for (int i=0; i<=12; i++){
                String url = String.format("%s?num=%d", this.updateUrl, i);
                System.out.println(url);
                sqlData = conn.getDataAsString(url);
                System.out.println(url + "   SQL string loaded");
                database db = new database(context);
                db.buildDatabase(sqlData);
            }
            prefs.updateDbUpdateData();
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
    protected void onProgressUpdate(Integer... values) {

    }


    @Override
    protected void onPostExecute(String sql){

    }

}

package async_tasks.db;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import pcpp_data.conn.Conn;
import pcpp_data.sqllite.database;
import preferences.Preferences;

public class updateSQL  extends AsyncTask<String, Integer, String>{
    String updateUrl;
    Context  context;
    Preferences prefs;

    public updateSQL(Context context) {
        this.updateUrl = "https://pcpp.verlet.io/updateData.php";
        this.context = context;
        this.prefs = new Preferences(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        Conn conn = new Conn();
        String [] tables = new String[] {"Cases", "CPU", "CPU_Cooler", "ExchangeRates", "GPU",
                "Images", "Memory", "Motherboard", "Price", "ProductMain", "PSU",
                "Rating", "Storage"};
        String sqlData = "";
        try {
            for (String table: tables){
                String url = String.format("%s?table=%s", this.updateUrl, table);
                System.out.println(url);
                sqlData = conn.getDataAsString(url);
                System.out.println(url + "   SQL string loaded");
                database db = new database(context);
                db.dropTable(table);
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

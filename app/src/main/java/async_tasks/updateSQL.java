package async_tasks;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ppp.R;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pcpp_data.conn.Conn;
import pcpp_data.queries.jsonFileManager;
import pcpp_data.sqllite.database;

public class updateSQL extends AsyncTask<String, Void, String> {
    String updateUrl;
    Context  context;

    public updateSQL(Context context){
        this.updateUrl = "https://pcpp.verlet.io/updateData.php";
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        Conn conn = new Conn();
        String sqlData = "";
        try {
            sqlData = conn.getDataAsString(this.updateUrl);
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
        database db = new database(context);
        db.buildDatabase(sql);
    }

}

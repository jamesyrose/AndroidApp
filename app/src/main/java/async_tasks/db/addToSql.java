package async_tasks.db;

import android.content.Context;
import android.os.AsyncTask;

import pcpp_data.sqllite.database;

public class addToSql extends AsyncTask<String, Void, Void> {
    String sql;
    Context  context;
    database db;

    public addToSql(Context context, database db, String sql){
        this.sql = sql;
        this.db = db;
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        db.buildDatabase(this.sql);
        return null;
    };
}

package pcpp_data.sqllite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import preferences.Preferences;

public class updateDatabase extends SQLiteOpenHelper {
    public Preferences prefs;
    public static final String[] tables = new String[] {"Cases", "CPU", "CPU_Cooler", "ExchangeRates", "GPU",
            "Images", "Memory", "Motherboard", "Price", "ProductMain", "PSU",
            "Rating", "Storage"};
    public static String createSqlString = "CREATE TABLE buffer (ID, INT)";
    public static String deleteSqlString = "";

    public updateDatabase(Context context){
        super(context, new Preferences(context).getUpdateDbName(), null, 1);
        prefs = new Preferences(context);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(createSqlString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void buildDatabase(String sql){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] splitData = sql.split("!@#");
        for (String buff: splitData){
            for (String table: tables){
                // Checks if its creates table so it may delete the old one
                deleteSqlString = String.format("DROP TABLE IF EXISTS `%s`; ", table);
                String checkString = String.format("CREATE TABLE `%s`", table);
                if (buff.contains(checkString)){
                    db.execSQL(deleteSqlString);
                }
            }
//            System.out.println(buff);
            createSqlString = buff;
            onCreate(db);
        }
        db.close();
    }
}

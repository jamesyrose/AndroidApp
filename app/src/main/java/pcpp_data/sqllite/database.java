package pcpp_data.sqllite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pcpp_data.constants.SqlConstants;
import preferences.Preferences;

public class database extends SQLiteOpenHelper {
    public Preferences prefs;
    SQLiteDatabase db;
    public static final String DB_NAME = "productData.db";
    public static final String[] tables = new String[] {"Cases", "CPU", "CPU_Cooler", "ExchangeRates", "GPU",
            "Images", "Memory", "Motherboard", "Price", "ProductMain", "PSU",
            "Rating", "Storage"};
    public static String createSqlString = "CREATE IF NOT EXISTS TABLE buffer (ID, INT)";
    public static String deleteSqlString = "";
    public SqlConstants sqlConstants = new SqlConstants();

    public database(Context context){
        super(context, DB_NAME, null, 1);
        prefs = new Preferences(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // createSavedBuilds();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void dropTable(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = String.format("DROP TABLE IF EXISTS `%s`; ", table);
        db.execSQL(sql);
    }

    public void buildDatabase(String sql){
        SQLiteDatabase db = this.getWritableDatabase();
        db.enableWriteAheadLogging();
        while (db.isDbLockedByCurrentThread()){
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String[] splitData = sql.split("!@#");
        for (String buff: splitData){
            db.execSQL(buff);
        }
        db.close();
    }

    public void showTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        while(cursor.moveToNext()){
            String tableName = cursor.getString(0);
            if(tableName.equals("android_metadata")){
                continue;
            }else{
                System.out.println(tableName);
            }
        }
        db.close();
        System.out.println(":########DONE ");
    }


    public void createSavedBuilds(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlString = sqlConstants.CREATE_SAVED_BUILD_TABLES;
        db.execSQL(sqlString);
    }


    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM  " + "Motherboard; ", null);
        db.close();
        return res;
    }

    public JSONArray getData(String sqlString){
        SQLiteDatabase db  = this.getWritableDatabase();
//        System.out.println(sqlString);
        Cursor res = db.rawQuery(sqlString, null);
        JSONArray resultSet = new JSONArray();
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            int totalColumn = res.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (res.getColumnName(i) != null) {
                    try {
                        rowObject.put(res.getColumnName(i),
                                res.getString(i));
                    } catch (Exception e) {
                        Log.d("Cursor to JSON: ", e.getMessage());
                    }
                }
            }
            resultSet.add(rowObject);
            res.moveToNext();
        }
        db.close();
        res.close();
        return resultSet;
    }

    public void execSQL(String sqlString){
        SQLiteDatabase db  = this.getWritableDatabase();
        db.execSQL(sqlString);
        db.close();
    }
}

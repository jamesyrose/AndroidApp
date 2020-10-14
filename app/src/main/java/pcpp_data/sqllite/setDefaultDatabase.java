package pcpp_data.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ppp.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class setDefaultDatabase extends SQLiteOpenHelper {

    private static final String dbName="productData.db";
    private static final int dbVersion=1;
    private Context context;

    public setDefaultDatabase(Context content){
        super(content, dbName, null, dbVersion);
        this.context = content;
    }
    public boolean copyDbIfNotExists()
    {
        // Ensure /data/data/YOUR_PACKAGE_NAME/databases/ directory is created.
        File dbDir = new File(context.getDatabasePath(dbName).getParentFile().getPath());
        if (!dbDir.exists())
            dbDir.mkdir();

        // Copy database starts here.
        String appDbPath = this.context.getDatabasePath(dbName).getAbsolutePath();
        File dbFile = new File(appDbPath);
        if(!dbFile.exists()){
            try {
                InputStream mInput = context.getAssets().open("productData.db");
                OutputStream mOutput = new FileOutputStream(appDbPath);
                byte[] mBuffer = new byte[1024];
                int mLength;
                while ((mLength = mInput.read(mBuffer, 0, 1024)) > 0)
                    mOutput.write(mBuffer, 0, mLength);
                mOutput.flush();

                mOutput.close();
                mInput.close();
                return true;
            }
            catch (IOException ex){
                throw new Error("Error copying database: "+ex.getMessage());
            }
        }
        return  false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Nothing to do. Use existing database.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade.
    }
}

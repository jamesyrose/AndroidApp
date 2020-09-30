package async_tasks;

import android.os.AsyncTask;

import java.lang.reflect.Array;
import java.util.ArrayList;

import pcpp_data.queries.CpuSearch;

public class WaitForData<T> extends AsyncTask<String, Void, ArrayList<T>> {
    /*
    Waits for src to not be empty and copies it to dest
     */
    ArrayList<T> src;
    ArrayList<T> dest;

    public WaitForData(ArrayList<T> src, ArrayList<T> dest){
        this.src = src;
        this.dest = dest;
    }

    @Override
    protected ArrayList<T> doInBackground(String...  strings) {
        while (src.isEmpty()){
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return src;
    }

    @Override
    protected void onPostExecute(ArrayList<T> data){
        dest = data;
    }
}

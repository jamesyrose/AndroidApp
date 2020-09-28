package pcpp_data.queries;


import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import pcpp_data.conn.Conn;

public class GetSearchLists{
    private Conn conn;
    private ArrayList<MainSearch> searchOptions;

    public GetSearchLists() {
        this.conn = new Conn();
    }


    public void getCPUsearchList() {
        String url = "https://pcpp.verlet.io/CpuSelectSearch.php";
        try {
            searchOptions = new ArrayList<>();
            JSONArray data = conn.getData(url);
            for (Object buff: data) {
                JSONObject row = (JSONObject) buff;
                CpuSearch cpuRow = new CpuSearch(row);
                searchOptions.add(cpuRow);
            }
        } catch (IOException | ParseException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<MainSearch> getSearchOptions(){
        return searchOptions;
    }


}

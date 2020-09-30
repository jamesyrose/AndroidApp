package pcpp_data.queries;


import android.content.Context;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pcpp_data.constants.SqlConstants;
import pcpp_data.sqllite.database;

public class GetSearchLists{
    private Context context;

    public GetSearchLists(Context context) {
        this.context = context;
    }

    public ArrayList<CpuSearch> getCPUsearchList() {
        String sql  = new SqlConstants().cpuSearchList;
        ArrayList<CpuSearch> searchOptions = new ArrayList<CpuSearch>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            CpuSearch cpuRow = new CpuSearch(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }

}

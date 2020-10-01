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
        String sql  = new SqlConstants().CPU_SEARCH_LIST;
        ArrayList<CpuSearch> searchOptions = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            CpuSearch cpuRow = new CpuSearch(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }

    public ArrayList<CpuCoolerSearch> getCpuCoolerSearchList() {
        String sql  = new SqlConstants().CPU_COOLER_SEARCH_LIST;
        ArrayList<CpuCoolerSearch> searchOptions = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            CpuCoolerSearch cpuRow = new CpuCoolerSearch(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }

    public ArrayList<MotherboardSearch> getMotherboardSearchList() {
        String sql  = new SqlConstants().MOTHERBOARD_SEARCH_LIST;
        ArrayList<MotherboardSearch> searchOptions = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            MotherboardSearch cpuRow = new MotherboardSearch(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }


}

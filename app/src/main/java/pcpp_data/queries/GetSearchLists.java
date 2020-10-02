package pcpp_data.queries;


import android.content.Context;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pcpp_data.constants.SqlConstants;
import pcpp_data.products.CpuCoolerProduct;
import pcpp_data.products.CpuSearchProduct;
import pcpp_data.products.MemoryProduct;
import pcpp_data.products.MotherboardProduct;
import pcpp_data.sqllite.database;

public class GetSearchLists{
    private Context context;

    public GetSearchLists(Context context) {
        this.context = context;
    }

    public ArrayList<CpuSearchProduct> getCPUsearchList() {
        String sql  = new SqlConstants().CPU_SEARCH_LIST;
        ArrayList<CpuSearchProduct> searchOptions = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            CpuSearchProduct cpuRow = new CpuSearchProduct(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }

    public ArrayList<CpuCoolerProduct> getCpuCoolerSearchList() {
        String sql  = new SqlConstants().CPU_COOLER_SEARCH_LIST;
        ArrayList<CpuCoolerProduct> searchOptions = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            CpuCoolerProduct cpuRow = new CpuCoolerProduct(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }

    public ArrayList<MotherboardProduct> getMotherboardSearchList() {
        String sql  = new SqlConstants().MOTHERBOARD_SEARCH_LIST;
        ArrayList<MotherboardProduct> searchOptions = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            MotherboardProduct cpuRow = new MotherboardProduct(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }

    public ArrayList<MemoryProduct> getMemorySearchList() {
        String sql  = new SqlConstants().MEMORY_SEARCH_LIST;
        ArrayList<MemoryProduct> searchOptions = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            MemoryProduct cpuRow = new MemoryProduct(row);
            searchOptions.add(cpuRow);
        }
        return searchOptions;
    }
}

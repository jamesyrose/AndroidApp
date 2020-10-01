package pcpp_data.queries;


import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.simple.JSONObject;


// Selection list of data
public class MotherboardSearch extends MainSearch{
    private String manufacturer;
    private String socketType;
    private String formFactor;
    private String maxMemory;
    private String chipSet;

    public MotherboardSearch(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.formFactor = (String) row.get("Form Factor");
        this.socketType = (String) row.get("Socket / CPU");
        this.maxMemory = (String) row.get("Memory Max");
        this.chipSet = (String) row.get("Chipset");
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSocketType(){
        return socketType;
    }

    public String getFormFactor(){
        return formFactor;
    }

    public String getMaxMemory() {
        return maxMemory;
    }

    public String getChipSet(){
        return chipSet;
    }
}

package pcpp_data.queries;


import org.json.simple.JSONObject;


// Selection list of data
public class CpuCoolerSearch extends MainSearch{
    private String manufacturer;
    private String rpm;
    private String noise;
    private String height;
    private String waterCooled;

    public CpuCoolerSearch(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.rpm = (String) row.get("Fan RPM");
        this.noise = (String) row.get("Noise Level");
        this.height = (String) row.get("Height");
        this.waterCooled = (String) row.get("Water Cooled");
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getRpm(){
        return rpm;
    }

    public String getNoise(){
        return noise;
    }

    public String getheight(){
        return height;
    }

    public String getWaterCooled(){
        return waterCooled;
    }
}

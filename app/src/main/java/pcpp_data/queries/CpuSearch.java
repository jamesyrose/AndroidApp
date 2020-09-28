package pcpp_data.queries;


import org.json.simple.JSONObject;


// Selection list of data
public class CpuSearch extends MainSearch{
    private String manufacturer;
    private String socketType;
    private String cores;
    private String baseClock;
    private String BoostClock;
    private String tdp;

    public CpuSearch(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.cores = (String) row.get("Core Count");
        this.baseClock = (String) row.get("Core Clock");
        this.BoostClock = (String) row.get("Boost Clock");
        this.tdp = (String) row.get("TDP");
        this.socketType = (String) row.get("Socket");
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCores() {
        return cores;
    }

    public String getBaseClock() {
        return baseClock;
    }

    public String getBoostClock() {
        return BoostClock;
    }

    public String getTdp() {
        return tdp;
    }

    public String getSocketType() {
        return socketType;
    }
}

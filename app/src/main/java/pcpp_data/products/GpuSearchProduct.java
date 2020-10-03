package pcpp_data.products;


import org.json.simple.JSONObject;


// Selection list of data
public class GpuSearchProduct extends MainProduct {
    private String manufacturer;
    private String coreClock;
    private String memory;
    private String length;
    private String tdp;
    private String chipset;

    public GpuSearchProduct(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.coreClock = (String) row.get("Core Clock");
        this.memory = (String) row.get("Memory");
        this.length = (String) row.get("Length");
        this.tdp = (String) row.get("TDP");
        this.chipset = (String) row.get("Chipset");

    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCoreClock(){
        return coreClock;
    }

    public String getMemory(){
        return memory;
    }

    public String getLength(){
        return length;
    }

    public String getTdp(){
        return tdp;
    }

    public String getChipset(){
        return chipset;
    }
}


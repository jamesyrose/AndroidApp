package pcpp_data.products;


import org.json.simple.JSONObject;


// Selection list of data
public class MemoryProduct extends MainProduct {
    private String manufacturer;
    private String pricePerGB;
    private String modules;
    private String memorySpeed;
    private String ecc;


    public MemoryProduct(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.pricePerGB = (String) row.get("Price / GB");
        this.modules = (String) row.get("Modules");
        this.memorySpeed = (String) row.get("Speed");
        this.ecc = (String) row.get("ECC / Registered");

    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPricePerGB(){
        return pricePerGB;
    }

    public String getModules(){
        return modules;
    }

    public String getMemorySpeed(){
        return memorySpeed;
    }

    public String getEcc(){
        return ecc;
    }

}


package pcpp_data.products;


import org.json.simple.JSONObject;


// Selection list of data
public class PsuProduct extends MainProduct {
    private String manufacturer;
    private String model;
    private String wattage;
    private String modular;
    private String efficiency;
    private String formFactor;

    public PsuProduct(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.model = (String) row.get("Model");
        this.wattage = (String) row.get("Wattage");
        this.modular = (String) row.get("Modular");
        this.efficiency = (String) row.get("Efficiency Rating");
        this.formFactor = (String) row.get("Form Factor");
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel(){
        return model;
    }

    public String getWattage(){
        return wattage;
    }

    public String getModular(){
        return modular;
    }

    public String getEfficiency(){
        return  efficiency;
    }

    public String getFormFactor(){
        return formFactor;
    }


}

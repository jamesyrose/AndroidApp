package pcpp_data.products;


import org.json.simple.JSONObject;


// Selection list of data
public class StorageProduct extends MainProduct {
    private String manufacturer;
    private String capacity;
    private String formFactor;
    private String type;
    private String nvme;

    public StorageProduct(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.formFactor = (String) row.get("Form Factor");
        this.capacity = (String) row.get("Capacity");
        this.type = (String) row.get("Type");
        this.nvme = (String) row.get("NVME");
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getFormFactor(){
        return formFactor;
    }

    public String getCapacity(){
        return capacity;
    }

    public String getType(){
        return type;
    }

    public String getNvme(){
        return nvme;
    }



}

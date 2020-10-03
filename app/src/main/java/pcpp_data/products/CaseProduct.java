package pcpp_data.products;


import org.json.simple.JSONObject;


// Selection list of data
public class CaseProduct extends MainProduct {
    private String manufacturer;
    private String tower;
    private String psuShroud;
    private String sidePanel;
    private int gpuLength;


    public CaseProduct(JSONObject row) {
        super(row);
        getSpecs(row);
    }

    private void getSpecs(JSONObject row) {
        this.setManufacturer((String) row.get("Manufacturer"));
        this.tower = (String) row.get("Type");
        this.psuShroud = (String) row.get("Power Supply Shroud");
        this.sidePanel = (String) row.get("Side Panel Window");
        String buff = (String) row.get("GPU_Length");
        this.gpuLength = 0;
        if (buff != null){
            this.gpuLength = Integer.valueOf(buff);
        }
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTower(){
        return tower;
    }

    public String getPsuShroud(){
        return psuShroud;
    }

    public String getSidePanel(){
        return sidePanel;
    }

    public int getGpuLength(){
        return gpuLength;
    }
}


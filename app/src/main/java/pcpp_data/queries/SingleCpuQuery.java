package pcpp_data.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pcpp_data.conn.Conn;
import pcpp_data.constants.Constants;
import pcpp_data.products.PriceObj;

public class SingleCpuQuery {
    private int productID;
    private String urlBase;
    private Conn conn;

    public SingleCpuQuery(int productID){
        this.productID = productID;
        this.conn = new Conn();
        this.urlBase = "https://pcpp.verlet.io/singleProductSpec.php?prodType=%s&id=%d";
    }

    public ArrayList<String> getImageGallery() {
        ArrayList<String> images = new ArrayList<>();
        String url = String.format(this.urlBase, "Images", this.productID);
        try {
            JSONArray data = conn.getData(url);
            for (Object buff: data) {
                JSONObject row = (JSONObject) buff;
                String image = (String) row.get("Images");
                if (image!= null) {
                    for (String key: Constants.IMAGE_BASE_MAP.keySet()) {
                        image = image.replace(key, Constants.IMAGE_BASE_MAP.get(key));
                    }
                }
                images.add(image);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

    public HashMap<String, String> getSpecs(){
        HashMap <String, String> specs = new HashMap<>();
        String url = String.format(this.urlBase, "CPU", this.productID);
        try {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$4");
            System.out.println(url);
            JSONArray data = conn.getData(url);
            for (Object buff: data) {
                JSONObject row = (JSONObject) buff;
                for(Iterator iterator = row.keySet().iterator(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    if (!key.equals("id") && !key.equals("ProductID")) {
                        specs.put(key,  (String) row.get(key));
                        System.out.println(key+ "  " + (String) row.get(key));
                    }
                }
            }
            return specs;
        }catch (Exception e) {

        }
        return null;
    }

    public ArrayList<String> getSpecOrder() {
        String url = "https://pcpp.verlet.io/describeTable.php?prodType=CPU";
        ArrayList<String> fields = new ArrayList<>();
        try {
            JSONArray data = conn.getData(url);
            for (Object buff: data) {
                JSONObject row = (JSONObject) buff;
                String field = (String) row.get("Field");
                if (!field.equals("id") && !field.equals("ProductID")) {
                    fields.add(field);
                }
            }
        }catch (Exception e) {
            e.getStackTrace();
        }
        return fields;

    }

    public ArrayList<PriceObj> getPrice(){
        ArrayList<PriceObj> priceData = new ArrayList<>();
        String url = String.format(this.urlBase, "Price", this.productID);
        try {
            JSONArray data = conn.getData(url);
            for (Object buff: data) {
                JSONObject row = (JSONObject) buff;
                String image = (String) row.get("Images");
                PriceObj price = new PriceObj();
                price.setBasePrice(stringToDouble((String) row.get("BasePrice")));
                price.setShipping(stringToDouble((String) row.get("Shipping")));
                price.setMerchant((String) row.get("Merchant"));
                int avail = stringToInteger((String) row.get("Availability"));
                boolean availability = (avail == 1) ? true : false;
                price.setAvail(availability);
                String purchaseLink =  Constants.PCPP_MAIN_URL +
                        (String) row.get("PurchaseLink");
                price.setPurchaseLink(purchaseLink);
                priceData.add(price);

            }
        }catch (Exception e) {

        }
        return priceData;
    }

    protected double stringToDouble(String x) {
        if (x == null){
            return -2.0;
        }
        return Double.valueOf(x);
    }

    protected int stringToInteger(String x) {
        if (x == null){
            return -2;
        }
        return Integer.valueOf(x);
    }
}

package pcpp_data.queries;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pcpp_data.conn.Conn;
import pcpp_data.constants.Constants;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.PriceObj;
import pcpp_data.sqllite.database;

public class SingleProductQuery {
    private int productID;
    private String urlBase;
    private database db;

    public SingleProductQuery(int productID, Context context){
        this.productID = productID;
        this.db = new database(context);
        this.urlBase = new SqlConstants().SINGLE_PRODUCT;
    }

    public ArrayList<String> getImageGallery() {
        ArrayList<String> images = new ArrayList<>();
        String url = String.format(this.urlBase, "Images", this.productID);
        JSONArray data = db.getData(url);
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
        return images;
    }

    public HashMap<String, String> getSpecs(String table){
        HashMap <String, String> specs = new HashMap<>();
        String url = String.format(this.urlBase, table, this.productID);
        JSONArray data = db.getData(url);
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

    }

    public ArrayList<String> getSpecOrder(String table) {
        String url = "PRAGMA table_info([%s]);";
        url = String.format(url, table);
        ArrayList<String> fields = new ArrayList<>();
        JSONArray data = db.getData(url);
        for (Object buff: data) {
            JSONObject row = (JSONObject) buff;
            String field = (String) row.get("name");
            if (!field.equals("id") && !field.equals("ProductID")) {
                fields.add(field);
            }
        }
        return fields;
    }

    public ArrayList<PriceObj> getPrice(){
        ArrayList<PriceObj> priceData = new ArrayList<>();
        String url = String.format(this.urlBase, "Price", this.productID);
        try {
            JSONArray data = db.getData(url);
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

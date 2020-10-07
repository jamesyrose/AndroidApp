package pcpp_data.queries;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pcpp_data.backflow.BuyLinkFetch;
import pcpp_data.conn.Conn;
import pcpp_data.constants.Constants;
import pcpp_data.constants.SqlConstants;
import pcpp_data.products.PriceObj;
import pcpp_data.sqllite.database;


/*
Queries the product information. This is for the details related to the product.
It is used when the product is clicked on to present more information.
 */

public class SingleProductQuery {
    private int productID;
    private String urlBase;
    private database db;
    private Context context;

    public SingleProductQuery(int productID, Context context){
        this.productID = productID;
        this.context = context;
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
                int productID = Integer.valueOf((String) row.get("ProductID"));
                PriceObj price = new PriceObj();
                price.setBasePrice(stringToDouble((String) row.get("BasePrice")));
                price.setShipping(stringToDouble((String) row.get("Shipping")));
                price.setMerchant((String) row.get("Merchant"));
                int avail = stringToInteger((String) row.get("Availability"));
                boolean availability = (avail == 1) ? true : false;
                price.setAvail(availability);
                String purchaseLink =  getURL((String) row.get("Merchant"));
                // Check for existance if not query it and send it
                try{
                    new URL(purchaseLink);
                    price.setPurchaseLink(purchaseLink);
                    priceData.add(price);
                }catch (Exception e){

                }
            }
            PriceObj price = new PriceObj();
            price.setBasePrice(-1);
            price.setShipping(-2);
            price.setMerchant("Google");
            price.setAvail(false);
            price.setPurchaseLink(getURL("Google"));
            priceData.add(price);
        }catch (Exception e) {

        }
        return priceData;
    }

    private String getURL(String merchant){
        String firstSql = String.format("SELECT ProductName, ProductType FROM ProductMain WHERE ProductID = %d", productID);
        JSONArray firstBuff = new database(context).getData(firstSql);
        String productName = (String) ((JSONObject) firstBuff.get(0)).get("ProductName");
        String productType = (String) ((JSONObject) firstBuff.get(0)).get("ProductType");
        String secondSql = String.format("SELECT `Part #` FROM %s WHERE ProductID = %d", productType, productID);
        JSONArray secondBuff = new database(context).getData(secondSql);
        String productNum = (String) ((JSONObject) secondBuff.get(0)).get("Part #");
        // 100-100000025BOX
        if (merchant.equals("Amazon")){
            String base = "https://www.amazon.com/s?k=%s&i=electronics" ;
            String url = String.format(base, encodeValue(productNum));
            return url;
        }else if (merchant.equals("Best Buy")){
            String base = "https://www.bestbuy.com/site/searchpage.jsp?st=%s";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("Newegg")){
            String base = "https://www.newegg.com/p/pl?d=%s";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("MemoryC")){
            String base = "https://www.memoryc.com/search.html?q=%s&Submit=Submit";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("Walmart")){
            String base = "https://www.walmart.com/search/?query=%s";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("B&H")){
            String base = "https://www.bhphotovideo.com/c/search?Ntt=%s";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("Adorama")){
            String base = "https://www.adorama.com/l/?searchinfo=%s";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("Corsair")){
            String base = "https://www.corsair.com/us/en/search/?text=%s&type=all";
            return String.format(base, encodeValue(productNum));
        } else if (merchant.equals("Staples")){
            String base = "https://www.staples.com/%s/directory_%s";
            return String.format(base, encodeValue(productNum), encodeValue(productNum));
        }else if (merchant.equals("Office Depot")){
            String base = "https://www.officedepot.com/catalog/search.do?Ntt=%s";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("Monoprice")){
            String base = "https://www.monoprice.com/search/index?keyword=%s";
            return String.format(base, encodeValue(productNum));
        }else if (merchant.equals("Microcenter")){
            String base = "https://www.microcenter.com/search/search_results.aspx?N=&cat=&Ntt=%s&searchButton=search";
            return String.format(base, encodeValue(productName));
        }else if (merchant.equals("Google")){
            String base = "https://www.google.com/search?biw=1072&bih=1146&tbm=shop&ei=N9R8X8WMCtTn-wSw47ioCA&q=%s&oq=%s";
            return String.format(base, encodeValue(productNum), encodeValue(productNum));
        }
        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
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

package pcpp_data.products;

import android.content.Context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import pcpp_data.constants.SqlConstants;
import pcpp_data.sqllite.database;

public class GeneralProduct {
    private Context context;
    private int productID;
    private String productName = "";
    private double price = 0.0;
    private double shipping = 0.0;
    private int ratingCount = 0;
    private double ratingAverage = 0;
    private int tdp = 0;
    private String dispImage  = "";
    private String productType = "";
    private SqlConstants sqlConsts;

    public GeneralProduct(Context context, int productID) {
        this.productID = productID;
        this.context = context;
        sqlConsts = new SqlConstants();
        getData();
    }

    private void getData(){
        String base = sqlConsts.SELECTED_PRODUCT_SEARCH;
        String sqlString = String.format(base, productID);
        database db = new database(context);
        JSONArray buff = db.getData(sqlString);
        try{
            JSONObject data = (JSONObject) buff.get(0);
            this.productName = (String) data.get("ProductName");
            this.price = stringToDouble( (String) data.get("BestPrice"));
            this.shipping = stringToDouble( (String) data.get("Shipping"));
            this.ratingCount = stringToInt( (String) data.get("Count"));
            this.ratingCount = (this.ratingCount < 0) ? 0 : this.ratingCount;
            this.ratingAverage= stringToDouble( (String) data.get("Average"));
            this.dispImage = (String) data.get("Images");
            this.tdp = Integer.parseInt( (String) data.get("TDP"));
            this.productType = (String) data.get("ProductType");
        }catch (java.lang.IndexOutOfBoundsException e){

        }

    }

    public int stringToInt(String val){
        if (val != null){
            return Integer.parseInt(val);
        }else{
            return -2;
        }
    }

    public double stringToDouble(String val){
        if (val != null){
            return Double.parseDouble(val);
        }
        return -2.0;
    }


    public int getProductID(){
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public double getShipping() {
        return shipping;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public int getTdp() {
        return tdp;
    }

    public String getDispImage() {
        return dispImage;
    }

    public String getProductType() {
        return productType;
    }
}

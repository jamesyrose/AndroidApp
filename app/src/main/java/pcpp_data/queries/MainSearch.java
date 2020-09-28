package pcpp_data.queries;

import org.json.simple.JSONObject;

import pcpp_data.constants.Constants;

public class MainSearch {
    private String productName;
    private String displayImg;
    private int productID, ratingCount;
    private double bestPrice, ratingAverage;

    public MainSearch(JSONObject row) {
        this.productID = stringToInteger((String) row.get("ProductID"));
        this.productName = (String) row.get("ProductName");
        this.bestPrice = stringToDouble((String) row.get("BestPrice"));
        this.ratingCount = stringToInteger((String) row.get("Count"));
        this.ratingAverage = stringToDouble((String) row.get("Average"));
        // Adding the full url to image url
        String buff = (String) row.get("Images");
        if (buff != null) {
            for (String key: Constants.IMAGE_BASE_MAP.keySet()) {
                buff = buff.replace(key, Constants.IMAGE_BASE_MAP.get(key));
            }
        }
        this.displayImg = buff;
    }

    protected double stringToDouble(String x) {
        if (x == null){
            return -1.0;
        }
        return Double.valueOf(x);
    }

    protected int stringToInteger(String x) {
        if (x == null){
            return -1;
        }
        return Integer.valueOf(x);
    }

    public final String getProductName() {
        return productName;
    }

    public final String getDisplayImg() {
        return this.displayImg;
    }

    public final int getProductID() {
        return this.productID;
    }

    public final double getBestPrice() {
        return this.bestPrice;
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    @Override
    public String toString() {
        return String.format("%s %d %.2f %.2f", this.productName, this.productID,  this.bestPrice, this.ratingAverage );
    }


}
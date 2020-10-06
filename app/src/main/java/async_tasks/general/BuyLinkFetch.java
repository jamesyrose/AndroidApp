package pcpp_data.backflow;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import pcpp_data.constants.SqlConstants;
import pcpp_data.sqllite.database;

public class BuyLinkFetch extends AsyncTask<String, Void, ArrayList<String>> {
    int productID;
    String merchant;
    Context context;
    String productName, productType, productNum;

    public BuyLinkFetch (Context context,int productID,  String merchant){
        this.productID = productID;
        this.merchant = merchant;
        this.context = context;
        System.out.println("##############################HELLOOO####");
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        if (merchant.equals("Amazon")){
            amazonURL();
        }
        return null;
    }

    public String amazonURL(){
        String base = "https://www.amazon.com/s?k=%s&i=electronics" ;
        getPartNum();
        String url = String.format(base, productNum);
        try {
            System.out.println(url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            String productLink = "";
            String price = "";
            for (Element element: doc.getAllElements()) {
                for (Attribute attr : element.attributes()) {
                    if (attr.getKey().equals("data-index") && attr.getValue().equals("0")) {
                        for (Element ele: element.getAllElements()) {
                            for (Attribute subAtter: ele.attributes()) {
                                if (subAtter.getKey().equals("href")) {
                                    if (productLink.equals("")) {
                                        productLink += "https://www.amazon.com" + subAtter.getValue();
                                        System.out.println(productLink);
                                    }
                                }
//                                if (subAtter.getKey().equals("class") && subAtter.getValue().equals("a-price")){
//                                    if (price.equals("")){
//                                        price += ele.text();
//                                    }
//                                }
                            }
                        }
                    }
                }
            }
            System.out.println(price + " " +  productLink);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    private void getPartNum(){
        String firstSql = String.format("SELECT ProductName, ProductType FROM ProductMain WHERE ProductID = %d", productID);
        JSONArray firstBuff = new database(context).getData(firstSql);
        productName = (String) ((JSONObject) firstBuff.get(0)).get("ProductName");
        productType = (String) ((JSONObject) firstBuff.get(0)).get("ProductType");
        String secondSql = String.format("SELECT `Part #` FROM %s WHERE ProductID = %d", productType, productID);
        JSONArray secondBuff = new database(context).getData(secondSql);
        productNum = (String) ((JSONObject) secondBuff.get(0)).get("Part #");
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }



}

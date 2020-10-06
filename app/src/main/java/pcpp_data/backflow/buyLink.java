package pcpp_data.backflow;

import android.content.Context;
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
import java.util.Iterator;

import pcpp_data.constants.SqlConstants;
import pcpp_data.sqllite.database;

public class buyLink {
    int productID;
    String merchant;
    String table;
    Context context;

    public buyLink(Context context, int productID, String merchant, String table){
        this.context = context;
        this.productID = productID;
        this.merchant = merchant;
        this.table = table;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String amazonURL(){
        String base = "https://www.amazon.com/s?k=%s&i=electronics" ;
        String partNum = getPartNum();
        String url = encodeValue(String.format(base, partNum));
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            String productLink = "";
            for (Element element: doc.getAllElements()) {
                for (Attribute attr : element.attributes()) {
                    if (attr.getKey().contentEquals("data-index")) {
                        for (Element ele: element.getAllElements()) {
                            for (Attribute subAtter: ele.attributes()) {
                                if (subAtter.getKey().equals("href")) {
                                    productLink += "https://www.amazon.com" + subAtter.getValue();
                                    break;
                                }
                            }
                            if (!productLink.equals("")) {break;}
                        }
                        if (!productLink.equals("")) {break;}
                    }
                }
                if (!productLink.equals("")) {break;}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    private String getPartNum(){
        String url = String.format(new SqlConstants().SINGLE_PRODUCT, table, this.productID);
        JSONArray data = new database(context).getData(url);
        for (Object buff: data) {
            try{
                JSONObject row = (JSONObject) buff;
                return (String) row.get("Part #");
            }catch (Exception e){

            }
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


}

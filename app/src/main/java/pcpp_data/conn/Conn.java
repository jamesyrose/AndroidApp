package pcpp_data.conn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Conn {

    public String getDataAsString(String webURL) throws IOException, ParseException, JSONException {
        URL url = new URL(webURL);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responsecode = conn.getResponseCode();
        StringBuilder inline = new StringBuilder();
        if(responsecode != 200){
            throw new RuntimeException("HttpResponseCode: " +responsecode);
        } else{
            Scanner sc = new Scanner(url.openStream());
            while(sc.hasNext()){
                String buff = sc.nextLine();
                inline.append(buff);
            }
            sc.close();
        }
        return inline.toString();
    }

    public JSONArray getData(String webURL) throws IOException, ParseException, JSONException {
        URL url = new URL(webURL);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");

        conn.connect();
        int responsecode = conn.getResponseCode();
        String inline = "";
        if(responsecode != 200)
            throw new RuntimeException("HttpResponseCode: " +responsecode);
        else{
            Scanner sc = new Scanner(url.openStream());
            while(sc.hasNext()){
                inline+=sc.nextLine();
            }
            sc.close();
        }

        JSONParser parse = new JSONParser();
        JSONArray jobj = (JSONArray) parse.parse(inline);
        return jobj;
    }
}

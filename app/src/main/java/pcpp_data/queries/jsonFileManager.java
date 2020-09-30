package pcpp_data.queries;

import android.content.Context;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class jsonFileManager {
    public String read(Context context, String fileName) {
        JSONParser parser = new JSONParser();
        try {
            String filePath = fileName;
            System.out.println(filePath);
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject.toString());
            return jsonObject.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "jhsdfkhjgsdbhfkjg";
    }

    public boolean create(Context context, String fileName, String jsonString){
        String FILENAME = "storage.json";
        try {
            System.out.println("@##################");
            String filePath = context.getFilesDir() + File.separator + "TEST" + File.separator + fileName;
            System.out.println(filePath);
            File file = new File( filePath);
            System.out.println(jsonString);
            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonString);
            writer.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            fileNotFound.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }

    public String getData(String webURL) throws IOException, ParseException, JSONException {
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
        return inline;
    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }
}

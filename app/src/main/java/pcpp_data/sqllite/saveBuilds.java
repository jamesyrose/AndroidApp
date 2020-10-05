package pcpp_data.sqllite;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class saveBuilds {
    private Context context;
    private String BUILD_ID;
    private String name = "";



    public saveBuilds(Context context, String build_ID){
        this.BUILD_ID = build_ID;
        this.context = context;
        this.name = getName();
    }

    public String getName(){
        String sql  = String.format("SELECT name FROM `SavedBuild` WHERE buildID = '%s';", BUILD_ID);
        String  name = "";
        JSONArray data = new database(context).getData(sql);
        try{
            JSONObject d = (JSONObject) data.get(0);
            name = (String) d.get("name");
        }catch (Exception e){

        }
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public ArrayList<Integer> getAddedProducts(){
        String sql = String.format("SELECT ProductID FROM `SavedBuild` WHERE buildID ='%s'", BUILD_ID);
        ArrayList<Integer> products = new ArrayList<>();
        JSONArray data = new database(context).getData(sql);
        for (Object o: data){
            JSONObject buff = (JSONObject) o;
            products.add(Integer.valueOf((String) buff.get("ProductID")));
        }
        return products;
    }

    public void addToBuild(int productID){
        @SuppressLint("DefaultLocale") String sql = String.format(
                "INSERT INTO `SavedBuild` (added, ProductID, name, saved, buildID, productType) " +
                        "VALUES (CURRENT_TIMESTAMP, %d, '%s', 0, '%s', (SELECT ProductMain.ProductType\n" +
                                                                        "FROM ProductMain\n" +
                                                                        "WHERE ProductMain.ProductID  = %d))",
                productID, name, BUILD_ID, productID);
        String prodType = getProductType(productID);
        String[] prodsCanOnlyHaveOne = {"CPU", "CPU_Cooler", "Motherboard", "PSU", "Cases"};
        ArrayList<Integer> existingProducts = getAddedProducts();
        // Check to see if it is there
        for (int prod: existingProducts){
            String buff = getProductType(prod);
            if (Arrays.asList(prodsCanOnlyHaveOne).contains(prodType)){
                if (prodType.equals(buff)){
                    removeFromBuild(prod);
                }
            }
        }
        new database(context).execSQL(sql);
    }

    public String getProductType(int productID){
        String sql  = String.format("SELECT ProductType FROM `ProductMain` WHERE ProductID = %d;", productID);
        JSONArray  data = new database(context).getData(sql);
        String prodType = (String) ((JSONObject) data.get(0)).get("ProductType");
        return prodType;
    }

    public void removeFromBuild(int productID){
        String sql = String.format("DELETE FROM `SavedBuild`" +
                        " WHERE rowid = (SELECT rowid " +
                        "FROM `SavedBuild` " +
                        "WHERE ProductID=%d " +
                        "AND buildID = '%s' " +
                        "LIMIT 1)",
                productID, BUILD_ID);
        System.out.println(sql);
        new database(context).execSQL(sql);
    }

    public void deleteBuild(){
        String sql = String.format("DELETE FROM `SavedBuild` WHERE buildID = '%s'", BUILD_ID);
        new database(context).execSQL(sql);
    }

    public void saveBuild(){
        String sql = String.format("UPDATE `SavedBuild` " +
                "SET saved = 1, name = '%s' " +
                "WHERE  buildID = '%s'; ",
                name,
                BUILD_ID);
        new database(context).execSQL(sql);
    }

    public String getLastUnsavedID(){
        String sql = "SELECT buildID FROM `SavedBuild` WHERE saved = 0 ORDER BY added DESC; ";
        JSONArray data = new database(context).getData(sql);
        try{
            JSONObject buff = (JSONObject) data.get(0);
            return (String) buff.get("buildID");
        }catch(Exception e){

        }
        return "";
    }
}

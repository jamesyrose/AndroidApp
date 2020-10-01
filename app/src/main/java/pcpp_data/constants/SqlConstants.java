package pcpp_data.constants;

public class SqlConstants {

    public SqlConstants(){

    }

    public final String CPU_SEARCH_LIST =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, CPU.Manufacturer, " +
            "CPU.`Core Count`, CPU.`Core Clock`, CPU.`Boost Clock`, CPU.TDP, CPU.Socket " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID " +
            "LEFT JOIN CPU on CPU.ProductID=ProductMain.ProductID " +
            "WHERE  ProductMain.ProductType = 'CPU';";

    public final String CPU_COOLER_SEARCH_LIST =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, \n" +
            "CPU_Cooler.Manufacturer, CPU_Cooler.`Fan RPM`, CPU_Cooler.`Noise Level`, CPU_Cooler.`Water Cooled`, CPU_Cooler.Height \n" +
            "FROM ProductMain \n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN CPU_Cooler on CPU_Cooler.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'CPU_Cooler';";

    public final String MOTHERBOARD_SEARCH_LIST =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, Motherboard.Manufacturer, " +
            "Motherboard.`Socket / CPU`, Motherboard.`Memory Max`, Motherboard.`Form Factor` , " +
            "Motherboard.Chipset " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Motherboard on Motherboard.ProductID=ProductMain.ProductID " +
            "WHERE  ProductMain.ProductType = 'Motherboard';";

    public final String SINGLE_PRODUCT  = "SELECT * FROM %s WHERE ProductID = %d";
}

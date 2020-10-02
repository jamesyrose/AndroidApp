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
            "WHERE  ProductMain.ProductType = 'CPU' ;";

    public final String CPU_SEARCH_LIST_FILTERED = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, CPU.Manufacturer, \n" +
            "CPU.`Core Count`, CPU.`Core Clock`,  \n" +
            "CPU.`Boost Clock`, CPU.TDP, CPU.Socket \n" +
            "FROM ProductMain \n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN CPU on CPU.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'CPU'\n" +
            "AND CPU.Manufacturer IN (%s)\n" +
            "AND ProductMain.BestPrice > %d " +
            "AND ProductMain.BestPrice < %d " +
            "AND CAST(CPU.`Core Count` AS INT) > %d\n" +
            "AND CAST(CPU.`Core Count` AS INT) < %d\n" +
            "AND CAST(CPU.`Core Clock` AS FLOAT) > %.2f\n" +
            "AND CAST(CPU.`Core Clock` AS FLOAT) < %.2f\n" +
            "AND CAST(CPU.`Boost Clock` AS FLOAT) > %.2f \n" +
            "AND CAST(CPU.`Boost Clock` AS FLOAT) < %.2f \n" +
            "AND CAST(CPU.TDP AS INT) > %d \n" +
            "AND CAST(CPU.TDP AS INT) < %d\n" +
            "ORDER BY %s;";

    public final String CPU_COOLER_SEARCH_LIST =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, " +
            "CPU_Cooler.Manufacturer, CPU_Cooler.`Fan RPM`, CPU_Cooler.`Noise Level`, CPU_Cooler.`Water Cooled`, CPU_Cooler.Height " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID " +
            "LEFT JOIN CPU_Cooler on CPU_Cooler.ProductID=ProductMain.ProductID " +
            "WHERE  ProductMain.ProductType = 'CPU_Cooler';";

    public final String CPU_COOLER_SEARCH_LIST_FILTERED = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, " +
            "CPU_Cooler.Manufacturer, CPU_Cooler.`Fan RPM`, CPU_Cooler.`Noise Level`, CPU_Cooler.`Water Cooled`, CPU_Cooler.Height " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN CPU_Cooler on CPU_Cooler.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'CPU_Cooler' \n" +
            "AND CPU_Cooler.Manufacturer IN (%s) " +
            "AND ProductMain.BestPrice > %d " +
            "AND ProductMain.BestPrice < %d " +
            "AND  (CPU_Cooler.`Water Cooled` LIKE '%s' OR CPU_Cooler.`Water Cooled` LIKE '%s')  "  +
            "ORDER BY %s;";



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

    public final String MOTHERBOARD_SEARCH_FILTER = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, Motherboard.Manufacturer, \n" +
            "Motherboard.`Socket / CPU`, Motherboard.`Memory Max`, Motherboard.`Form Factor` , \n" +
            "Motherboard.Chipset \n" +
            "FROM ProductMain\n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Motherboard on Motherboard.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'Motherboard'\n" +
            "AND Motherboard.Manufacturer IN (%s) \n" +
            "AND Motherboard.`Socket / CPU` IN (%s)\n" +
            "AND Motherboard.`Form Factor` IN (%s) \n" +
            "AND ProductMain.BestPrice > %d \n" +
            "AND ProductMain.BestPrice < %d \n" +
            "AND CAST(Motherboard.`Memory Max` AS INT) > %d\n" +
            "AND CAST(Motherboard.`Memory Max` AS INT) < %d\n" +
            "AND CAST(Motherboard.`Memory Slots` AS INT) > %d\n" +
            "AND CAST(Motherboard.`Memory Slots` AS INT) <%d\n" +
            "ORDER BY %s ;";


    public final String MEMORY_SEARCH_LIST =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, " +
            "Memory.Manufacturer, Memory.Modules, Memory.`Price / GB`, Memory.`ECC / Registered`, Memory.Speed " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Memory on Memory.ProductID=ProductMain.ProductID " +
            "WHERE  ProductMain.ProductType = 'Memory';";

    public final String SINGLE_PRODUCT  = "SELECT * FROM %s WHERE ProductID = %d";
}

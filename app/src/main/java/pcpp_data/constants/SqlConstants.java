package pcpp_data.constants;

public class SqlConstants {

    public SqlConstants(){

    }

    public final String cpuSearchList =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, CPU.Manufacturer, " +
            "CPU.`Core Count`, CPU.`Core Clock`, CPU.`Boost Clock`, CPU.TDP, CPU.Socket " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID " +
            "LEFT JOIN CPU on CPU.ProductID=ProductMain.ProductID " +
            "WHERE  ProductMain.ProductType = 'CPU';";
}

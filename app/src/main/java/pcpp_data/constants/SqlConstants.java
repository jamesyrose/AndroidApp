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
            "WHERE  ProductMain.ProductType = 'CPU' " +
            "ORDER BY Rating.Count DESC;";

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
            "AND ProductMain.BestPrice >= %d " +
            "AND ProductMain.BestPrice <= %d " +
            "AND CAST(CPU.`Core Count` AS INT) >= %d\n" +
            "AND CAST(CPU.`Core Count` AS INT) <= %d\n" +
            "AND CAST(CPU.`Core Clock` AS FLOAT) >= %.2f\n" +
            "AND CAST(CPU.`Core Clock` AS FLOAT) <= %.2f\n" +
            "AND CAST(CPU.`Boost Clock` AS FLOAT) >= %.2f \n" +
            "AND CAST(CPU.`Boost Clock` AS FLOAT) <= %.2f \n" +
            "AND CAST(CPU.TDP AS INT) >= %d \n" +
            "AND CAST(CPU.TDP AS INT) <= %d\n" +
            "ORDER BY %s;";

    public final String CPU_COOLER_SEARCH_LIST =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, " +
            "CPU_Cooler.Manufacturer, CPU_Cooler.`Fan RPM`, CPU_Cooler.`Noise Level`, CPU_Cooler.`Water Cooled`, CPU_Cooler.Height " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID " +
            "LEFT JOIN CPU_Cooler on CPU_Cooler.ProductID=ProductMain.ProductID " +
            "WHERE  ProductMain.ProductType = 'CPU_Cooler' " +
            "ORDER BY Rating.Count DESC;";

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
            "AND ProductMain.BestPrice >= %d " +
            "AND ProductMain.BestPrice <= %d " +
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
            "WHERE  ProductMain.ProductType = 'Motherboard' " +
            "ORDER BY Rating.Count DESC;";

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
            "AND ProductMain.BestPrice >= %d \n" +
            "AND ProductMain.BestPrice <= %d \n" +
            "AND CAST(Motherboard.`Memory Max` AS INT) >= %d\n" +
            "AND CAST(Motherboard.`Memory Max` AS INT) <= %d\n" +
            "AND CAST(Motherboard.`Memory Slots` AS INT) >= %d\n" +
            "AND CAST(Motherboard.`Memory Slots` AS INT) <=%d\n" +
            "ORDER BY %s ;";


    public final String MEMORY_SEARCH_LIST =  "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, " +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, " +
            "Memory.Manufacturer, Memory.Modules, Memory.`Price / GB`, Memory.`ECC / Registered`, " +
            " (Memory.Speed|| ' '|| CASE WHEN Memory.Type IS NULL THEN '' ELSE Memory.Type END)  AS Speed " +
            "FROM ProductMain " +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID " +
            "LEFT JOIN Memory on Memory.ProductID=ProductMain.ProductID " +
            "WHERE  ProductMain.ProductType = 'Memory' " +
            "ORDER BY Rating.Count DESC;";

    public final String MEMORY_SEARCH_FILTER = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images, \n" +
            "Memory.Manufacturer, Memory.Modules, Memory.`Price / GB`, Memory.`ECC / Registered`," +
            " (Memory.Speed|| ' '||CASE WHEN Memory.Type IS NULL  THEN '' ELSE Memory.Type END)  AS Speed, " +
            " CAST(Memory.Speed as INT) AS spd \n" +
            "FROM ProductMain \n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Memory on Memory.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'Memory'\n" +
            "AND Memory.Manufacturer IN (%s)\n" +
            "AND ProductMain.BestPrice >= %d\n" +
            "AND ProductMain.BestPrice <= %d\n" +
            "AND CAST(Memory.Speed AS INT) >= %d\n" +
            "AND CAST(Memory.Speed AS INT) <= %d\n" +
            "AND CAST(REPLACE(SUBSTR(REPLACE(Modules, 'x', 'XXXX'), 1, 3), 'X', '') AS INT) >= %d\n" +
            "AND CAST(REPLACE(SUBSTR(REPLACE(Modules, 'x', 'XXXX'), 1, 3), 'X', '') AS INT) <= %d\n" +
            "AND CAST(REPLACE(SUBSTR(REPLACE(Modules, 'x', 'XXXX'), 4, 20), 'X', '') AS INT) >= %d\n" +
            "AND CAST(REPLACE(SUBSTR(REPLACE(Modules, 'x', 'XXXX'), 4, 20), 'X', '') AS INT) <= %d\n" +
            "AND CAST(REPLACE(Memory.`Price / GB`, '$', '') AS FLOAT) >= %.2f\n" +
            "AND CAST(REPLACE(Memory.`Price / GB`, '$', '') AS FLOAT) <= %.2f\n" +
            "AND (Memory.`ECC / Registered` LIKE '%s' OR Memory.`ECC / Registered` LIKE '%s') \n" +
            "ORDER BY %s ;\n";

    public final String STORAGE_SEARCH_LIST = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "Storage.Manufacturer, Storage.Capacity, Storage.`Form Factor`, Storage.Type, Storage.NVME\n" +
            "FROM ProductMain \n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Storage on Storage.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'Storage' " +
            "ORDER BY Rating.Count DESC;";

    public final String STORAGE_SEARCH_FILTER = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "Storage.Manufacturer, Storage.Capacity, Storage.`Form Factor`, Storage.Type, Storage.NVME\n" +
            "FROM ProductMain \n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Storage on Storage.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'Storage'\n" +
            "AND Storage.Manufacturer IN (%s)\n" +
            "AND Storage.`Form Factor` IN (%s)\n" +
            "AND Storage.Type IN (%s)\n" +
            "AND (Storage.NVME = '%s' OR Storage.NVME = '%s' ) \n" +
            "AND ProductMain.BestPrice > %d\n" +
            "AND ProductMain.BestPrice < %d\n" +
            "AND CAST(Storage.Capacity AS INT) > %d\n" +
            "AND CAST(Storage.Capacity AS INT) < %d\n" +
            "ORDER BY %s";

    public final String GPU_SEARCH_LIST = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "GPU.Manufacturer, GPU.`Core Clock`, GPU.Memory, GPU.Length, GPU.TDP, GPU.Chipset \n" +
            "FROM ProductMain \n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN GPU on GPU.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'GPU' " +
            "ORDER BY Rating.Count DESC;\n";

    public final String GPU_SEARCH_FILTER = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "GPU.Manufacturer, GPU.`Core Clock`, GPU.Memory, GPU.Length, GPU.TDP, GPU.Chipset\n" +
            "FROM ProductMain \n" +
            "LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "LEFT JOIN GPU on GPU.ProductID=ProductMain.ProductID \n" +
            "WHERE  ProductMain.ProductType = 'GPU'\n" +
            "AND GPU.Manufacturer  IN (%s)\n" +
            "AND ProductMain.BestPrice > %d\n" +
            "AND ProductMain.BestPrice < %d\n" +
            "AND CAST(GPU.Memory AS INT) > %d\n" +
            "AND CAST(GPU.Memory AS INT) < %d\n" +
            "AND CAST(GPU.TDP AS INT) > %d\n" +
            "AND CAST(GPU.TDP AS INT) < %d\n" +
            "ORDER BY %s;\n" +
            "\n";

    public final String PSU_SEARCH_LIST = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "    ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "    PSU.Manufacturer, PSU.Model, PSU.Wattage, PSU.Modular, PSU.`Efficiency Rating`, PSU.`Form Factor`\n" +
            "    FROM ProductMain \n" +
            "    LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN PSU on PSU.ProductID=ProductMain.ProductID \n" +
            "    WHERE  ProductMain.ProductType = 'PSU' " +
            "ORDER BY Rating.Count DESC;";

    public final String PSU_SEARCH_FILTER = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "    ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "    PSU.Manufacturer, PSU.Model, PSU.Wattage, PSU.Modular, PSU.`Efficiency Rating`, PSU.`Form Factor`\n" +
            "    FROM ProductMain \n" +
            "    LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN PSU on PSU.ProductID=ProductMain.ProductID \n" +
            "    WHERE  ProductMain.ProductType = 'PSU'\n" +
            "    AND PSU.Manufacturer IN (%s)\n" +
            "    AND PSU.`Efficiency Rating` IN (%s)\n" +
            "    AND PSU.`Form Factor` IN (%s) \n" +
            "    AND PSU.Modular IN (%s) \n" +
            "    AND ProductMain.BestPrice > %d \n" +
            "    AND ProductMain.BestPrice < %d \n" +
            "    AND CAST(PSU.Wattage AS INT) >  %d\n" +
            "    AND CAST(PSU.Wattage AS INT) < %d\n" +
            "    ORDER BY %s ;\n" +
            "    ";

    public final String CASE_SEARCH_LIST = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "        ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "        Cases.Manufacturer, Cases.Type, Cases.`Power Supply Shroud`, Cases.`Side Panel Window`, \n" +
            "        CAST(SUBSTR(Cases.`Maximum Video Card Length`, 1, 4) AS INT) GPU_Length\n" +
            "        FROM ProductMain \n" +
            "        LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "        LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "        LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "        LEFT JOIN Cases on Cases.ProductID=ProductMain.ProductID \n" +
            "        WHERE  ProductMain.ProductType = 'Cases'  " +
            "ORDER BY Rating.Count  DESC;";

    public final String CASE_SEARCH_FILTER = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "        ProductMain.BestPrice, Rating.Count, Rating.Average, Images.Images,\n" +
            "        Cases.Manufacturer, Cases.Type, Cases.`Power Supply Shroud`, Cases.`Side Panel Window`, \n" +
            "        CAST(SUBSTR(Cases.`Maximum Video Card Length`, 1, 4) AS INT) GPU_Length\n" +
            "        FROM ProductMain \n" +
            "        LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "        LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "        LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "        LEFT JOIN Cases on Cases.ProductID=ProductMain.ProductID \n" +
            "        WHERE  ProductMain.ProductType = 'Cases';\n" +
            "        AND Cases.Manufacturer IN (%s)\n" +
            "        AND Cases.`Power Supply Shroud` IN (%s)\n" +
            "        AND Cases.`Type` IN (%s)\n" +
            "        AND Cases.`Side Panel Window` IN (%s) \n" +
            "        AND ProductMain.BestPrice > %d \n" +
            "        AND ProductMain.BestPrice < %d \n" +
            "        ORDER BY %s ;\n" +
            "\n";

    public final String CREATE_SAVED_BUILD_TABLES = "CREATE TABLE IF NOT EXISTS `SavedBuild` (\n" +
            " `added`  DATETIME NOT NULL,\n" +
            "  `ProductID` INT NOT NULL,\n" +
            "  `name` VARCHAR(90) NULL,\n" +
            "  `buildID` VARCHAR(20) NULL,\n" +
            "  `productType` VARCHAR(20) NULL,\n" +
            " `saved` INT UNSIGNED NOT NULL DEFAULT 0 );";


    public final String SELECTED_PRODUCT_SEARCH = "SELECT DISTINCT ProductMain.ProductName, ProductMain.ProductID, \n" +
            "        ProductMain.BestPrice, ProductMain.ProductType, Rating.Count, Rating.Average, \n" +
            "          Price.PurchaseLink, buff.Images,\n" +
            "        CASE " +
            "               WHEN Price.Shipping IS NULL " +
            "                   THEN -2 " +
            "               ELSE Price.Shipping" +
            "        END Shipping,  " +
            "        CASE  \n" +
            "                WHEN GPU.TDP IS NOT NULL \n" +
            "                    THEN CAST(GPU.TDP AS INT)\n" +
            "                WHEN CPU.TDP IS NOT NULL \n" +
            "                    THEN CAST(CPU.TDP  AS INT)\n" +
            "                ELSE 0 \n" +
            "        END TDP\n" +
            "    FROM ProductMain \n" +
            "    LEFT JOIN Images on Images.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN Rating on Rating.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN Price on Price.ProductID=ProductMain.ProductID \n" +
            "    LEFT JOIN CPU on CPU.ProductID=ProductMain.ProductID\n" +
            "    LEFT JOIN GPU ON GPU.ProductID=ProductMain.ProductID\n" +
            "    LEFT JOIN (SELECT ProductID,\n" +
            "                CASE \n" +
            "                    WHEN Images.Images IS NULL\n" +
            "                        THEN \"ZZZZZZZZZZZZ\"\n" +
            "                    ELSE Images.Images\n" +
            "                END Images\n" +
            "                FROM Images\n" +
            "                ) AS buff ON  buff.ProductID= ProductMain.ProductID\n" +
            "    WHERE ProductMain.ProductID = %d \n" +
            "    ORDER BY buff.Images" +
            "    LIMIT 1;\n";
    
    public final String SINGLE_PRODUCT  = "SELECT * FROM %s WHERE ProductID = %d";
}

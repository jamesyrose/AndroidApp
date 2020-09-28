package pcpp_data.constants;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    private Constants() {
        // restrict instantiation
    }

    public static final String PRODUCT = "https://pcpartpicker.com/product/";
    public static final String PCPP_MAIN_URL = "https://pcpartpicker.com/";
    public static final String FLOAT_MATCH = "[^0-9.]";
    public static final String 	INT_MATCH = "[^0-9]";
    public static final Map<String, String> IMAGE_BASE_MAP = new HashMap<String, String>() {{
        put("PCPP/", "https://cdn.pcpartpicker.com/static/forever/images/product/");
        put("AMZN1/", "https://m.media-amazon.com/images/");
        put("AMZN_EU/", "https://images-eu.ssl-images-amazon.com/images/");
        put("AMZN_NA/", "https://images-na.ssl-images-amazon.com/images/");
    }};

    public static final String[] PRODUCT_SPEC_COL_DROP = {"ProductID", "ProductName", "ProductLink",
            "NeedsUpdate", "LastUpdate", "id", "BestPrice", "ProductName", "Images", "COUNT", "Average"};
}

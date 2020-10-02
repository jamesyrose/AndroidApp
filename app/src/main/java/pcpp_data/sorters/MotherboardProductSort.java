package pcpp_data.sorters;

import java.util.ArrayList;

import pcpp_data.products.MotherboardProduct;

public class MotherboardProductSort {

    public ArrayList<MotherboardProduct> sortPopularity(ArrayList<MotherboardProduct> data){
        ArrayList<MotherboardProduct> sorted = new ArrayList<>();
        ArrayList<MotherboardProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int popularity = orig.get(0).getRatingCount();
            System.out.println(popularity + "#####");
            MotherboardProduct mostPopular = orig.get(0);
            for (MotherboardProduct product: orig){
                int nextItemPop = product.getRatingCount();
                if (nextItemPop < popularity){
                    popularity = nextItemPop;
                    mostPopular = product;
                }
            }
            sorted.add(mostPopular);
            orig.remove(mostPopular);
        }
        return sorted;
    }

    public ArrayList<MotherboardProduct> sortName(ArrayList<MotherboardProduct> data){
        ArrayList<MotherboardProduct> sorted = new ArrayList<>();
        ArrayList<MotherboardProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            String val = orig.get(0).getProductName();
            MotherboardProduct valObj= orig.get(0);
            for (MotherboardProduct product: orig){
                String val2 = product.getProductName();
                if (val2.compareToIgnoreCase(val) < 0){
                    val = val2;
                    valObj = product;
                }
            }
            sorted.add(valObj);
            orig.remove(valObj);
        }
        return sorted;
    }

    public ArrayList<MotherboardProduct> sortPrice(ArrayList<MotherboardProduct> data){
        ArrayList<MotherboardProduct> sorted = new ArrayList<>();
        ArrayList<MotherboardProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getBestPrice();
            MotherboardProduct valObj= orig.get(0);
            for (MotherboardProduct product: orig){
                double nextItemPop = product.getBestPrice();
                if (nextItemPop < val){
                    val = nextItemPop;
                    valObj = product;
                }
            }
            sorted.add(valObj);
            orig.remove(valObj);
        }
        return sorted;
    }

    public ArrayList<MotherboardProduct> sortRating(ArrayList<MotherboardProduct> data){
        ArrayList<MotherboardProduct> sorted = new ArrayList<>();
        ArrayList<MotherboardProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getRatingAverage();
            MotherboardProduct valObj= orig.get(0);
            for (MotherboardProduct product: orig){
                double nextItemPop = product.getRatingAverage();
                if (nextItemPop < val){
                    val = nextItemPop;
                    valObj = product;
                }
            }
            sorted.add(valObj);
            orig.remove(valObj);
        }
        return sorted;
    }




    protected double stringToDouble(String x) {
        if (x == null){
            return 0;
        }
        return Double.valueOf(x.replaceAll("[^0-9.]", ""));
    }

    protected int stringToInteger(String x) {
        if (x == null){
            return 0;
        }
        return Integer.valueOf(x.replaceAll("[^0-9]", ""));
    }
}

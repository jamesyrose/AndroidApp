package pcpp_data.sorters;

import java.util.ArrayList;

import pcpp_data.queries.CpuSearch;
import pcpp_data.queries.MotherboardSearch;

public class MotherboardProductSort {

    public ArrayList<MotherboardSearch> sortPopularity(ArrayList<MotherboardSearch> data){
        ArrayList<MotherboardSearch> sorted = new ArrayList<>();
        ArrayList<MotherboardSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int popularity = orig.get(0).getRatingCount();
            System.out.println(popularity + "#####");
            MotherboardSearch mostPopular = orig.get(0);
            for (MotherboardSearch product: orig){
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

    public ArrayList<MotherboardSearch> sortName(ArrayList<MotherboardSearch> data){
        ArrayList<MotherboardSearch> sorted = new ArrayList<>();
        ArrayList<MotherboardSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            String val = orig.get(0).getProductName();
            MotherboardSearch valObj= orig.get(0);
            for (MotherboardSearch product: orig){
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

    public ArrayList<MotherboardSearch> sortPrice(ArrayList<MotherboardSearch> data){
        ArrayList<MotherboardSearch> sorted = new ArrayList<>();
        ArrayList<MotherboardSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getBestPrice();
            MotherboardSearch valObj= orig.get(0);
            for (MotherboardSearch product: orig){
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

    public ArrayList<MotherboardSearch> sortRating(ArrayList<MotherboardSearch> data){
        ArrayList<MotherboardSearch> sorted = new ArrayList<>();
        ArrayList<MotherboardSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getRatingAverage();
            MotherboardSearch valObj= orig.get(0);
            for (MotherboardSearch product: orig){
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

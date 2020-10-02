package pcpp_data.sorters;

import java.util.ArrayList;

import pcpp_data.products.MemoryProduct;

public class MemoryProductSort {

    public ArrayList<MemoryProduct> sortPopularity(ArrayList<MemoryProduct> data){
        ArrayList<MemoryProduct> sorted = new ArrayList<>();
        ArrayList<MemoryProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int popularity = orig.get(0).getRatingCount();
            System.out.println(popularity + "#####");
            MemoryProduct mostPopular = orig.get(0);
            for (MemoryProduct product: orig){
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

    public ArrayList<MemoryProduct> sortName(ArrayList<MemoryProduct> data){
        ArrayList<MemoryProduct> sorted = new ArrayList<>();
        ArrayList<MemoryProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            String val = orig.get(0).getProductName();
            MemoryProduct valObj= orig.get(0);
            for (MemoryProduct product: orig){
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

    public ArrayList<MemoryProduct> sortPrice(ArrayList<MemoryProduct> data){
        ArrayList<MemoryProduct> sorted = new ArrayList<>();
        ArrayList<MemoryProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getBestPrice();
            MemoryProduct valObj= orig.get(0);
            for (MemoryProduct product: orig){
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

    public ArrayList<MemoryProduct> sortRating(ArrayList<MemoryProduct> data){
        ArrayList<MemoryProduct> sorted = new ArrayList<>();
        ArrayList<MemoryProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getRatingAverage();
            MemoryProduct valObj= orig.get(0);
            for (MemoryProduct product: orig){
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

    public ArrayList<MemoryProduct> sortPricePerGB(ArrayList<MemoryProduct> data){
        ArrayList<MemoryProduct> sorted = new ArrayList<>();
        ArrayList<MemoryProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++) {
            String buff = orig.get(0).getPricePerGB();
            double val = 20.0;
            if (buff != null) {
                val = stringToDouble(buff);
            }
            MemoryProduct valObj = orig.get(0);
            for (MemoryProduct product : orig) {
                String nextBuff = product.getPricePerGB();
                double nextItemPop = 20.0;
                if (nextBuff != null){
                    nextItemPop = stringToDouble(nextBuff);
                }
                if (nextItemPop < val) {
                    val = nextItemPop;
                    valObj = product;
                }
            }
            sorted.add(valObj);
            orig.remove(valObj);
        }
        return sorted;
    }

    public ArrayList<MemoryProduct> sortSpeed(ArrayList<MemoryProduct> data){
        ArrayList<MemoryProduct> sorted = new ArrayList<>();
        ArrayList<MemoryProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++) {
            String buff = orig.get(0).getMemorySpeed();
            int val = 0;
            if (buff != null) {
                val = stringToInteger(buff);
            }
            MemoryProduct valObj = orig.get(0);
            for (MemoryProduct product : orig) {
                String nextBuff = product.getMemorySpeed();
                int nextItemPop = 0;
                if (nextBuff != null){
                    nextItemPop = stringToInteger(nextBuff);
                }
                if (nextItemPop < val) {
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

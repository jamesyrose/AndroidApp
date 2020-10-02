package pcpp_data.sorters;

import java.util.ArrayList;

import pcpp_data.products.CpuSearchProduct;

public class CpuProductSort {

    public ArrayList<CpuSearchProduct> sortPopularity(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int popularity = orig.get(0).getRatingCount();
            CpuSearchProduct mostPopular = orig.get(0);
            for (CpuSearchProduct product: orig){
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

    public ArrayList<CpuSearchProduct> sortName(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            String val = orig.get(0).getProductName();
            CpuSearchProduct valObj= orig.get(0);
            for (CpuSearchProduct product: orig){
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

    public ArrayList<CpuSearchProduct> sortPrice(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getBestPrice();
            CpuSearchProduct valObj= orig.get(0);
            for (CpuSearchProduct product: orig){
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

    public ArrayList<CpuSearchProduct> sortRating(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getRatingAverage();
            CpuSearchProduct valObj= orig.get(0);
            for (CpuSearchProduct product: orig){
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

    public ArrayList<CpuSearchProduct> sortCores(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int val = stringToInteger(orig.get(0).getCores());
            CpuSearchProduct valObj= orig.get(0);
            for (CpuSearchProduct product: orig){
                int nextItemPop = stringToInteger(product.getCores());
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

    public ArrayList<CpuSearchProduct> sortBaseClock(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = stringToDouble(orig.get(0).getBaseClock());
            CpuSearchProduct valObj= orig.get(0);
            for (CpuSearchProduct product: orig){
                double nextItemPop = stringToDouble(product.getBaseClock());
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

    public ArrayList<CpuSearchProduct> sortTDP(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int val = stringToInteger(orig.get(0).getTdp());
            CpuSearchProduct valObj= orig.get(0);
            for (CpuSearchProduct product: orig){
                int nextItemPop = stringToInteger(product.getTdp());
                if (nextItemPop > val){
                    val = nextItemPop;
                    valObj = product;
                }
            }
            sorted.add(valObj);
            orig.remove(valObj);
        }
        return sorted;
    }

    public ArrayList<CpuSearchProduct> sortBoostClock(ArrayList<CpuSearchProduct> data){
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        ArrayList<CpuSearchProduct> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = stringToDouble(orig.get(0).getBoostClock());
            CpuSearchProduct valObj= orig.get(0);
            for (CpuSearchProduct product: orig){
                double nextItemPop = stringToDouble(product.getBoostClock());
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

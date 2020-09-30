package pcpp_data.sorters;

import java.util.ArrayList;

import pcpp_data.queries.CpuSearch;

public class CpuProductSort {

    public ArrayList<CpuSearch> sortPopularity(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int popularity = orig.get(0).getRatingCount();
            CpuSearch mostPopular = orig.get(0);
            for (CpuSearch product: orig){
                int nextItemPop = product.getRatingCount();
                if (nextItemPop > popularity){
                    popularity = nextItemPop;
                    mostPopular = product;
                }
            }
            sorted.add(mostPopular);
            orig.remove(mostPopular);
        }
        return sorted;
    }

    public ArrayList<CpuSearch> sortName(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            String val = orig.get(0).getProductName();
            CpuSearch valObj= orig.get(0);
            for (CpuSearch product: orig){
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

    public ArrayList<CpuSearch> sortPrice(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getBestPrice();
            CpuSearch valObj= orig.get(0);
            for (CpuSearch product: orig){
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

    public ArrayList<CpuSearch> sortRating(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = orig.get(0).getRatingAverage();
            CpuSearch valObj= orig.get(0);
            for (CpuSearch product: orig){
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

    public ArrayList<CpuSearch> sortCores(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int val = stringToInteger(orig.get(0).getCores());
            CpuSearch valObj= orig.get(0);
            for (CpuSearch product: orig){
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

    public ArrayList<CpuSearch> sortBaseClock(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = stringToDouble(orig.get(0).getBaseClock());
            CpuSearch valObj= orig.get(0);
            for (CpuSearch product: orig){
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

    public ArrayList<CpuSearch> sortTDP(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            int val = stringToInteger(orig.get(0).getTdp());
            CpuSearch valObj= orig.get(0);
            for (CpuSearch product: orig){
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

    public ArrayList<CpuSearch> sortBoostClock(ArrayList<CpuSearch> data){
        ArrayList<CpuSearch> sorted = new ArrayList<>();
        ArrayList<CpuSearch> orig = data;
        int size = orig.size();
        for(int i=0; i<size; i++){
            double val = stringToDouble(orig.get(0).getBoostClock());
            CpuSearch valObj= orig.get(0);
            for (CpuSearch product: orig){
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

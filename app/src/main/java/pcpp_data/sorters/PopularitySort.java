package pcpp_data.sorters;

import java.util.ArrayList;

import pcpp_data.queries.CpuSearch;

public class PopularitySort {

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

}

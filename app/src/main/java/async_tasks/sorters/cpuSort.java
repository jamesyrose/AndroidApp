package async_tasks.sorters;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;

import async_tasks.feeds.RetrieveCpuFeedTask;
import pcpp_data.products.CpuSearchProduct;
import pcpp_data.sorters.CpuProductSort;

public class cpuSort  extends AsyncTask<String, Void, Boolean> {
    boolean amdSelected, intelSelected;
    LinearLayout dialog;
    RetrieveCpuFeedTask cpuFeed;
    int priceMin, priceMax, coreMin, coreMax, tdpMin, tdpMax;
    double baseClockMin, baseClockMax, boostClockMin, boostClockMax;
    String sortFilter;


    public cpuSort(boolean amdSelected, boolean intelSelected, LinearLayout dialog,
                   RetrieveCpuFeedTask cpuFeed, int priceMin, int priceMax, int coreMin, int coreMax,
                   int tdpMin, int tdpMax, double baseClockMin, double baseClockMax,
                   double boostClockMin, double boostClockMax, String sortFilter){
        this.amdSelected = amdSelected;
        this.intelSelected = intelSelected;
        this.dialog = dialog;
        this.cpuFeed = cpuFeed;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.coreMin = coreMin;
        this.coreMax = coreMax;
        this.tdpMin = tdpMin;
        this.tdpMax = tdpMax;
        this.baseClockMin = baseClockMin;
        this.baseClockMax = baseClockMax;
        this.boostClockMin = boostClockMin;
        this.boostClockMax = boostClockMax;
        this.sortFilter = sortFilter;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        ArrayList<String> manufacturers = new ArrayList<>();
        ArrayList<CpuSearchProduct> filtered = new ArrayList<>();

        if (amdSelected){
            manufacturers.add("AMD");
        }
        if (intelSelected){
            manufacturers.add("Intel");
        }

        dialog.removeAllViews();
        for (CpuSearchProduct product: cpuFeed.getSearchData()){
            if (manufacturers.contains(product.getManufacturer()) &&
                    priceMin < product.getBestPrice() &&
                    priceMax > product.getBestPrice()  &&
                    coreMin<= stringToValue(product.getCores()) &&
                    coreMax >= stringToValue(product.getCores()) &&
                    baseClockMin <= stringToValue(product.getBaseClock()) &&
                    baseClockMax >= stringToValue(product.getBaseClock()) &&
                    boostClockMin <= stringToValue(product.getBoostClock())  &&
                    boostClockMax >= stringToValue(product.getBoostClock()) &&
                    tdpMin <= stringToValue(product.getTdp()) &&
                    tdpMax >= stringToValue(product.getTdp())
            ){
                filtered.add(product);
            }
        }
        ArrayList<CpuSearchProduct> sorted = new ArrayList<>();
        // Sorted
        CpuProductSort sorter = new CpuProductSort();
        if (sortFilter.toLowerCase().contains("popularity")) {
            sorted = sorter.sortPopularity(filtered);
        }else if (sortFilter.toLowerCase().contains("name")){
            sorted = sorter.sortName(filtered);
        }else if (sortFilter.toLowerCase().contains("price")){
            sorted = sorter.sortPrice(filtered);
        }else if (sortFilter.toLowerCase().contains("rating")){
            sorted = sorter.sortRating(filtered);
        }else if (sortFilter.toLowerCase().contains("cores")){
            sorted = sorter.sortCores(filtered);
        }else if (sortFilter.toLowerCase().contains("base")){
            sorted = sorter.sortBaseClock(filtered);
        }else if (sortFilter.toLowerCase().contains("boost")){
            sorted = sorter.sortBoostClock(filtered);
        }else if (sortFilter.toLowerCase().contains("tdp")){
            sorted = sorter.sortTDP(filtered);
        }else {
            sorted = sorter.sortPopularity(filtered);
        }

        dialog.removeAllViews();
        dialog.setVisibility(View.GONE);
        if (sortFilter.toLowerCase().contains("descending")){
            Collections.reverse(sorted);
            for (CpuSearchProduct product: sorted){
                cpuFeed.addProduct(product);
            }
        }else {
            for (CpuSearchProduct product: sorted){
                cpuFeed.addProduct(product);
            }
        }
        return true;
    }

    private Double stringToValue(String val){
        if (val != null){
            String value = val.replaceAll("[^0-9.]", "");
            if (value == ""){
                return 0.0;
            }
            return Double.valueOf(value);
        }
        return 0.0;
    }

}

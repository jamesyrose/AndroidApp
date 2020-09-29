package preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import pcpp_data.conn.Conn;

public class Preferences {
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    
    public Preferences(Context context){
        settings = context.getSharedPreferences("UserInfo", 0);
        editor = settings.edit();
    }

    public void updateTaxRate(double taxPercentage){
        double value = checkTaxRate(taxPercentage);
        if (value >= 0){
            editor.putString("TaxRate", String.valueOf(taxPercentage)).commit();
        }
        editor.commit();
    }

    public void updateTaxRate(String taxPercentage){
        try{
            double tax = Double.valueOf(taxPercentage);
            double value = checkTaxRate(tax);
            if (value >= 0){
                editor.putString("TaxRate", String.valueOf(taxPercentage)).commit();
            }
            editor.commit();
        }catch (Exception e){
            System.out.println("TaxRate Failed conversion");
        }
    }

    public void updateCurrency(String currency){
        editor.putString("Currency", currency);
        editor.commit();
        setCurrencySymbol(currency);
    }

    public double getCurrencyRate() throws ParseException, JSONException, IOException {
        String ticker = settings.getString("Ticker", "USD");
        if (!ticker.equals("USD")){
            String url = "https://pcpp.verlet.io/currency.php?currency=" + ticker;
            Conn conn = new Conn();
            JSONArray rate = conn.getData(url);
            JSONObject obj = (JSONObject) rate.get(1);
            return Double.valueOf((String) obj.get("Rate"));
        }
        return 1.0;
    }

    public String getCurrencySymbol(){
        return settings.getString("CurrencySymbol", "$");
    }

    public String getCurrency(){
        return settings.getString("Currency", "");
    }

    public String getTaxRateString(){
        return settings.getString("TaxRate", "0.0");
    }

    public double getTaxRate(){
        String taxRate = settings.getString("TaxRate", "0.0");
        try {
            double rate = Double.valueOf(taxRate);
            return rate;
        }catch (Exception e){
            return 0.0;
        }
    }

    private void setCurrencySymbol(String currency){
        if (currency.contains("USD")){
            editor.putString("CurrencySymbol", "$");
            editor.putString("Ticker", "USD");
        }else if (currency.contains("EUR")){
            editor.putString("CurrencySymbol", "\u20AC");
            editor.putString("Ticker", "EUR");
        }else if (currency.contains("AUD")){
            editor.putString("CurrencySymbol", "A$");
            editor.putString("Ticker", "AUD");
        }else if (currency.contains("CHF")){
            editor.putString("CurrencySymbol", "\u20A3");
            editor.putString("Ticker", "CHF");
        }else if (currency.contains("JPY")){
            editor.putString("CurrencySymbol", "\u00A5");
            editor.putString("Ticker", "JPY");
        }else if (currency.contains("GBP")){
            editor.putString("CurrencySymbol", "\u00A3");
            editor.putString("Ticker", "GBP");
        }else if (currency.contains("CAD")){
            editor.putString("CurrencySymbol", "C$");
            editor.putString("Ticker", "CAD");
        }
        editor.commit();
    }

    private double checkTaxRate(double value){
        // Corrects for > 1 or less than one (given as whole vs decimal)
        try{
            if (value > 1){
                value = value / 100;
            }
            if (value >= 0){
                return value;
            }
        }catch (NumberFormatException e){
            value = 0;
        }
        return 0;
    }


}

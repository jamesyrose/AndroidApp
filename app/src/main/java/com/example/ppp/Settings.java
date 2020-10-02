package com.example.ppp;

import android.content.SharedPreferences;
import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import preferences.Preferences;

public class Settings extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    EditText taxRate;
    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        radioGroup = findViewById(R.id.currencyRadioGroup);
        taxRate = findViewById(R.id.settingTaxInput);
        pref = new Preferences(Settings.this);
        setRadioGroup();
        setTextRate();


    }

    public void setTextRate(){
        EditText textBox = findViewById(R.id.settingTaxInput);
        String taxRate = pref.getTaxRateString();
        textBox.setText(taxRate);
    }

    public void setRadioGroup(){
        String selected = pref.getCurrency();
        int count = radioGroup.getChildCount();
        for (int i=0; i<count; i++){
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton) {
                RadioButton selectedAnswer =(RadioButton)o;
                if(selectedAnswer.getText().equals(selected)) {
                    System.out.println(selectedAnswer.getText());
                    selectedAnswer.setChecked(true);
                }
            }

        }
    }


    public void returnPage(){
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateSettings(View view) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        selectedRadioButton = findViewById(selectedId);
        Editable taxPercentage = taxRate.getText();

        pref.updateCurrency(String.valueOf(selectedRadioButton.getText()));
        pref.updateTaxRate(String.valueOf(taxPercentage));
        returnPage();

    }

    private double checkTaxRate(String stringText){
        try{
            double value = Double.valueOf(stringText);
            if (value > 1){
                value = value / 100;
            }
            if (value >= 0){
                return value;
            }
        }catch (NumberFormatException e){
            int value = 0;
        }
        return -1;
    }
}
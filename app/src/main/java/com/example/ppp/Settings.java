package com.example.ppp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    EditText taxRate;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        radioGroup = findViewById(R.id.currencyRadioGroup);
        taxRate = findViewById(R.id.settingTaxInput);
        settings = getSharedPreferences("UserInfo", 0);
        editor = settings.edit();
        setRadioGroup();
        setTextRate();


    }

    public void setTextRate(){
        EditText textBox = findViewById(R.id.settingTaxInput);
        String taxRate = settings.getString("TaxRate", "");
        textBox.setText(taxRate);
    }

    public void setRadioGroup(){
        String selected = settings.getString("Currency", "");
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

        double value = checkTaxRate(String.valueOf(taxPercentage));
        if (value >= 0){
            editor.putString("TaxRate", String.valueOf(taxPercentage)).commit();
        }
        editor.putString("Currency", String.valueOf(selectedRadioButton.getText()));
        editor.commit();

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
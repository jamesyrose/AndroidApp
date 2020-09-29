package com.example.ppp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.PopupWindow;

public class product_description_popup extends Activity {
    PopupWindow popUp;
    boolean click = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popUp = new PopupWindow(this);
        setContentView(R.layout.product_description_popup);
    }
}

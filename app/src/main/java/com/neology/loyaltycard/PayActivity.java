package com.neology.loyaltycard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.neology.loyaltycard.utils.Constants;

public class PayActivity extends AppCompatActivity {
    String saldo = "";
    TextView saldoTxt;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Intent intent = getIntent();
//        saldo = intent.getStringExtra("cardInfo");
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        saldo = sharedPreferences.getString(Constants.KEY_SALDO, null);
        initElements();
    }

    private void initElements() {
        saldoTxt = (TextView) findViewById(R.id.saldoId);
        saldoTxt.setText(saldo);
    }
}

package com.neology.loyaltycard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.neology.loyaltycard.model.CardInfo;
import com.neology.loyaltycard.model.Product;
import com.neology.loyaltycard.utils.Constants;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {

    CardInfo cardInfo;
    ArrayList<Product> productArrayList;
    TextView cardId;
    TextView clientId;
    TextView availableBalance;
    TextView fuelType;
    TextView points;
    TextView expirationDate;
    TextView creationDate;
    FloatingActionButton mapBtn, searchBtn, reloadBtn;
    Toolbar toolBar;

    String[] productList = {"Formula Shell Super y Común 1lt",
                            "Shell V-Power Nitro+ Nafta 1lt",
                            "Shell V-Power Nitro+ Diesel 1lt",
                            "Shell Gasoil 1500 1lt",
                            "Shell Fórmula Diesel 500 1lt"};
    String[] price = {"15 puntos", "15 puntos", "14 puntos", "15 puntos", "13 puntos"};
    String[] url = {
            "http://s01.static-shell.com/content/dam/shell-new/local/country/arg/downloads/pdf/datasheets/TDSArgentinaFormulaShell2013conS300supertemporal.pdf",
            "http://s01.static-shell.com/content/dam/shell-new/local/country/arg/downloads/pdf/datasheets/tdsvpowernitroplusnafta.pdf",
            "http://s03.static-shell.com/content/dam/shell-new/local/country/arg/downloads/pdf/datasheets/2014/TDS-Argentina-VPower-Diesel-R1104-2014.pdf",
            "http://s03.static-shell.com/content/dam/shell-new/local/country/arg/downloads/pdf/datasheets/2014/TDS-Argentina-Gasoil-1500-R14-04-2014.pdf",
            "http://s05.static-shell.com/content/dam/shell-new/local/country/arg/downloads/pdf/datasheets/2014/TDS-Argentina-Formula-Diesel-500-R4-04-2014.pdf"};

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Intent intent = getIntent();
        cardInfo = intent.getParcelableExtra("cardInfo");
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        setSupportBar();
        initElements();
    }

    private void setSupportBar() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("");
        toolBar.setLogo(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unnamed));
        setSupportActionBar(toolBar);
    }

    private void initElements() {
        cardId = (TextView) findViewById(R.id.cardId);
        cardId.setText(cardInfo.getCardId());

        clientId = (TextView) findViewById(R.id.clientId);
        clientId.setText(cardInfo.getClientId());

        availableBalance = (TextView) findViewById(R.id.availableBalanceID);
        availableBalance.setText(sharedPreferences.getString(Constants.KEY_SALDO, null)+"0");

        fuelType = (TextView) findViewById(R.id.fuelType);
        fuelType.setText(cardInfo.getFuelType());

        points = (TextView) findViewById(R.id.pointsId);
        points.setText(String.valueOf(cardInfo.getPoints()));

        creationDate = (TextView) findViewById(R.id.creationDateId);
        creationDate.setText(cardInfo.getCreationDate());

        expirationDate = (TextView) findViewById(R.id.expirationDateId);
        expirationDate.setText(cardInfo.getExpirationDate());

        mapBtn = (FloatingActionButton) findViewById(R.id.mapsBtnID);
        showMap();

        searchBtn = (FloatingActionButton) findViewById(R.id.pointsID);
        searchPoints();

        reloadBtn = (FloatingActionButton) findViewById(R.id.reloadID);
        reloadMoney();
    }

    private void showMap() {
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });
    }

    private void searchPoints() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productArrayList = new ArrayList<Product>();
                for (int i = 0; i < productList.length; i++) {
                    Product product = new Product(productList[i], price[i], url[i]);
                    productArrayList.add(product);
                }
                Intent intent = new Intent(getApplicationContext(), PointsActivity.class);
                intent.putExtra("cardInfo", cardInfo);
                intent.putParcelableArrayListExtra("productArrayList", productArrayList);
                startActivity(intent);
            }
        });
    }

    private void reloadMoney() {
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreReloadActivity.class);
                intent.putExtra("cardInfo", cardInfo);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        availableBalance.setText(sharedPreferences.getString(Constants.KEY_SALDO, null)+"0");
    }
}

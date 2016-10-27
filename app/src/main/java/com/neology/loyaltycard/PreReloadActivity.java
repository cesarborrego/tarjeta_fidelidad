package com.neology.loyaltycard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.neology.loyaltycard.dialogs.PreReloadDlg;
import com.neology.loyaltycard.model.CardInfo;
import com.neology.loyaltycard.model.Reload;

import java.util.ArrayList;

public class PreReloadActivity extends AppCompatActivity {
    LinearLayout gas, parking, peaje;
    CardInfo cardInfo;
    Button recargarBtn;
    private View.OnClickListener payDlgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDlg();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_reload);
        Intent intent = getIntent();
        cardInfo = intent.getParcelableExtra("cardInfo");
        initELements();
        setSupportBar();
    }

    private void initELements() {
        gas = (LinearLayout) findViewById(R.id.gasId);
//        gas.setOnClickListener(payDlgListener);
        parking = (LinearLayout) findViewById(R.id.parkingId);
//        parking.setOnClickListener(payDlgListener);
        peaje = (LinearLayout) findViewById(R.id.peajeID);
//        peaje.setOnClickListener(payDlgListener);
        recargarBtn = (Button) findViewById(R.id.recargarBtnID);
        recargarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReload();
            }
        });
    }

    private void setSupportBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("");
        toolBar.setLogo(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unnamed));
        setSupportActionBar(toolBar);
    }

    private void showDlg() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = PreReloadDlg.newInstance(cardInfo.getAvailableBalance());
        newFragment.show(ft, "dialog");
    }

    public void openPay() {
        Intent intent = new Intent(getApplicationContext(), PayActivity.class);
        intent.putExtra("cardInfo", cardInfo.getAvailableBalance());
        startActivity(intent);
    }

    public void openReload() {
        ArrayList<Reload> reloadArrayList = new ArrayList<Reload>();
        String[] reload = {
                "30",
                "50",
                "70",
                "100",
                "130",
                "150",
                "170",
                "200",};
        Intent intent = new Intent(getApplicationContext(), ReloadActivity.class);
        for (String r : reload) {
            Reload reload1 = new Reload(r);
            reloadArrayList.add(reload1);
        }
        intent.putParcelableArrayListExtra("reload", reloadArrayList);
        intent.putExtra("cardInfo", cardInfo.getAvailableBalance());
        startActivity(intent);
    }
}

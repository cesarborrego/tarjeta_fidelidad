package com.neology.loyaltycard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neology.loyaltycard.adapters.ReloadAdapter;
import com.neology.loyaltycard.dialogs.ReloadDlg;
import com.neology.loyaltycard.model.CardInfo;
import com.neology.loyaltycard.model.Reload;
import com.neology.loyaltycard.utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReloadActivity extends AppCompatActivity {

    ArrayList<Reload> reloadArrayList;
    ReloadAdapter reloadAdapter;
    LinearLayout panelRecarga;
    RecyclerView recyclerView;
    TextView saldoTxt, recargarTxt;
    String saldo;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);
        reloadArrayList = getIntent().getParcelableArrayListExtra("reload");
//        saldo = getIntent().getStringExtra("cardInfo");
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        saldo = sharedPreferences.getString(Constants.KEY_SALDO, null)+"0";
//        setSupportBar();
        createRecycler();
        initElements();
    }

    private void createRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerGeneralID);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        reloadAdapter = new ReloadAdapter(reloadArrayList, getSupportFragmentManager());
        recyclerView.setAdapter(reloadAdapter);
    }

//    private void setSupportBar() {
//        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
//        toolBar.setTitle("");
//        toolBar.setLogo(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unnamed));
//        setSupportActionBar(toolBar);
//    }

    private void initElements() {
        recargarTxt = (TextView) findViewById(R.id.recargarTxt);

        saldoTxt = (TextView) findViewById(R.id.saldoRecargaId);
        saldoTxt.setText(saldo);

        panelRecarga = (LinearLayout)findViewById(R.id.panelRecargaID);
        panelRecarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recyclerView.setVisibility(View.VISIBLE);
//                panelRecarga.setVisibility(View.GONE);
                if (recargarTxt.getText().toString().length()>0) {
                    showDialog(recargarTxt.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Ingrese una cantidad", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void recarga(String s) {
        String saldoInicial = saldoTxt.getText().toString().substring(1);
        double saldoTotal = Double.parseDouble(saldoInicial)+Double.parseDouble(s);
        saldoTxt.setText("$"+String.valueOf(saldoTotal)+"0");
        editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_SALDO, "$" + String.valueOf(saldoTotal));
        editor.commit();
        recargarTxt.setText("");
    }

    void showDialog(String r) {
        DialogFragment dialogFragment = ReloadDlg.newInstance(r);
        dialogFragment.show(getSupportFragmentManager(), "d");
    }
}

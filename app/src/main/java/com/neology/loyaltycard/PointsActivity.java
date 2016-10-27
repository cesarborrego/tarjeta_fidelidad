package com.neology.loyaltycard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.neology.loyaltycard.adapters.ProductAdapter;
import com.neology.loyaltycard.barcode.IntentIntegrator;
import com.neology.loyaltycard.barcode.IntentResult;
import com.neology.loyaltycard.dialogs.PointsDlg;
import com.neology.loyaltycard.model.CardInfo;
import com.neology.loyaltycard.model.Product;

import java.util.ArrayList;

public class PointsActivity extends AppCompatActivity {
    CardInfo cardInfo;
    ArrayList<Product> productArrayList;
    private Toolbar toolBar;

    TextView pointsTxt, userTxt;
    FloatingActionButton fab;

    IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        Intent intent = getIntent();
        intentIntegrator = new IntentIntegrator(PointsActivity.this);
        cardInfo = intent.getParcelableExtra("cardInfo");
        productArrayList = intent.getParcelableArrayListExtra("productArrayList");
        initElements();
        setSupportBar();
        createRecycler();
    }

    private void initElements() {
        pointsTxt = (TextView) findViewById(R.id.mPointsID);
        pointsTxt.setText(String.valueOf(cardInfo.getPoints()));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentIntegrator.initiateScan();
            }
        });
    }

    private void createRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerPointsID);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        ProductAdapter productAdapter = new ProductAdapter(productArrayList, this);
        recyclerView.setAdapter(productAdapter);
    }

    private void setSupportBar() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("");
        toolBar.setLogo(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unnamed));
        setSupportActionBar(toolBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            String info = intentResult.getContents();
            showDialog_Login(info);
        }
    }

    void showDialog_Login(String pts) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = PointsDlg.newInstance(pts);
        newFragment.show(ft, "dialog");
    }

    public void okPoinstDlg(String points) {
        int totalPoints = Integer.parseInt(points) + cardInfo.getPoints();
        pointsTxt.setText(String.valueOf(totalPoints));
    }
}

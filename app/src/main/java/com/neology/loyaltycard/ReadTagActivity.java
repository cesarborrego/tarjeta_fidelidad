package com.neology.loyaltycard;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.neology.loyaltycard.lecturanfc.LecturaTag;
import com.neology.loyaltycard.model.CardInfo;
import com.neology.loyaltycard.utils.Constants;

public class ReadTagActivity extends AppCompatActivity {

    public static final String TAG = ReadTagActivity.class.getSimpleName();
    protected String[] datosTag;
    NfcAdapter nfcAdapter = null;
    PendingIntent pendingIntent = null;
    IntentFilter[] filters = null;
    String[][] techList = null;
    Intent lecturaIntent;
    private Toolbar toolBar;
    private TextView tvUI;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_tag);
        setSupportBar();
        loadWebView();

        // Obtenemos el control sobre el lector de NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // Si no se encuentra el lector de NFC se cierra aplicacion
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.errornfc),
                    Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        // Creamos un Intent para manejar los datos leidos
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Creamos un filtro de Intent relacionado con descubrir un mensaje NDEF
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter discovery = new IntentFilter(
                NfcAdapter.ACTION_TAG_DISCOVERED);

        // Configuramos el filtro para que acepte de cualquier tipo de NDEF
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        filters = new IntentFilter[]{ndef, discovery};

        // Configuramos para que lea de cualquier clase de tag NFC
        techList = new String[][]{new String[]{NfcF.class.getName()}};

        Intent lecturaIntent = getIntent();
        Log.d(TAG, "INTENT " + lecturaIntent.toString());
        Log.d(TAG, "PENDING INTENT " + pendingIntent.toString());
        Parcelable[] rawMsgs = lecturaIntent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        Log.d(TAG, "RAW " + rawMsgs);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(lecturaIntent.getAction())) {
            lecturaNFC(lecturaIntent);
        }

    }

    public void lecturaNFC(Intent intent) {
        try {

            LecturaTag lecturaObj = new LecturaTag(intent);
            datosTag = lecturaObj.lectura();

            if (datosTag != null) {

                Log.d(TAG, "# Datos leidos: " + datosTag.length);
                int i = 0;
                while (i < datosTag.length) {
                    Log.d(TAG, "Dato[" + i + "]: " + datosTag[i]);
                    i++;
                }
//                NFC::decryptedData: A-0000001|103000112|10000|860|15-01-2016|15-01-2018|4
                CardInfo cardInfo = new CardInfo(
                        datosTag[0],
                        datosTag[1],
                        datosTag[2],
                        Integer.parseInt(datosTag[3]),
                        datosTag[4],
                        datosTag[5],
                        datosTag[6]);
                sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_SALDO, datosTag[2].substring(0, datosTag[2].length()-1));
                editor.commit();
                Intent intentMain = new Intent(getApplicationContext(), DataActivity.class);
                intentMain.putExtra("cardInfo", cardInfo);
                startActivity(intentMain);

            } else {
                Log.i(TAG, "Lectura de datos no validos");
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepion::lecturaNFC->" + e.getMessage());
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_read_tag, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void setSupportBar() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("");
        toolBar.setLogo(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unnamed));
        setSupportActionBar(toolBar);
    }

    private void loadWebView() {
        WebView wv = (WebView) findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("file:///android_asset/tap.html");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d(TAG, "onResume()");
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters,
                techList);
        lecturaNFC(lecturaIntent);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d(TAG, "onPause()");
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent()");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        lecturaNFC(intent);
    }
}
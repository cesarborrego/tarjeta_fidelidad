package com.neology.loyaltycard.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.neology.loyaltycard.PreReloadActivity;
import com.neology.loyaltycard.R;

/**
 * Created by root on 26/01/16.
 */
public class PreReloadDlg extends DialogFragment {

    LinearLayout pagar, recargar;
    String saldo = "";
    private View.OnClickListener pagarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((PreReloadActivity) getActivity()).openPay();
            dismiss();
        }
    };
    private View.OnClickListener recargarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((PreReloadActivity) getActivity()).openReload();
            dismiss();
        }
    };

    public static PreReloadDlg newInstance(String saldo) {
        PreReloadDlg reloadDlg = new PreReloadDlg();
        Bundle bundle = new Bundle();
        bundle.putString("cardInfo", saldo);
        reloadDlg.setArguments(bundle);
        return reloadDlg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saldo = getArguments().getString("cardInfo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.pay, container, false);
        pagar = (LinearLayout) v.findViewById(R.id.pagarId);
        pagar.setOnClickListener(pagarListener);
        recargar = (LinearLayout) v.findViewById(R.id.recargarId);
        recargar.setOnClickListener(recargarListener);
        return v;
    }
}

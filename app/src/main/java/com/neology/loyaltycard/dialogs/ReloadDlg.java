package com.neology.loyaltycard.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;

import com.neology.loyaltycard.R;
import com.neology.loyaltycard.ReloadActivity;

/**
 * Created by root on 26/01/16.
 */
public class ReloadDlg extends DialogFragment {

    public static ReloadDlg newInstance(String reload) {
        ReloadDlg reloadDlg = new ReloadDlg();
        Bundle bundle = new Bundle();
        bundle.putString("reload", reload);
        reloadDlg.setArguments(bundle);
        return reloadDlg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String reload = getArguments().getString("reload");
        return new AlertDialog.Builder(getActivity())
                .setTitle(" ")
                .setIcon(ContextCompat.getDrawable(getContext(), R.drawable.unnamed))
                .setMessage("¿DESEAS RECARGAR $" + reload + ".00 ?")
                .setPositiveButton("ACEPTAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ReloadActivity)getActivity()).recarga(reload);
                                dismiss();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        }).create();
    }
}

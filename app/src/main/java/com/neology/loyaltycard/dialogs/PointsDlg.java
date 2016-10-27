package com.neology.loyaltycard.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neology.loyaltycard.PointsActivity;
import com.neology.loyaltycard.R;

/**
 * Created by root on 25/01/16.
 */
public class PointsDlg extends DialogFragment {
    String points = "";
    TextView pts, ticket, Ok;

    public static PointsDlg newInstance(String points) {
        PointsDlg pointsDlg = new PointsDlg();
        Bundle bundle = new Bundle();
        bundle.putString("points", points);
        pointsDlg.setArguments(bundle);
        return pointsDlg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        points = getArguments().getString("points");
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Settings);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_points, container, false);
        pts = (TextView) v.findViewById(R.id.totalPointsId);
        pts.setText("15");
        ticket = (TextView) v.findViewById(R.id.ticketId);
        ticket.setText(points);
        Ok = (TextView) v.findViewById(R.id.OKID);
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PointsActivity) getActivity()).okPoinstDlg("15");
                dismiss();
            }
        });
        return v;
    }
}

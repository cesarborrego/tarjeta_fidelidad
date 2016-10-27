package com.neology.loyaltycard.adapters;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neology.loyaltycard.R;
import com.neology.loyaltycard.dialogs.ReloadDlg;
import com.neology.loyaltycard.model.Reload;

import java.util.ArrayList;

/**
 * Created by root on 26/01/16.
 */
public class ReloadAdapter extends RecyclerView.Adapter<ReloadAdapter.Reload_ViewHolder> {
    ArrayList<Reload> reloadArrayList;
    FragmentManager f;
    Reload reload;

    public ReloadAdapter(ArrayList<Reload> reloadArrayList, FragmentManager f) {
        this.reloadArrayList = reloadArrayList;
        this.f = f;
    }

    @Override
    public Reload_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reload, parent, false);
        return new Reload_ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Reload_ViewHolder holder, final int position) {
        reload = reloadArrayList.get(position);
        holder.reload.setText(reload.getReload());
        holder.layoutReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(reloadArrayList.get(position).getReload());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reloadArrayList.size();
    }

    void showDialog(String r) {
        DialogFragment dialogFragment = ReloadDlg.newInstance(r);
        dialogFragment.show(f, "d");
    }

    public static class Reload_ViewHolder extends RecyclerView.ViewHolder {
        public TextView reload;
        public LinearLayout layoutReload;

        public Reload_ViewHolder(View itemView) {
            super(itemView);
            reload = (TextView) itemView.findViewById(R.id.dataRow);
            layoutReload = (LinearLayout) itemView.findViewById(R.id.linearReloadID);
        }
    }
}

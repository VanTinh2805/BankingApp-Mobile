package com.example.proiectmobilebanking.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proiectmobilebanking.R;
import com.example.proiectmobilebanking.TranzactionJson;

import java.util.List;

public class AdapterTranzactieJson extends ArrayAdapter<TranzactionJson> {
    private int resource;
    private List<TranzactionJson> tranzactions;
    private LayoutInflater inflater;


    public AdapterTranzactieJson(@NonNull Context context, int resource, @NonNull List<TranzactionJson> tranzactions, LayoutInflater layout) {
        super(context, resource, tranzactions);
        this.resource = resource;
        this.tranzactions = tranzactions;
        this.inflater = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
        }

        TranzactionJson tranzaction = tranzactions.get(position);

        TextView tvBenef = convertView.findViewById(R.id.tv_benef);
        TextView tvAccount = convertView.findViewById(R.id.tv_account);
        TextView tvAmount = convertView.findViewById(R.id.tv_amount);
        TextView tvStatus = convertView.findViewById(R.id.tv_status);

        if (tranzaction != null) {
            tvBenef.setText(tranzaction.getBeneficiaryName());
            tvAccount.setText(tranzaction.getAccountNumber());
            tvAmount.setText(tranzaction.getAmount() != null ? String.valueOf(tranzaction.getAmount()) : "0");
            tvStatus.setText(tranzaction.getStatus());
        }

        return convertView;
    }
}

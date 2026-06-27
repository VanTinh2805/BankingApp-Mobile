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
import com.example.proiectmobilebanking.network.model.TransitionResponse;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class AdapterTransitionResponse extends ArrayAdapter<TransitionResponse> {
    private final int resource;
    private final List<TransitionResponse> transitions;
    private final LayoutInflater layout;
    private final DecimalFormat amountFormat;

    public AdapterTransitionResponse(@NonNull Context context, int resource, @NonNull List<TransitionResponse> transitions, LayoutInflater layout) {
        super(context, resource, transitions);
        this.resource = resource;
        this.transitions = transitions;
        this.layout = layout;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        this.amountFormat = new DecimalFormat("#,##0.##", symbols);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layout.inflate(resource, parent, false);
        }

        TransitionResponse transition = transitions.get(position);

        TextView tvBenef = view.findViewById(R.id.tv_benef);
        TextView tvAccount = view.findViewById(R.id.tv_account);
        TextView tvAmount = view.findViewById(R.id.tv_amount);
        TextView tvStatus = view.findViewById(R.id.tv_status);

        if (transition == null) {
            tvBenef.setText("To: -");
            tvAccount.setText("From: -");
            tvAmount.setText("0");
            tvStatus.setText("Date: -");
            return view;
        }

        tvBenef.setText("To: " + valueOrDash(transition.getToUser()));
        tvAccount.setText("From: " + valueOrDash(transition.getFromUser()));
        tvAmount.setText(formatAmount(transition.getAmount()));
        tvStatus.setText("Date: " + valueOrDash(transition.getCreated()));
        return view;
    }

    private String formatAmount(Double amount) {
        if (amount == null) {
            return "0";
        }
        return amountFormat.format(amount);
    }

    private String valueOrDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }
}

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

import java.util.List;

public class AdapterTransitionResponse extends ArrayAdapter<TransitionResponse> {
    private int resource;
    private List<TransitionResponse> transitions;
    private LayoutInflater inflater;

    public AdapterTransitionResponse(@NonNull Context context, int resource, @NonNull List<TransitionResponse> transitions, LayoutInflater layout) {
        super(context, resource, transitions);
        this.resource = resource;
        this.transitions = transitions;
        this.inflater = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
        }

        TransitionResponse transition = transitions.get(position);

        TextView tvBenef = convertView.findViewById(R.id.tv_benef);
        TextView tvAccount = convertView.findViewById(R.id.tv_account);
        TextView tvAmount = convertView.findViewById(R.id.tv_amount);
        TextView tvStatus = convertView.findViewById(R.id.tv_status);

        if (transition != null) {
            tvBenef.setText(transition.getToUser() != null ? transition.getToUser() : "N/A");
            tvAccount.setText(transition.getFromUser() != null ? transition.getFromUser() : "N/A");
            tvAmount.setText(transition.getAmount() != null ? String.valueOf(transition.getAmount()) : "0.0");
            tvStatus.setText(transition.getCreated() != null ? transition.getCreated() : "");
        }

        return convertView;
    }
}

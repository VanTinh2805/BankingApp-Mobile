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
    private LayoutInflater layout;

    public AdapterTransitionResponse(@NonNull Context context, int resource, @NonNull List<TransitionResponse> transitions, LayoutInflater layout) {
        super(context, resource, transitions);
        this.resource = resource;
        this.transitions = transitions;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layout.inflate(resource, parent, false);
        TransitionResponse transition = transitions.get(position);

        TextView tvBenef = view.findViewById(R.id.tv_benef);
        TextView tvAccount = view.findViewById(R.id.tv_account);
        TextView tvAmount = view.findViewById(R.id.tv_amount);
        TextView tvStatus = view.findViewById(R.id.tv_status);

        tvBenef.setText(transition.getToUser());
        tvAccount.setText(transition.getFromUser());
        tvAmount.setText(String.valueOf(transition.getAmount()));
        tvStatus.setText(transition.getCreated());
        return view;
    }
}

package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.UserInfo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutFragment extends Fragment {
    Button btnMaps;
    TextView tvName;
    TextView tvEmail;
    TextView tvCard;
    TextView tvBank;
    TextView tvBalance;
    SharedPreferencesUser preferences;
    private ApiService apiService;
    private DecimalFormat balanceFormat;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        preferences = new SharedPreferencesUser(requireContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        balanceFormat = new DecimalFormat("#,##0.##", new DecimalFormatSymbols(Locale.US));
        initComponents(view);
        bindCachedProfile();
        loadProfile();
        return view;
    }

    private void initComponents(View view) {
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvCard = view.findViewById(R.id.tvProfileCard);
        tvBank = view.findViewById(R.id.tvProfileBank);
        tvBalance = view.findViewById(R.id.tvProfileBalance);
        btnMaps = view.findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void bindCachedProfile() {
        tvName.setText("Name: " + valueOrDash(preferences.getName()));
        tvEmail.setText("Email: " + valueOrDash(preferences.getEmail()));
        tvCard.setText("Card: " + valueOrDash(preferences.getCardNumber()));
        tvBank.setText("Bank: " + valueOrDash(preferences.getBank()));
        tvBalance.setText("Balance: " + valueOrDash(preferences.getBalance()));
    }

    private void loadProfile() {
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        apiService.getCurrentUser(authorization).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    preferences.saveUserInfo(response.body());
                    bindProfile(response.body());
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getContext(), "Khong lay duoc thong tin nguoi dung", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Toast.makeText(getContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void bindProfile(UserInfo userInfo) {
        tvName.setText("Name: " + valueOrDash(userInfo.getName()));
        tvEmail.setText("Email: " + valueOrDash(userInfo.getEmail()));
        tvCard.setText("Card: " + valueOrDash(userInfo.getCardNumber()));
        tvBank.setText("Bank: " + valueOrDash(userInfo.getBank()));
        tvBalance.setText("Balance: " + formatBalance(userInfo.getBalance()));
    }

    private String formatBalance(Double balance) {
        if (balance == null) {
            return "0";
        }
        return balanceFormat.format(balance);
    }

    private String valueOrDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }

    private void goToLogin() {
        if (getContext() == null) {
            return;
        }
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}

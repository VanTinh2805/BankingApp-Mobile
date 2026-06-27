package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.UserInfo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentBalanceActivity extends AppCompatActivity {
    TextView currentBalance;
    SharedPreferencesUser preferences;
    Button btnRefresh;
    private ApiService apiService;
    private DecimalFormat balanceFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_balance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentBalance = findViewById(R.id.sold2);
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        balanceFormat = new DecimalFormat("#,##0.##", new DecimalFormatSymbols(Locale.US));
        initComponents();
        loadCurrentBalance();
    }

    private void initComponents() {
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCurrentBalance();
            }
        });
    }

    private void loadCurrentBalance() {
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        btnRefresh.setEnabled(false);
        apiService.getCurrentUser(authorization).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                btnRefresh.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    preferences.saveUserInfo(response.body());
                    currentBalance.setText(formatBalance(response.body().getBalance()));
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc so du", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                btnRefresh.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private String formatBalance(Double balance) {
        if (balance == null) {
            return "0";
        }
        return balanceFormat.format(balance);
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

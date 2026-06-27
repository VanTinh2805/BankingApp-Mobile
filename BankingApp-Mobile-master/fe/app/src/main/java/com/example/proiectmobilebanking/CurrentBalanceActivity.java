package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.UserInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentBalanceActivity extends AppCompatActivity {
TextView currentBalance;
SharedPreferencesUser preferences;
Button btnRefresh;
private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_balance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentBalance=findViewById(R.id.sold2);
        preferences=new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        initComponents();
        loadCurrentBalance();
    }

    private void initComponents(){
        btnRefresh=findViewById(R.id.btnRefresh);

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

        apiService.getCurrentUser(authorization).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBalance.setText(String.valueOf(response.body().getBalance()));
                } else {
                    preferences.isLogged(false);
                    goToLogin();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;

import com.example.proiectmobilebanking.Chart.ChartActivity;
import com.example.proiectmobilebanking.Raports.RaportsActivity;
import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.UserInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectActivity extends AppCompatActivity {
    Button btnInfo;
    Button btnCurrentBal;
//    Button btnTranzactions;
    Button btnHistory;
    Button btnReceivedTransactions;
    FloatingActionButton fabChart;
    public static final int code = 230;
    public static final String ADD_TRANZACTION_HISTORY = "addTranzactionH";
    SharedPreferencesUser preferences;
    private ApiService apiService;
    Button btnDeleteAccount;
    Button btnRaport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences=new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadCurrentUser();
        initComponents();
    }

    private void loadCurrentUser() {
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
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc thong tin nguoi dung", Toast.LENGTH_LONG).show();
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

    public void initComponents() {
        btnInfo = findViewById(R.id.btnInfos);
        btnCurrentBal = findViewById(R.id.btnCurentBal);
        //btnTranzactions = findViewById(R.id.btnTransactions);
        btnHistory = findViewById(R.id.btnHistory);
        btnDeleteAccount=findViewById(R.id.btnDeleteAccount);
        btnRaport=findViewById(R.id.btnRaport);
        fabChart=findViewById(R.id.fabChart);
        btnReceivedTransactions=findViewById(R.id.btnReceivedTransactions);

        btnReceivedTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReceivedTransactionActivity.class);
                startActivity(intent);
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        btnCurrentBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getApplicationContext(), CurrentBalanceActivity.class);
                startActivity(intent3);
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getApplicationContext(), HistoryTransactionsActivity.class);
                startActivity(intent4);
            }
        });
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.clearSession();
                goToLogin();
            }
        });
        btnRaport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), RaportsActivity.class);
                startActivity(intent);
            }
        });
        fabChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ChartActivity.class);
                startActivity(intent);
            }
        });
    }

    }

package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransitionResponse;
import com.example.proiectmobilebanking.util.AdapterTransitionResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceivedTransactionActivity extends AppCompatActivity {
    Button btnReceivedTranz;
    ListView lvReceived;
    private SharedPreferencesUser preferences;
    private ApiService apiService;
    private List<TransitionResponse> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        addInListView();
        initComponents();
        loadReceivedTransactions();
    }

    private void initComponents() {
        btnReceivedTranz = findViewById(R.id.btnReceivedTransactions);
        btnReceivedTranz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadReceivedTransactions();
            }
        });
    }

    private void loadReceivedTransactions() {
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        apiService.getReceivedTransitions(authorization).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactions.clear();
                    transactions.addAll(response.body());
                    AdapterTransitionResponse adapter = (AdapterTransitionResponse) lvReceived.getAdapter();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), R.string.moneyReceived, Toast.LENGTH_LONG).show();
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc giao dich nhan", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void addInListView() {
        lvReceived = findViewById(R.id.lv_receivedTranzaction);
        lvReceived.invalidate();
        AdapterTransitionResponse adapter = new AdapterTransitionResponse(getApplicationContext(),
                R.layout.tranzaction_row, transactions, getLayoutInflater());
        lvReceived.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

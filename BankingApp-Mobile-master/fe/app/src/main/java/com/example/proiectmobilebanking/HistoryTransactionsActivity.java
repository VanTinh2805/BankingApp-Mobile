package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransitionResponse;
import com.example.proiectmobilebanking.util.AdapterTransitionResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryTransactionsActivity extends AppCompatActivity {
    private ListView lvHistoryTranz;
    private List<TransitionResponse> transactions = new ArrayList<>();
    public static final int code = 230;
    FloatingActionButton fabAdd;
    SharedPreferencesUser preferences;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvHistoryTranz = findViewById(R.id.lvHistory);
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);

        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), MoneyActivity.class);
                startActivityForResult(it, code);
            }
        });

        loadTransitions();
    }

    private void loadTransitions() {
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        apiService.getCurrentTransitions(authorization).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactions.clear();
                    transactions.addAll(response.body());
                    AdapterTransitionResponse adapter = new AdapterTransitionResponse(getApplicationContext(),
                            R.layout.tranzaction_row, transactions, getLayoutInflater());
                    lvHistoryTranz.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc lich su giao dich", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == code) {
            loadTransitions();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

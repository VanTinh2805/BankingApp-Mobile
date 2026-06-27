package com.example.proiectmobilebanking.Raports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.LoginActivity;
import com.example.proiectmobilebanking.R;
import com.example.proiectmobilebanking.SharedPreferencesUser;
import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransitionResponse;
import com.example.proiectmobilebanking.util.AdapterTransitionResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaportAmount extends AppCompatActivity {
    EditText editRaportAmount;
    Button btnViewRaportAmount;
    SharedPreferencesUser preferences;
    ListView lvRaportAmount;
    Button btnRaportToTxt;
    List<TransitionResponse> transitions = new ArrayList<>();
    private ApiService apiService;
    private AdapterTransitionResponse adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raport_amount);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editRaportAmount = findViewById(R.id.etRaportAmount);
        btnViewRaportAmount = findViewById(R.id.btnViewRaportAmount);
        lvRaportAmount = findViewById(R.id.lvRaportAmount);
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        btnRaportToTxt = findViewById(R.id.btnRaportToTxt);
        adapter = new AdapterTransitionResponse(getApplicationContext(), R.layout.tranzaction_row, transitions, getLayoutInflater());
        lvRaportAmount.setAdapter(adapter);

        btnRaportToTxt.setEnabled(false);
        btnViewRaportAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadReport();
            }
        });
        btnRaportToTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRaportToTxt();
            }
        });
    }

    private void loadReport() {
        final double minAmount;
        try {
            minAmount = Double.parseDouble(editRaportAmount.getText().toString().trim());
            if (minAmount < 0) {
                editRaportAmount.setError(getString(R.string.error_amount));
                editRaportAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editRaportAmount.setError(getString(R.string.error_amount));
            editRaportAmount.requestFocus();
            return;
        }

        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        btnViewRaportAmount.setEnabled(false);
        apiService.getCurrentTransitions(authorization).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                btnViewRaportAmount.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    transitions.clear();
                    for (TransitionResponse transition : response.body()) {
                        if (transition.getAmount() != null && transition.getAmount() > minAmount) {
                            transitions.add(transition);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    btnRaportToTxt.setEnabled(!transitions.isEmpty());
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc bao cao theo so tien", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                btnViewRaportAmount.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void saveRaportToTxt() {
        if (transitions.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Khong co du lieu de luu", Toast.LENGTH_LONG).show();
            return;
        }

        try (FileOutputStream outputStream = openFileOutput("raportAmount.txt", Context.MODE_PRIVATE)) {
            StringBuilder content = new StringBuilder();
            for (TransitionResponse transition : transitions) {
                content.append("Transaction: ")
                        .append(valueOrDash(transition.getFromUser()))
                        .append(" -> ")
                        .append(valueOrDash(transition.getToUser()))
                        .append(" amount=")
                        .append(transition.getAmount() == null ? "0" : transition.getAmount())
                        .append('\n');
            }
            outputStream.write(content.toString().getBytes(StandardCharsets.UTF_8));
            Toast.makeText(getApplicationContext(), getString(R.string.raport_saved_successfully), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Khong luu duoc bao cao", Toast.LENGTH_LONG).show();
        }
    }

    private String valueOrDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

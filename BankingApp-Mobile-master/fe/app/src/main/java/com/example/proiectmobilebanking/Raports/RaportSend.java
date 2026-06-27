package com.example.proiectmobilebanking.Raports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaportSend extends AppCompatActivity {
    ListView lvSend;
    List<String> beneficiars = new ArrayList<>();
    SharedPreferencesUser preferences;
    Button btnSave;
    private ApiService apiService;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raport_send);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvSend = findViewById(R.id.lvRaportSend);
        btnSave = findViewById(R.id.btnSaveRaportSendToTxt);
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, beneficiars);
        lvSend.setAdapter(adapter);

        btnSave.setEnabled(false);
        loadBeneficiars();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToTxt();
            }
        });
    }

    private void loadBeneficiars() {
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        apiService.getCurrentTransitions(authorization).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    beneficiars.clear();
                    for (TransitionResponse transition : response.body()) {
                        if (transition.getToUser() != null && !transition.getToUser().trim().isEmpty()) {
                            beneficiars.add(transition.getToUser());
                        }
                    }
                    adapter.notifyDataSetChanged();
                    btnSave.setEnabled(!beneficiars.isEmpty());
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc bao cao nguoi nhan", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void saveToTxt() {
        if (beneficiars.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Khong co du lieu de luu", Toast.LENGTH_LONG).show();
            return;
        }

        try (FileOutputStream outputStream = openFileOutput("raportSend.txt", Context.MODE_PRIVATE)) {
            StringBuilder content = new StringBuilder();
            for (String beneficiar : beneficiars) {
                content.append("Beneficiar: ").append(beneficiar).append('\n');
            }
            outputStream.write(content.toString().getBytes(StandardCharsets.UTF_8));
            Toast.makeText(getApplicationContext(), R.string.raport_saved_successfully, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Khong luu duoc bao cao", Toast.LENGTH_LONG).show();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

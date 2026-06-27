package com.example.proiectmobilebanking.Raports;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.R;
import com.example.proiectmobilebanking.SharedPreferencesUser;
import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransitionResponse;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
        loadBeneficiars();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToTxt(beneficiars);
                Toast.makeText(getApplicationContext(), R.string.raport_saved_successfully, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadBeneficiars() {
        apiService.getCurrentTransitions(preferences.getAuthorizationHeader()).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    beneficiars.clear();
                    for (TransitionResponse transition : response.body()) {
                        beneficiars.add(transition.getToUser());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, beneficiars);
                    lvSend.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToTxt(List<String> beneficiars) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("raportSend.txt", Context.MODE_PRIVATE);
            DataOutputStream out = new DataOutputStream(fileOutputStream);
            for (String beneficiar : beneficiars) {
                out.write("Beneficiar: ".getBytes());
                out.write(beneficiar.getBytes());
                out.write("\n".getBytes());
            }
            out.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

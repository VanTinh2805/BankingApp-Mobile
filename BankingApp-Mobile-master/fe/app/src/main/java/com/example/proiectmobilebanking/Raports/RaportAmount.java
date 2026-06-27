package com.example.proiectmobilebanking.Raports;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.R;
import com.example.proiectmobilebanking.SharedPreferencesUser;
import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransitionResponse;
import com.example.proiectmobilebanking.util.AdapterTransitionResponse;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

        btnViewRaportAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadReport();
            }
        });
        btnRaportToTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRaportToTxt(transitions);
                Toast.makeText(getApplicationContext(), getString(R.string.raport_saved_successfully), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadReport() {
        final double minAmount;
        try {
            minAmount = Double.parseDouble(editRaportAmount.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), R.string.error_amount, Toast.LENGTH_LONG).show();
            return;
        }

        apiService.getCurrentTransitions(preferences.getAuthorizationHeader()).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transitions.clear();
                    for (TransitionResponse transition : response.body()) {
                        if (transition.getAmount() != null && transition.getAmount() > minAmount) {
                            transitions.add(transition);
                        }
                    }
                    AdapterTransitionResponse adapter = new AdapterTransitionResponse(getApplicationContext(), R.layout.tranzaction_row, transitions, getLayoutInflater());
                    lvRaportAmount.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveRaportToTxt(List<TransitionResponse> list) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("raportAmount.txt", Context.MODE_PRIVATE);
            DataOutputStream out = new DataOutputStream(fileOutputStream);
            for (TransitionResponse transition : list) {
                out.write("Transaction: ".getBytes());
                out.write((transition.getFromUser() + " -> " + transition.getToUser() + " amount=" + transition.getAmount()).getBytes());
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

package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransferRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoneyActivity extends AppCompatActivity {
    public static final String ADD_TRANZACTION_HISTORY = "addTranzactionH";

    EditText etBeneficiar;
    EditText etAccount;
    EditText etAmount;
    Button btnSend;
    SharedPreferencesUser preferences;
    private ApiService apiService;
    public static final int code = 230;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);

        initComponents();
    }

    public void initComponents() {
        etBeneficiar = findViewById(R.id.et_beneficiary2);
        etAccount = findViewById(R.id.et_accountNumber2);
        etAmount = findViewById(R.id.et_amount2);
        btnSend = findViewById(R.id.btn_send2);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valid()) {
                    sendMoney();
                }
            }
        });
    }

    private boolean valid() {
        clearErrors();

        String beneficiary = etBeneficiar.getText().toString().trim();
        String receiver = etAccount.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();

        if (beneficiary.isEmpty()) {
            etBeneficiar.setError(getString(R.string.error_beneficiar));
            etBeneficiar.requestFocus();
            return false;
        }
        if (receiver.isEmpty()) {
            etAccount.setError(getString(R.string.error_account));
            etAccount.requestFocus();
            return false;
        }
        if (amountText.isEmpty()) {
            etAmount.setError(getString(R.string.error_amount));
            etAmount.requestFocus();
            return false;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                etAmount.setError(getString(R.string.error_amount));
                etAmount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etAmount.setError(getString(R.string.error_amount));
            etAmount.requestFocus();
            return false;
        }

        return true;
    }

    private void sendMoney() {
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        btnSend.setEnabled(false);
        String receiver = etAccount.getText().toString().trim();
        Double amount = Double.parseDouble(etAmount.getText().toString().trim());
        TransferRequest request = new TransferRequest(receiver, amount);

        apiService.transfer(authorization, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSend.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.money_send, Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    showTransferError(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void showTransferError(Response<Void> response) {
        String message = getBackendError(response);
        if (message == null || message.trim().isEmpty()) {
            if (response.code() >= 500) {
                message = "Server dang gap loi";
            } else {
                message = "Khong the chuyen tien";
            }
        }

        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("receiver") || lowerMessage.contains("card")) {
            etAccount.setError(message);
            etAccount.requestFocus();
        } else if (lowerMessage.contains("amount") || lowerMessage.contains("balance")) {
            etAmount.setError(message);
            etAmount.requestFocus();
        }

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private String getBackendError(Response<Void> response) {
        if (response.errorBody() == null) {
            return null;
        }

        try {
            String rawError = response.errorBody().string();
            JSONObject errorJson = new JSONObject(rawError);
            return errorJson.optString("error", rawError);
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    private void clearErrors() {
        etBeneficiar.setError(null);
        etAccount.setError(null);
        etAmount.setError(null);
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

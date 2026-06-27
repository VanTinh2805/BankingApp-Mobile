package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransferRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    Intent intent;
    private ApiService apiService;
    public static final int code=230;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences=new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        intent=getIntent();

        initComponents();
  }

    public void initComponents(){
        etBeneficiar=findViewById(R.id.et_beneficiary2);
        etAccount=findViewById(R.id.et_accountNumber2);
        etAmount=findViewById(R.id.et_amount2);
        btnSend=findViewById(R.id.btn_send2);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid())
                {
                    sendMoney();
                }
            }
        });

    }
    private boolean valid()
    {
        if(etBeneficiar.getText().toString()==null||etBeneficiar.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getApplicationContext(),R.string.error_beneficiar,Toast.LENGTH_LONG).show();
            return false;
        }
        if(etAccount.getText().toString()==null||etAccount.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getApplicationContext(),R.string.error_account,Toast.LENGTH_LONG).show();
            return false;
        }
        if(etAmount.getText().toString()==null||etAmount.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getApplicationContext(),R.string.error_amount,Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            double amount = Double.parseDouble(etAmount.getText().toString().trim());
            if (amount <= 0) {
                Toast.makeText(getApplicationContext(), R.string.error_amount, Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), R.string.error_amount, Toast.LENGTH_LONG).show();
            return false;
        }


            return true;
    }

    private void sendMoney(){
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        String receiver = etAccount.getText().toString().trim();
        Double amount = Double.parseDouble(etAmount.getText().toString().trim());
        TransferRequest request = new TransferRequest(receiver, amount);

        apiService.transfer(authorization, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.money_send, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong the chuyen tien. Kiem tra so du va so the nguoi nhan", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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

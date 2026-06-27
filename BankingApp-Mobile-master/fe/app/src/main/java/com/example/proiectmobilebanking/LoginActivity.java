package com.example.proiectmobilebanking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.LoginRequest;
import com.example.proiectmobilebanking.network.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button btnregister;
    Button btnlogin;
    EditText etEmail;
    EditText etPassword;

    SharedPreferencesUser preferences;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.tv_username);
        etPassword = findViewById(R.id.tv_password);
        btnregister = findViewById(R.id.register_button);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        btnlogin = findViewById(R.id.login_button);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
                    etEmail.setError("Nhap email");
                    return;
                }

                if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
                    etPassword.setError("Nhap mat khau");
                    return;
                }

                LoginRequest request = new LoginRequest(
                        etEmail.getText().toString().trim(),
                        etPassword.getText().toString()
                );

                apiService.login(request).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            preferences.isLogged(true);
                            preferences.setToken(response.body().getToken());
                            preferences.saveUserInfo(response.body().getUser());

                            Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, getLoginErrorMessage(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    private String getLoginErrorMessage(int code) {
        if (code == 401 || code == 403 || code == 404) {
            return "Sai email hoac mat khau";
        }
        if (code >= 500) {
            return "Server dang gap loi";
        }
        return "Dang nhap khong thanh cong";
    }
}

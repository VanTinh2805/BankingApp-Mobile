package com.example.proiectmobilebanking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    Button btnlogin;
    Button btnRegister;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText iban;
    EditText password;
    EditText confirmPassword;
    RadioGroup rgGender;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
    }

    private void initComponents() {
        btnlogin = findViewById(R.id.bt_goLogin);
        btnRegister = findViewById(R.id.bt_register);
        firstName = findViewById(R.id.et_firstName);
        lastName = findViewById(R.id.et_lastName);
        email = findViewById(R.id.et_email);
        iban = findViewById(R.id.et_iban);
        password = findViewById(R.id.et_password);
        rgGender = findViewById(R.id.radioGroup);
        confirmPassword = findViewById(R.id.et_confirmPassword);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valid()) {
                    register();
                }
            }
        });
    }

    private void register() {
        btnRegister.setEnabled(false);
        RegisterRequest request = createRegisterRequest();

        apiService.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnRegister.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.register_succesful, Toast.LENGTH_LONG).show();
                    goToLogin();
                } else {
                    showRegisterError(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnRegister.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private boolean valid() {
        clearErrors();

        String firstNameValue = firstName.getText().toString().trim();
        String lastNameValue = lastName.getText().toString().trim();
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString();
        String confirmPasswordValue = confirmPassword.getText().toString();

        if (firstNameValue.isEmpty()) {
            firstName.setError(getString(R.string.invalid_firstName));
            firstName.requestFocus();
            return false;
        }
        if (lastNameValue.isEmpty()) {
            lastName.setError(getString(R.string.invalid_lastName));
            lastName.requestFocus();
            return false;
        }
        if (emailValue.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            email.setError(getString(R.string.invalid_email));
            email.requestFocus();
            return false;
        }
        if (passwordValue.trim().isEmpty()) {
            password.setError(getString(R.string.invalid_password));
            password.requestFocus();
            return false;
        }
        if (!passwordValue.equals(confirmPasswordValue)) {
            confirmPassword.setError(getString(R.string.invalid_password2));
            confirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private RegisterRequest createRegisterRequest() {
        String fullName = firstName.getText().toString().trim() + " " + lastName.getText().toString().trim();
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString();
        return new RegisterRequest(fullName, emailValue, passwordValue);
    }

    private void showRegisterError(Response<Void> response) {
        String message = getBackendError(response);
        if (message == null || message.trim().isEmpty()) {
            if (response.code() == 409) {
                message = "Email da ton tai";
            } else if (response.code() >= 500) {
                message = "Server dang gap loi";
            } else {
                message = "Du lieu dang ky khong hop le";
            }
        }

        if (message.toLowerCase().contains("email")) {
            email.setError(message);
            email.requestFocus();
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
        firstName.setError(null);
        lastName.setError(null);
        email.setError(null);
        iban.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

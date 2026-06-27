package com.example.proiectmobilebanking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.RegisterRequest;

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
        //initilizare controale
        btnlogin = findViewById(R.id.bt_goLogin);
        btnRegister=findViewById(R.id.bt_register);
        firstName = findViewById(R.id.et_firstName);
        lastName=findViewById(R.id.et_lastName);
        email=findViewById(R.id.et_email);
        iban=findViewById(R.id.et_iban);
        password=findViewById(R.id.et_password);
        rgGender=findViewById(R.id.radioGroup);
        confirmPassword=findViewById(R.id.et_confirmPassword);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid())
                {
                    RegisterRequest request = createRegisterRequest();
                    apiService.register(request).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), R.string.register_succesful, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Email da ton tai hoac du lieu khong hop le", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private boolean valid() {
        if (firstName.getText() == null || firstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_firstName, Toast.LENGTH_LONG).show();
            return false;
        }
        if (lastName.getText() == null || lastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_lastName, Toast.LENGTH_LONG).show();
            return false;
        }
        if (email.getText() == null || email.getText().toString().trim().isEmpty()||!email.getText().toString().contains("@")) {
            Toast.makeText(getApplicationContext(), R.string.invalid_email, Toast.LENGTH_LONG).show();
            return false;
        }
        if (iban.getText() == null || iban.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_iban, Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.getText() == null || password.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_LONG).show();
            return false;
        }
        if(((password.getText().toString().compareTo(confirmPassword.getText().toString()))!=0) || (confirmPassword.getText() == null || confirmPassword.getText().toString().trim().isEmpty()))
        {
            Toast.makeText(getApplicationContext(), R.string.invalid_password2, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private RegisterRequest createRegisterRequest(){
        String fullName = firstName.getText().toString().trim() + " " + lastName.getText().toString().trim();
        String emailS = email.getText().toString().trim();
        String passwordS = password.getText().toString();
        return new RegisterRequest(fullName, emailS, passwordS);
    }


}

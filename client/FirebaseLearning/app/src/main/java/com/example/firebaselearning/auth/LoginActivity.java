package com.example.firebaselearning.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaselearning.DialogLoading;
import com.example.firebaselearning.MainActivity;
import com.example.firebaselearning.dto.auth.request.LoginRequestDto;
import com.example.firebaselearning.dto.auth.response.TokensDto;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.AuthClient;
import com.example.firebaselearning.R;
import com.example.firebaselearning.network.FCMTokenManager;
import com.google.firebase.FirebaseApp;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email_login;
    private EditText password_login;

    private DialogLoading dialogLoading;

    private AuthClient authClient;

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        Button btn_login = findViewById(R.id.btn_login);
        TextView register_txt = findViewById(R.id.register_txt);

        tokenManager = new TokenManager(this);

        authClient = ApiClient.getInstance(this).create(AuthClient.class);

        register_txt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btn_login.setOnClickListener(v -> {
            dialogLoading = new DialogLoading(this);
            dialogLoading.showDialog();
            Handler handler = new Handler();
            if (email_login.getText().toString().isEmpty() || password_login.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Поля должны быть заполненны", Toast.LENGTH_SHORT).show();
                handler.postDelayed(() -> dialogLoading.closeDialog(), 0);
            } else {
                LoginRequestDto loginDto = new LoginRequestDto();
                loginDto.setEmail(email_login.getText().toString());
                loginDto.setPassword(password_login.getText().toString());

                authClient.login(loginDto).enqueue(new retrofit2.Callback<>() {
                    @Override
                    public void onResponse(Call<TokensDto> call, Response<TokensDto> response) {
                        if(response.isSuccessful()) {
                            TokensDto tokensDto = response.body();
                            tokenManager.saveTokens(tokensDto.getAccessToken(), tokensDto.getRefreshToken());

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            handler.postDelayed(() -> dialogLoading.closeDialog(), 0);
                            FCMTokenManager.updateFCMToken(LoginActivity.this);
                            startActivity(intent);
                        } else {
                            System.out.println(response.code());
                            Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                            handler.postDelayed(() -> dialogLoading.closeDialog(), 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<TokensDto> call, Throwable t) {
                        handler.postDelayed(() -> dialogLoading.closeDialog(), 0);
                        Toast.makeText(LoginActivity.this, "Возникла ошибка", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
package com.example.firebaselearning.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebaselearning.DialogLoading;
import com.example.firebaselearning.MainActivity;
import com.example.firebaselearning.dto.auth.request.RegisterRequestDto;
import com.example.firebaselearning.dto.auth.response.TokensDto;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.AuthClient;
import com.example.firebaselearning.R;
import com.example.firebaselearning.network.FCMTokenManager;

import retrofit2.Call;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_register;
    private EditText password1_register;
    private EditText password2_register;
    private EditText user_name_register;

    private DialogLoading dialogLoading;

    private AuthClient authClient;

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_register = findViewById(R.id.email_register);
        password1_register = findViewById(R.id.password1_register);
        password2_register = findViewById(R.id.password2_register);
        user_name_register = findViewById(R.id.user_name_register);
        Button btn_register = findViewById(R.id.btn_register);

        tokenManager = new TokenManager(this);

        authClient = ApiClient.getInstance(this).create(AuthClient.class);

        btn_register.setOnClickListener(v -> {

            if (email_register.getText().toString().isEmpty() ||
                    password1_register.getText().toString().isEmpty() ||
                    password2_register.getText().toString().isEmpty() ||
                    user_name_register.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Поля должны быть заполенными", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password2_register.getText().toString().equals(password1_register.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                return;
            }

            dialogLoading = new DialogLoading(this);
            dialogLoading.showDialog();
            Handler handler = new Handler();

            RegisterRequestDto userRegister = new RegisterRequestDto();
            userRegister.setUserName(user_name_register.getText().toString());
            userRegister.setEmail(email_register.getText().toString());
            userRegister.setPassword1(password1_register.getText().toString());
            userRegister.setPassword2(password2_register.getText().toString());

            authClient.create(userRegister).enqueue(new retrofit2.Callback<>() {
                @Override
                public void onResponse(Call<TokensDto> call, Response<TokensDto> response) {
                    if (response.isSuccessful()) {
                        TokensDto tokensDto = response.body();
                        tokenManager.saveTokens(tokensDto.getAccessToken(), tokensDto.getRefreshToken());

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        handler.postDelayed(() -> dialogLoading.closeDialog(), 0);
                        FCMTokenManager.updateFCMToken(RegisterActivity.this);
                        startActivity(intent);
                    } else {
                        System.out.println(response.code());
                        Toast.makeText(RegisterActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                        handler.postDelayed(() -> dialogLoading.closeDialog(), 0);
                    }
                }

                @Override
                public void onFailure(Call<TokensDto> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("RetrofitError", "Ошибка запроса: " + t.getMessage());
                    Toast.makeText(RegisterActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    handler.postDelayed(() -> dialogLoading.closeDialog(), 0);
                }
            });
        });
    }
}
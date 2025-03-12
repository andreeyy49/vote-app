package com.example.firebaselearning.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.model.Community;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.CommunityClient;
import com.example.firebaselearning.R;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCommunityActivity extends BaseActivity {

    private EditText communityName;
    private EditText communityBody;
    private Button addCommunityBtn;
    private Button cancelAddBtn;

    private CommunityClient communityClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        communityName = findViewById(R.id.community_name);
        communityBody = findViewById(R.id.community_body);
        addCommunityBtn = findViewById(R.id.add_community_btn);
        cancelAddBtn = findViewById(R.id.cansel_add_btn);

        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);

        addCommunityBtn.setOnClickListener(v -> addCommunity());
        cancelAddBtn.setOnClickListener(v -> navigateToListCommunity());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_community;
    }

    @Override
    protected String getToolbarTitle() {
        return "Создать сообщество";
    }

    private void addCommunity() {
        String title = communityName.getText().toString().trim();
        String body = communityBody.getText().toString();

        // Проверяем, существует ли сообщество с таким названием
        communityClient.findByTitle(title).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    // Проверяем, есть ли тело ответа
                    if (response.body() != null) {
                        // Если тело не пустое, обрабатываем его
                        if (response.body()) {
                            Toast.makeText(AddCommunityActivity.this, "Сообщество с данным названием существует", Toast.LENGTH_SHORT).show();
                        } else {
                            Community community = new Community();
                            community.setTitle(title);
                            community.setDescription(body);
                            saveCommunity(community);
                        }
                    } else {
                        // Если тело пустое, логируем это или показываем сообщение
                        Log.w("RetrofitWarning", "Ответ пустой, создаём новое сообщество.");
                        Community community = new Community();
                        community.setTitle(title);
                        community.setDescription(body);
                        saveCommunity(community);
                    }
                } else {
                    // Обрабатываем ошибку при неуспешном ответе
                    Log.e("RetrofitError", "Ошибка при получении ответа: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                showError(t);
            }
        });

    }

    private void saveCommunity(Community response) {
        communityClient.save(response).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Community> call, Response<Community> response) {
                if (response.isSuccessful()) {
                    navigateToListCommunity();
                } else {
                    // Логирование ошибки
                    Log.e("RetrofitError", "Error response: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Community> call, Throwable t) {
                showError(t);
            }
        });
    }

    private void navigateToListCommunity() {
        Intent intent = new Intent(AddCommunityActivity.this, ListCommunityActivity.class);
        startActivity(intent);
    }

    private void showError(Throwable t) {
        Log.e("RetrofitError", "Ошибка запроса: " + t.getMessage());
        Toast.makeText(this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }
}

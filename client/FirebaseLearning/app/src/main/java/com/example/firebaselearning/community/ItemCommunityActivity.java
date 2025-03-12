package com.example.firebaselearning.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.dto.auth.response.UserCommunityShipResponse;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.CommunityClient;
import com.example.firebaselearning.model.Community;
import com.example.firebaselearning.network.MembershipClient;
import com.example.firebaselearning.R;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemCommunityActivity extends BaseActivity {

    private Button back_btn;
    private Button leave_btn;
    private Button join_btn;
    private TextView community_name_tv;
    private TextView community_body_tv;
    private CheckBox privateCommunity_chb;

    private String communityKey;

    private Community thisCommunity;

    private CommunityClient communityClient;
    private MembershipClient membershipClient;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);
        membershipClient = ApiClient.getInstance(this).create(MembershipClient.class);

        getIntentMain();

        back_btn = findViewById(R.id.back_btn);
        leave_btn = findViewById(R.id.leave_btn);
        join_btn = findViewById(R.id.join_btn);
        community_name_tv = findViewById(R.id.community_name_tv);
        community_body_tv = findViewById(R.id.community_body_tv);
        privateCommunity_chb = findViewById(R.id.privateCommunity_chb); // Инициализация чекбокса

        if (communityKey != null) {
            // Проверяем, состоит ли пользователь в сообществе
            disposable = membershipClient.findAllByUserId()
                    .subscribeOn(Schedulers.io())  // Запрос выполняется в фоне
                    .observeOn(AndroidSchedulers.mainThread())  // Обновляем UI в главном потоке
                    .map(memberships -> {
                        for (UserCommunityShipResponse membership : memberships) {
                            if (String.valueOf(membership.getCommunityId()).equals(communityKey)) {
                                return true; // Найдено совпадение
                            }
                        }
                        return false; // Нет совпадений
                    })
                    .subscribe(
                            this::loadCommunityData, // Передаём результат в loadCommunityData
                            this::showError
                    );


        }

        back_btn.setOnClickListener(v -> {
            Intent intent = new Intent(ItemCommunityActivity.this, ListCommunityActivity.class);
            startActivity(intent);
        });

        // Здесь код для выхода из сообщества
        leave_btn.setOnClickListener(v -> {
            disposable = membershipClient.deleteByCommunityId(Long.valueOf(communityKey))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> { // onComplete (если удаление успешно)
                                Intent intent = new Intent(ItemCommunityActivity.this, ListCommunityActivity.class);
                                startActivity(intent);
                            },
                            this::showError // onError (если произошла ошибка)
                    );
        });

        // Здесь код для вступления в сообщество
        join_btn.setOnClickListener(v -> {
            disposable = membershipClient.create(Long.valueOf(communityKey))
                    .subscribeOn(Schedulers.io()) // Запрос выполняется в фоне
                    .observeOn(AndroidSchedulers.mainThread()) // Результат обрабатываем в UI-потоке
                    .subscribe(
                            response -> { // onSuccess (если успешно создано)
                                Intent intent = new Intent(ItemCommunityActivity.this, ListCommunityActivity.class);
                                startActivity(intent);
                            },
                            this::showError // onError (если произошла ошибка)
                    );
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_item_community;
    }

    @Override
    protected String getToolbarTitle() {
        return "Сообщество";
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            communityKey = intent.getStringExtra("communityId"); // Поменяйте на нужный ключ
        }
    }

    // Метод для загрузки данных о сообществе
    private void loadCommunityData(boolean isMember) {
        communityClient.findById(Long.parseLong(communityKey)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Community> call, Response<Community> response) {
                if (response.isSuccessful() && response.body() != null) {
                    thisCommunity = response.body();
                    community_name_tv.setText(thisCommunity.getTitle());
                    community_body_tv.setText(thisCommunity.getDescription());
                    privateCommunity_chb.setChecked(thisCommunity.isPrivateFlag());

                    if (isMember) {
                        leave_btn.setVisibility(View.VISIBLE);
                        join_btn.setVisibility(View.INVISIBLE);
                    } else {
                        leave_btn.setVisibility(View.INVISIBLE);
                        join_btn.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<Community> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showError(Throwable t) {
        Log.e("RetrofitError", "Ошибка запроса: " + t.getMessage());
        Toast.makeText(this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}

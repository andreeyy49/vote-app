package com.example.firebaselearning.community;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.MainActivity;
import com.example.firebaselearning.dto.auth.response.UserCommunityShipResponse;
import com.example.firebaselearning.model.Community;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.CommunityClient;
import com.example.firebaselearning.network.MembershipClient;
import com.example.firebaselearning.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCommunityActivity extends BaseActivity {

    private EditText findCommunity_et;
    private ListView community_list;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private ArrayList<Long> communityKey;
    private boolean isSearch;

    private CommunityClient communityClient;
    private MembershipClient membershipClient;
    private List<UserCommunityShipResponse> shipResponses;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        membershipClient = ApiClient.getInstance(this).create(MembershipClient.class);
        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);

        findCommunity_et = findViewById(R.id.findCommunity_et);
        community_list = findViewById(R.id.community_list);
        listData = new ArrayList<>();
        communityKey = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        community_list.setAdapter(adapter);

        loadUserCommunities();  // Загружаем сообщества пользователя при старте
        setupSearchListener();   // Настроим обработчик поиска

        // Обработка кнопки добавления сообщества
        Button addCommunityBtn = findViewById(R.id.add_community_btn);
        addCommunityBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ListCommunityActivity.this, AddCommunityActivity.class);
            startActivity(intent);
        });

        // Обработка кнопки назад
        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ListCommunityActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Обработчик клика по элементу в списке
        community_list.setOnItemClickListener((parent, view, position, id) -> {
            Long communityId = communityKey.get(position);
            Intent intent = new Intent(ListCommunityActivity.this, ItemCommunityActivity.class);
            intent.putExtra("communityId", String.valueOf(communityId));
            startActivity(intent);
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list_community;
    }

    @Override
    protected String getToolbarTitle() {
        return "Сообщества";
    }

    private void loadUserCommunities() {
        if (!isSearch) {
            disposable = membershipClient.findAllByUserId()
                    .subscribeOn(Schedulers.io())  // Запрос выполняется в фоне
                    .observeOn(AndroidSchedulers.mainThread())  // Обновляем UI в главном потоке
                    .filter(shipResponses -> shipResponses != null && !shipResponses.isEmpty()) // Фильтруем пустые или null-значения
                    .map(shipResponses -> {
                        List<Long> communityIds = new ArrayList<>();
                        for (UserCommunityShipResponse ship : shipResponses) {
                            communityIds.add(ship.getCommunityId());
                        }
                        return communityIds;
                    })
                    .subscribe(
                            this::fetchCommunitiesByIds, // Передаём список ID в метод fetchCommunitiesByIds
                            this::showError // Обрабатываем ошибку
                    );
        }
    }

    private void fetchCommunitiesByIds(List<Long> communityIds) {
        communityClient.findAllByIds(communityIds).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Community>> call, Response<List<Community>> response) {
                updateCommunityList(response.body());
            }

            @Override
            public void onFailure(Call<List<Community>> call, Throwable t) {
                showError(t);
            }
        });
    }

    private void setupSearchListener() {
        findCommunity_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    isSearch = false;
                    loadUserCommunities();
                } else {
                    isSearch = true;
                    searchCommunities(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchCommunities(String fragment) {
        communityClient.findAllByFragment(fragment).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Community>> call, Response<List<Community>> response) {
                updateCommunityList(response.body());
            }

            @Override
            public void onFailure(Call<List<Community>> call, Throwable t) {
                showError(t);
            }
        });
    }

    private void updateCommunityList(List<Community> communities) {
        listData.clear();
        communityKey.clear();
        if (communities != null) {
            for (Community community : communities) {
                listData.add(community.getTitle());
                communityKey.add(community.getId());
            }
        }
        adapter.notifyDataSetChanged();
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

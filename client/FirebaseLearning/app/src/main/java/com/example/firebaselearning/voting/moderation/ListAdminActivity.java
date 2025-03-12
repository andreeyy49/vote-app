package com.example.firebaselearning.voting.moderation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.MainActivity;
import com.example.firebaselearning.dto.auth.request.ModeratorRequest;
import com.example.firebaselearning.dto.auth.response.UserCommunityShipResponse;
import com.example.firebaselearning.model.Community;
import com.example.firebaselearning.model.User;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.CommunityClient;
import com.example.firebaselearning.network.MembershipClient;
import com.example.firebaselearning.network.UserClient;
import com.example.firebaselearning.staticvalue.StaticValue;
import com.example.firebaselearning.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListAdminActivity extends BaseActivity {

    private Button back;
    private LinearLayout ll_users;
    private Spinner community_spn;

    private Community thisCommunity;
    private HashMap<String, UUID> users;

    private ArrayAdapter<String> communityAdapter;
    private List<String> communityName;
    private List<String> communityIdList;
    private List<Community> communityList;
    private String selectedCommunity;
    private Long communityId;
    private List<User> userList;

    private Disposable disposable;
    private MembershipClient membershipClient;
    private UserClient userClient;
    private CommunityClient communityClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        back = findViewById(R.id.back);
        ll_users = findViewById(R.id.ll_users);
        community_spn = findViewById(R.id.community_spn);

        membershipClient = ApiClient.getInstance(this).create(MembershipClient.class);
        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);
        userClient = ApiClient.getInstance(this).create(UserClient.class);

        users = new HashMap<>();

        getSpinnerItem();
        community_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCommunity = parent.getItemAtPosition(position).toString();

                if (!selectedCommunity.equals("Выберите сообщество")) {
                    for (Community community : communityList) {
                        if (community.getTitle().equals(selectedCommunity)) {
                            communityId = community.getId();
                            thisCommunity = community;
                        }
                    }

                    disposable = membershipClient.findAllByCommunityId(communityId)
                            .flatMap(membership -> Observable.fromCallable(() -> {
                                List<UUID> userIds = new ArrayList<>();
                                for (UserCommunityShipResponse ship : membership) {
                                    userIds.add(ship.getUserId());
                                }

                                Call<List<User>> call = userClient.findAllByIds(userIds);
                                Response<List<User>> response = call.execute();
                                if (response.isSuccessful()) {
                                    return response.body();
                                } else {
                                    throw new Exception("Ошибка получения пользователей");
                                }
                            }))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(usersList -> {
                                for (User user : usersList) {
                                    String role;
                                    if (thisCommunity.getModerators().contains(user.getId())) {
                                        role = StaticValue.MODERATOR_ROLE;
                                    } else if (!thisCommunity.getAdmin().equals(user.getId())) {
                                        role = StaticValue.VOTING_ROLE;
                                    } else {
                                        continue;
                                    }
                                    addUser(user.getName() + ": " + user.getEmail(), role);
                                    users.put(user.getEmail(), user.getId());
                                }
                                startListenerChoice();
                            }, throwable -> {
                                Toast.makeText(ListAdminActivity.this, "Ошибка загрузки данных: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(ListAdminActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list_admin;
    }

    @Override
    protected String getToolbarTitle() {
        return "Управление модераторами";
    }

    private void addUser(String name, String role) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getParams(lParams);
        addChoice(lParams, name, role);

    }

    private void getParams(LinearLayout.LayoutParams lParams) {
        lParams.gravity = Gravity.LEFT;
    }

    private void addChoice(LinearLayout.LayoutParams lParams, String name, String role) {
        CheckBox newCheckBox = new CheckBox(this);
        newCheckBox.setText(name);
        newCheckBox.setClickable(false);
        if (role.equals(StaticValue.MODERATOR_ROLE)) {
            newCheckBox.setChecked(true);
        }
        ll_users.addView(newCheckBox, lParams);
    }

    private String subStr(String name) {
        Pattern pattern = Pattern.compile(": ");
        Matcher matcher = pattern.matcher(name.trim());
        int end = 0;

        while (matcher.find()) {
            end = matcher.end();
        }

        return name.substring(end);
    }

    private void startListenerChoice() {
        for (int i = 0; i < ll_users.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) ll_users.getChildAt(i);
            checkBox.setOnClickListener(v -> {
                String userEmail = subStr(checkBox.getText().toString());
                UUID userId = users.get(userEmail); // Получаем ID пользователя

                if (userId == null) {
                    Toast.makeText(ListAdminActivity.this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isChecked = checkBox.isChecked();
                updateModeratorStatus(userId.toString(), isChecked);
            });
        }
    }


    private void getSpinnerItem() {
        communityName = new ArrayList<>();
        communityIdList = new ArrayList<>();
        communityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, communityName);
        communityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        communityList = new ArrayList<>();
        communityName.add("Выберите сообщество");

        // Запрос для получения сообществ, где пользователь является администратором
        Call<List<Community>> callAdmin = communityClient.findAllByAdmin();
        callAdmin.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Community>> call, Response<List<Community>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isEmpty()) {
                        communityList.addAll(response.body()); // Добавляем сообщества, где пользователь администратор
                    }

                    // Заполняем списки для Spinner
                    for (Community community : communityList) {
                        communityName.add(community.getTitle());
                        communityIdList.add(community.getId().toString());
                    }

                    // Обновляем адаптер для Spinner
                    community_spn.setAdapter(communityAdapter);
                } else {
                    Toast.makeText(ListAdminActivity.this, "Ошибка загрузки данных администратора", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Community>> call, Throwable t) {
                Toast.makeText(ListAdminActivity.this, "Ошибка загрузки данных администратора: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для добавления/удаления модератора
    private void updateModeratorStatus(String userId, boolean isAdding) {
        ModeratorRequest request = new ModeratorRequest(userId, communityId);
        Completable action = isAdding ? communityClient.createModerator(request) : communityClient.removeModerator(request);

        disposable = action
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(ListAdminActivity.this, isAdding ? "Модератор добавлен" : "Модератор удален", Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    Toast.makeText(ListAdminActivity.this, "Ошибка: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
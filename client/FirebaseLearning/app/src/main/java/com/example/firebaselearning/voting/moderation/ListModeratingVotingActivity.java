package com.example.firebaselearning.voting.moderation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.DialogLoading;
import com.example.firebaselearning.MainActivity;
import com.example.firebaselearning.dto.auth.request.FindVoteRequest;
import com.example.firebaselearning.model.Community;
import com.example.firebaselearning.model.User;
import com.example.firebaselearning.model.Voting;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.CommunityClient;
import com.example.firebaselearning.network.VotingClient;
import com.example.firebaselearning.staticvalue.StaticValue;
import com.example.firebaselearning.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListModeratingVotingActivity extends BaseActivity {
    private ListView moderating_list;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private Button back;
    private Spinner community_spn;

    private User thisUser;
    private String selectedCommunity;

    private ArrayAdapter<String> communityListAdapter;
    private List<String> communityName;
    private List<String> communityId;
    private List<Community> communityList;
    private ArrayList<String> votingKey;

    private DialogLoading dialogLoading;
    private Handler handler;

    private Disposable disposable;
    private VotingClient votingClient;
    private CommunityClient communityClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogLoading = new DialogLoading(this);
        dialogLoading.showDialog();
        handler = new Handler();

        moderating_list = findViewById(R.id.moderating_list);
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        moderating_list.setAdapter(adapter);

        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);
        votingClient = ApiClient.getInstance(this).create(VotingClient.class);

        back = findViewById(R.id.back);
        community_spn = findViewById(R.id.community_spn);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(ListModeratingVotingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        community_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCommunity = parent.getItemAtPosition(position).toString();
                if (!selectedCommunity.equals("Выберите сообщество")) {
                    getVoteFromDB();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSpinnerItem();
        setOnClickItem();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list_moderating_voting;
    }

    @Override
    protected String getToolbarTitle() {
        return "Модерация";
    }

    private void getVoteFromDB() {
        if (!selectedCommunity.equals("Выберите сообщество")) {
            listData.clear();
            votingKey = new ArrayList<>();

            // Находим сообщество
            Optional<Community> optionalCommunity = communityList.stream()
                    .filter(element -> element.getTitle().equals(selectedCommunity))
                    .findFirst();

            if (optionalCommunity.isEmpty()) {
                return; // Выходим, если не нашли сообщество
            }

            Community community = optionalCommunity.get();
            FindVoteRequest findVoteRequest = new FindVoteRequest(community.getId(), false);

            // Асинхронный вызов WebFlux API
            disposable = votingClient.findAllByCommunityIdAndPublished(findVoteRequest)
                    .subscribe(votings -> {
                        for (Voting vote : votings) {
                            listData.add(vote.getTitle());
                            votingKey.add(vote.getId());
                        }

                        // Обновляем адаптер в главном потоке
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                        runOnUiThread(() -> handler.postDelayed(() -> dialogLoading.closeDialog(), 0));
                    }, throwable -> {
                        // Обработка ошибок
                        runOnUiThread(() ->
                                Toast.makeText(this, "Ошибка загрузки: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    });
        }
    }

    private void setOnClickItem() {
        moderating_list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ListModeratingVotingActivity.this, ItemModeratingVotingActivity.class);
            intent.putExtra(StaticValue.VOTING_KEY, votingKey.get(position));
            startActivity(intent);
        });
    }

    private void getSpinnerItem() {
        communityName = new ArrayList<>();
        communityId = new ArrayList<>();
        communityListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, communityName);
        communityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        communityList = new ArrayList<>();
        communityName.add("Выберите сообщество");


        // Запрос для получения сообществ, где пользователь является модератором
        Call<List<Community>> callModerator = communityClient.findAllByModerator();
        // Запрос для получения сообществ, где пользователь является администратором
        Call<List<Community>> callAdmin = communityClient.findAllByAdmin();

        // Выполняем запросы асинхронно
        callModerator.enqueue(new Callback<List<Community>>() {
            @Override
            public void onResponse(Call<List<Community>> call, Response<List<Community>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(!response.body().isEmpty()) {
                        communityList.addAll(response.body()); // Добавляем сообщества, где пользователь модератор
                    }
                    // После получения данных модератора, выполняем запрос для администратора
                    callAdmin.enqueue(new Callback<List<Community>>() {
                        @Override
                        public void onResponse(Call<List<Community>> call, Response<List<Community>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if(!response.body().isEmpty()) {
                                communityList.addAll(response.body()); // Добавляем сообщества, где пользователь администратор

                                // Убираем дубликаты (если пользователь одновременно модератор и администратор)
                                Set<Community> uniqueCommunities = new HashSet<>(communityList);
                                communityList.clear();
                                communityList.addAll(uniqueCommunities);
                                }

                                // Заполняем списки для Spinner
                                for (Community community : communityList) {
                                    communityName.add(community.getTitle());
                                    communityId.add(community.getId().toString());
                                }

                                // Обновляем адаптер для Spinner
                                community_spn.setAdapter(communityListAdapter);
                                dialogLoading.closeDialog();
                            } else {
                                Toast.makeText(ListModeratingVotingActivity.this, "Ошибка загрузки данных администратора", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Community>> call, Throwable t) {
                            Toast.makeText(ListModeratingVotingActivity.this, "Ошибка загрузки данных администратора: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ListModeratingVotingActivity.this, "Ошибка загрузки данных модератора", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Community>> call, Throwable t) {
                Toast.makeText(ListModeratingVotingActivity.this, "Ошибка загрузки данных модератора: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
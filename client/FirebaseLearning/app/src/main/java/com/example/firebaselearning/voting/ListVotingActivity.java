package com.example.firebaselearning.voting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.MainActivity;
import com.example.firebaselearning.dto.auth.response.UserCommunityShipResponse;
import com.example.firebaselearning.model.Community;
import com.example.firebaselearning.model.Voting;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.CommunityClient;
import com.example.firebaselearning.network.MembershipClient;
import com.example.firebaselearning.network.VotingClient;
import com.example.firebaselearning.staticvalue.StaticValue;
import com.example.firebaselearning.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class ListVotingActivity extends BaseActivity {
    private String selectedCommunity;

    private Disposable disposable;
    private MembershipClient membershipClient;
    private CommunityClient communityClient;
    private VotingClient votingClient;

    private ListView main_list;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> votingKey;

    private String typeKey;

    private Button back;
    private Spinner community_spn;

    private ArrayAdapter<String> communityListAdapter;
    private List<String> communityName;
    private List<String> communityId;
    private List<Community> communityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main_list = findViewById(R.id.main_list);
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        main_list.setAdapter(adapter);
        back = findViewById(R.id.back);
        community_spn = findViewById(R.id.community_spn);

        membershipClient = ApiClient.getInstance(this).create(MembershipClient.class);
        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);
        votingClient = ApiClient.getInstance(this).create(VotingClient.class);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(ListVotingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        getIntentMain();
        getSpinnerItem();

        community_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCommunity = parent.getItemAtPosition(position).toString();
                getDataFromDB();
                setOnClickItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list_voting;
    }

    @Override
    protected String getToolbarTitle() {
        return "Голосования";
    }

    private void getDataFromDB() {
        if (!selectedCommunity.equals("Выберите сообщество")) {
            listData.clear();
            votingKey = new ArrayList<>();

            // Находим сообщество по выбранному имени
            Optional<Community> optionalCommunity = communityList.stream()
                    .filter(element -> element.getTitle().equals(selectedCommunity))
                    .findFirst();

            if (optionalCommunity.isEmpty()) {
                return; // Выходим, если не нашли сообщество
            }

            Community community = optionalCommunity.get();
            Long communityId = community.getId();  // Получаем ID выбранного сообщества

            // В зависимости от типа ключа загружаем голосования
            Observable<List<Voting>> votingObservable;
            if (typeKey.equals("DoVote")) {
                // Голосования, в которых пользователь ещё не проголосовал
                votingObservable = votingClient.findAllByUserNotVoted(communityId);
            } else if (typeKey.equals("Result")) {
                // Голосования, в которых пользователь уже проголосовал
                votingObservable = votingClient.findAllByUserVoted(communityId);
            } else {
                return; // Если typeKey не соответствует, ничего не делаем
            }

            // Асинхронный вызов API для получения голосований
            disposable = votingObservable.subscribe(votings -> {
                for (Voting vote : votings) {
                    listData.add(vote.getTitle());
                    votingKey.add(vote.getId());
                }
                // Обновляем адаптер в главном потоке
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }, throwable -> {
                // Обработка ошибок
                runOnUiThread(() ->
                        Toast.makeText(this, "Ошибка загрузки: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                );
            });
        }
    }


    private void setOnClickItem() {
        main_list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;
            if(typeKey.equals("Result")){
                intent = new Intent(ListVotingActivity.this, ResultActivity.class);
                intent.putExtra(StaticValue.VOTING_KEY, votingKey.get(position));
                intent.putExtra(StaticValue.TYPE_KEY, typeKey);
                startActivity(intent);
                return;
            }
            intent = new Intent(ListVotingActivity.this, ItemVotingActivity.class);
            intent.putExtra(StaticValue.VOTING_KEY, votingKey.get(position));
            intent.putExtra(StaticValue.TYPE_KEY, typeKey);
            startActivity(intent);
        });
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            typeKey = intent.getStringExtra(StaticValue.TYPE_KEY);
        }
    }

    private void getSpinnerItem() {
        communityName = new ArrayList<>();
        communityId = new ArrayList<>();
        communityListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, communityName);
        communityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        communityName.add("Выберите сообщество");

        // Сначала получаем связи пользователя с сообществами (реактивный запрос)
        disposable = membershipClient.findAllByUserId()
                .flatMap(userCommunityShipResponses -> {
                    // Составляем список communityId из связей
                    List<Long> communityIds = new ArrayList<>();
                    for (UserCommunityShipResponse response : userCommunityShipResponses) {
                        communityIds.add(response.getCommunityId());
                    }

                    // Теперь выполняем синхронный запрос для получения сообществ по этим ID
                    return Observable.fromCallable(() -> {
                        // Используем синхронный CommunityClient
                        Call<List<Community>> call = communityClient.findAllByIds(communityIds);
                        Response<List<Community>> response = call.execute();  // Синхронный вызов
                        if (response.isSuccessful()) {
                            return response.body();  // Возвращаем список сообществ
                        } else {
                            throw new Exception("Ошибка получения сообществ");
                        }
                    });
                })
                .subscribeOn(Schedulers.io())  // Выполняем запрос в фоновом потоке
                .observeOn(AndroidSchedulers.mainThread())  // Переход к главному потоку для обновления UI
                .subscribe(new Consumer<List<Community>>() {
                    @Override
                    public void accept(List<Community> communities) throws Throwable {
                        // Заполняем список с сообществами
                        communityList = new ArrayList<>();
                        for (Community community : communities) {
                            communityName.add(community.getTitle());
                            communityId.add(community.getId().toString());  // Преобразуем ID в строку
                            communityList.add(community);
                        }
                        // Обновляем адаптер для Spinner
                        community_spn.setAdapter(communityListAdapter);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        // Обработка ошибок
                        Toast.makeText(ListVotingActivity.this, "Ошибка загрузки данных: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
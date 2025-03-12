package com.example.firebaselearning.voting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.firebaselearning.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class AddVotingActivity extends BaseActivity {

    private EditText voting_name;
    private EditText voting_body;
    private Spinner community_spn;
    private LinearLayout ll_choices;

    private ArrayAdapter<String> communityListAdapter;
    private List<String> communityName;
    private List<String> communityId;
    private List<Community> communityList;

    private MembershipClient membershipClient;
    private CommunityClient communityClient;
    private VotingClient votingClient;

    private Disposable disposable;

    private String selectedCommunityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voting_name = findViewById(R.id.voting_name);
        voting_body = findViewById(R.id.voting_body);
        community_spn = findViewById(R.id.community_spn);

        Button add_choice = findViewById(R.id.add_choice);
        Button deleted_choice = findViewById(R.id.deleted_choice);
        Button cansel = findViewById(R.id.cansel);
        Button voting_save = findViewById(R.id.voting_save);

        ll_choices = findViewById(R.id.ll_choices);

        membershipClient = ApiClient.getInstance(this).create(MembershipClient.class);
        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);
        votingClient = ApiClient.getInstance(this).create(VotingClient.class);

        getSpinnerItem();

        community_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCommunityId = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i = 0; i < 2; i++) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getParams(lParams);
            addChoice(lParams);
        }

        add_choice.setOnClickListener(this::onClick);

        deleted_choice.setOnClickListener(v -> {
            if (ll_choices.getChildCount() <= 2) {
                Toast.makeText(this, "Число вариантов должно быть не меньше двух", Toast.LENGTH_SHORT).show();
            } else {
                ll_choices.removeView(ll_choices.getChildAt(ll_choices.getChildCount() - 1));
            }
        });

        voting_save.setOnClickListener(v -> {
                    boolean dataWrite = (!voting_name.getText().toString().isEmpty()) &&
                            (!voting_body.getText().toString().isEmpty());

                    int counter = ll_choices.getChildCount();
                    for (int i = 0; i < counter; i++) {
                        EditText editText = (EditText) ll_choices.getChildAt(i);
                        if (editText.getText().toString().isEmpty()) {
                            dataWrite = false;
                            break;
                        }
                    }

                    if (community_spn.getSelectedItem().toString().equals("Выберите сообщество")) {
                        dataWrite = false;
                    }

                    Community community = new Community();
                    if (dataWrite) {
                        for (Community element : communityList) {
                            if (element.getTitle().equals(selectedCommunityId)) {
                                community = element;
                            }
                        }

                        Voting newVoting = new Voting();
                        newVoting.setTitle(voting_name.getText().toString());
                        newVoting.setDescription(voting_body.getText().toString());
                        newVoting.setCastVote(new HashMap<>());
                        newVoting.setChoices(new HashMap<>());
                        newVoting.setCommunityId(community.getId());

                        for (int i = 0; i < ll_choices.getChildCount(); i++) {
                            EditText editText = (EditText) ll_choices.getChildAt(i);
                            if (newVoting.getChoices().containsKey(editText.getText().toString())) {
                                Toast.makeText(AddVotingActivity.this, "Варианты не должны повторяться или состоять только из цифр", Toast.LENGTH_LONG).show();
                                return;
                            }
                            newVoting.getChoices().put(editText.getText().toString() + " ", 0);
                        }

                        disposable = votingClient.save(newVoting)
                                .subscribeOn(Schedulers.io()) // Выполнение в фоне
                                .observeOn(AndroidSchedulers.mainThread()) // Обработка в UI-потоке
                                .subscribe(
                                        voting -> {
                                            // Успешный результат
                                            Toast.makeText(this, "Голосование успешно создано!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddVotingActivity.this, MainActivity.class));
                                        },
                                        throwable -> {
                                            // Ошибка
                                            Toast.makeText(this, "Ошибка при создании голосования: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                );



                        Intent intent = new Intent(AddVotingActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cansel.setOnClickListener(v -> {
            Intent intent = new Intent(AddVotingActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_voting;
    }

    @Override
    protected String getToolbarTitle() {
        return "Голосование";
    }

    private void onClick(View v) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getParams(lParams);
        addChoice(lParams);
    }

    private void getParams(LinearLayout.LayoutParams lParams) {
        lParams.gravity = Gravity.LEFT;
    }

    private void addChoice(LinearLayout.LayoutParams lParams) {
        EditText newChoice = new EditText(this);
        newChoice.setHint("Вариант");
        ll_choices.addView(newChoice, lParams);
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
                        Toast.makeText(AddVotingActivity.this, "Ошибка загрузки данных: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
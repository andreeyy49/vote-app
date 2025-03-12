package com.example.firebaselearning.voting.moderation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.MainActivity;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.VotingClient;
import com.example.firebaselearning.staticvalue.StaticValue;
import com.example.firebaselearning.R;

import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class ItemModeratingVotingActivity extends BaseActivity {

    private TextView voting_name;
    private TextView voting_body;
    private Button add_btn;
    private Button cansel_add_btn;
    private LinearLayout ll_choices;

    private String votingKey;

    private Map<String, Integer> choices;

    private Disposable disposable;
    private VotingClient votingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voting_name = findViewById(R.id.community_name);
        voting_body = findViewById(R.id.community_body);
        add_btn = findViewById(R.id.add_btn);
        cansel_add_btn = findViewById(R.id.cansel_add_btn);
        ll_choices = findViewById(R.id.ll_choices);
        votingClient = ApiClient.getInstance(this).create(VotingClient.class);

        getIntentMain();

        disposable = votingClient.findById(votingKey)
                .observeOn(AndroidSchedulers.mainThread()) // Выполняем в UI-потоке
                .subscribe(voting -> {
                    voting_name.setText(voting.getTitle());
                    voting_body.setText(voting.getDescription());

                    choices = voting.getChoices();
                    for (String st : choices.keySet()) {
                        showChoices(st);
                    }
                }, throwable -> {
                    // Обработаем ошибку (например, если голосование не найдено)
                    Intent intent = new Intent(ItemModeratingVotingActivity.this, MainActivity.class);
                    startActivity(intent);
                });

        cansel_add_btn.setOnClickListener(v -> {
            disposable = votingClient.deleteVote(votingKey)
                    .observeOn(AndroidSchedulers.mainThread()) // Обрабатываем результат в UI-потоке
                    .subscribe(() -> {
                        // Успешное удаление, можно добавить лог или уведомление
                        Toast.makeText(ItemModeratingVotingActivity.this, "Голосование удалено", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ItemModeratingVotingActivity.this, ListModeratingVotingActivity.class);
                        startActivity(intent);
                    }, throwable -> {
                        // Обработка ошибки
                        Toast.makeText(ItemModeratingVotingActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        throwable.printStackTrace();
                    });
        });


        add_btn.setOnClickListener(v -> {
            disposable = votingClient.publishVote(votingKey)
                    .observeOn(AndroidSchedulers.mainThread()) // Выполняем в UI-потоке
                    .subscribe(() -> {
                        // Успешная публикация
                        Toast.makeText(ItemModeratingVotingActivity.this, "Голосование опубликовано", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ItemModeratingVotingActivity.this, ListModeratingVotingActivity.class);
                        startActivity(intent);
                    }, throwable -> {
                        // Обработка ошибки
                        Toast.makeText(ItemModeratingVotingActivity.this, "Ошибка публикации", Toast.LENGTH_SHORT).show();
                        throwable.printStackTrace();
                    });
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_item_moderating_voting;
    }

    @Override
    protected String getToolbarTitle() {
        return "Модерация";
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            votingKey = intent.getStringExtra(StaticValue.VOTING_KEY);
        }
    }

    private void showChoices(String choices) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getParams(lParams);
        addChoice(lParams, choices);
    }

    private void getParams(LinearLayout.LayoutParams lParams) {
        lParams.gravity = Gravity.LEFT;
    }

    private void addChoice(LinearLayout.LayoutParams lParams, String choices) {
        TextView newChoice = new TextView(this);
        newChoice.setText(choices);
        ll_choices.addView(newChoice, lParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
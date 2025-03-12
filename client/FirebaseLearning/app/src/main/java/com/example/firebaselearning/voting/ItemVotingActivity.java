package com.example.firebaselearning.voting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.dto.auth.request.CastVoteRequest;
import com.example.firebaselearning.model.Voting;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.VotingClient;
import com.example.firebaselearning.staticvalue.StaticValue;
import com.example.firebaselearning.R;

import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ItemVotingActivity extends BaseActivity {

    private TextView voting_name;
    private TextView voting_body;
    private Button add_btn;
    private Button back;
    private Button result;
    private LinearLayout ll_choices;
    private Voting thisVoting;
    private String votingKey;
    private String typeKey;
    private boolean hasVoted;
    private VotingClient votingClient;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voting_name = findViewById(R.id.community_name);
        voting_body = findViewById(R.id.community_body);
        ll_choices = findViewById(R.id.ll_choices);
        add_btn = findViewById(R.id.add_btn);
        back = findViewById(R.id.back);
        result = findViewById(R.id.result);

        votingClient = ApiClient.getInstance(this).create(VotingClient.class);
        getIntentMain();
        loadVotingData();

        add_btn.setOnClickListener(v -> submitVote());
        result.setOnClickListener(v -> openResults());
        back.setOnClickListener(v -> navigateBack());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_item_voting;
    }

    @Override
    protected String getToolbarTitle() {
        return "Голосование";
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            votingKey = intent.getStringExtra(StaticValue.VOTING_KEY);
            typeKey = intent.getStringExtra(StaticValue.TYPE_KEY);
        }
    }

    private void loadVotingData() {
        disposable = votingClient.findById(votingKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voting -> {
                    thisVoting = voting;
                    voting_name.setText(voting.getTitle());
                    voting_body.setText(voting.getDescription());
                    hasVoted = voting.getCastVote().containsKey(StaticValue.USER_ID);
                    displayChoices();
                }, throwable -> Toast.makeText(this, "Ошибка загрузки голосования", Toast.LENGTH_SHORT).show());
    }

    private void displayChoices() {
        ll_choices.removeAllViews();
        for (String choice : thisVoting.getChoices().keySet()) {
            addChoice(choice);
        }
        if (hasVoted) {
            add_btn.setVisibility(View.INVISIBLE);
            result.setVisibility(View.VISIBLE);
            highlightUserVote();
        } else {
            startListenerChoices();
        }
    }

    private void addChoice(String text) {
        CheckBox newCheckBox = new CheckBox(this);
        newCheckBox.setText(text);
        ll_choices.addView(newCheckBox);
    }

    private void highlightUserVote() {
        String userVote = thisVoting.getCastVote().get(StaticValue.USER_ID);
        for (int i = 0; i < ll_choices.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) ll_choices.getChildAt(i);
            if (checkBox.getText().toString().equals(userVote)) {
                checkBox.setChecked(true);
            }
            checkBox.setClickable(false);
        }
    }

    private void startListenerChoices() {
        for (int i = 0; i < ll_choices.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) ll_choices.getChildAt(i);
            checkBox.setOnClickListener(v -> {
                for (int j = 0; j < ll_choices.getChildCount(); j++) {
                    ((CheckBox) ll_choices.getChildAt(j)).setChecked(false);
                }
                checkBox.setChecked(true);
            });
        }
    }

    private void submitVote() {
        String selectedChoice = null;
        for (int i = 0; i < ll_choices.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) ll_choices.getChildAt(i);
            if (checkBox.isChecked()) {
                selectedChoice = checkBox.getText().toString();
                break;
            }
        }

        if (selectedChoice == null) {
            Toast.makeText(this, "Выберите вариант", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> updatedCastVote = new HashMap<>(thisVoting.getCastVote());
        updatedCastVote.put(StaticValue.USER_ID, selectedChoice);

        HashMap<String, Integer> updatedChoices = new HashMap<>(thisVoting.getChoices());
        if (hasVoted) {
            String previousChoice = thisVoting.getCastVote().get(StaticValue.USER_ID);
            updatedChoices.put(previousChoice, updatedChoices.get(previousChoice) - 1);
        }
        updatedChoices.put(selectedChoice, updatedChoices.getOrDefault(selectedChoice, 0) + 1);

        thisVoting.setCastVote(updatedCastVote);
        thisVoting.setChoices(updatedChoices);

        disposable = votingClient.castVote(new CastVoteRequest(votingKey, selectedChoice))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voting -> {
                    Toast.makeText(this, "Голос учтен", Toast.LENGTH_SHORT).show();
                    navigateBack();
                }, throwable -> Toast.makeText(this, "Ошибка отправки голоса", Toast.LENGTH_SHORT).show());
    }

    private void openResults() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(StaticValue.TYPE_KEY, typeKey);
        intent.putExtra(StaticValue.VOTING_KEY, votingKey);
        startActivity(intent);
    }

    private void navigateBack() {
        Intent intent = new Intent(this, ListVotingActivity.class);
        intent.putExtra(StaticValue.TYPE_KEY, typeKey);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}

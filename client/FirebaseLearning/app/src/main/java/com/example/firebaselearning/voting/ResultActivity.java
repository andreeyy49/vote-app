package com.example.firebaselearning.voting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firebaselearning.BaseActivity;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.VotingClient;
import com.example.firebaselearning.staticvalue.StaticValue;
import com.example.firebaselearning.R;

import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResultActivity extends BaseActivity {

    private Button back;
    private LinearLayout ll_choices;
    private String votingKey;
    private String typeKey;
    private VotingClient votingClient;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ll_choices = findViewById(R.id.ll_choices);
        back = findViewById(R.id.back);

        votingClient = ApiClient.getInstance(this).create(VotingClient.class);

        getIntentMain();
        loadVotingResults();

        back.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, ListVotingActivity.class);
            intent.putExtra(StaticValue.TYPE_KEY, typeKey);
            intent.putExtra(StaticValue.VOTING_KEY, votingKey);
            startActivity(intent);
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_result;
    }

    @Override
    protected String getToolbarTitle() {
        return "Результаты";
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            votingKey = intent.getStringExtra(StaticValue.VOTING_KEY);
            typeKey = intent.getStringExtra(StaticValue.TYPE_KEY);
        }
    }

    private void loadVotingResults() {
        disposable = votingClient.findById(votingKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voting -> {
                    if (voting != null) {
                        displayResults(voting.getChoices());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    private void displayResults(Map<String, Integer> choices) {
        int totalVotes = choices.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : choices.entrySet()) {
            float percentage = (totalVotes == 0) ? 0 : (entry.getValue() * 100f / totalVotes);
            addChoice(entry.getKey() + " - " + String.format("%.2f", percentage) + "%");
        }
    }

    private void addChoice(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(Gravity.LEFT);
        ll_choices.addView(textView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}

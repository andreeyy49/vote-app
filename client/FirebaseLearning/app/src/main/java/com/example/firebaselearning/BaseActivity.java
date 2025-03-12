package com.example.firebaselearning;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.firebaselearning.auth.LoginActivity;
import com.example.firebaselearning.auth.TokenManager;
import com.example.firebaselearning.community.ListCommunityActivity;
import com.example.firebaselearning.model.User;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.CommunityClient;
import com.example.firebaselearning.network.UserClient;
import com.example.firebaselearning.staticvalue.StaticValue;
import com.example.firebaselearning.voting.AddVotingActivity;
import com.example.firebaselearning.voting.ListVotingActivity;
import com.example.firebaselearning.voting.moderation.ListAdminActivity;
import com.example.firebaselearning.voting.moderation.ListModeratingVotingActivity;
import com.example.firebaselearning.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected ImageButton toolbarButton;
    protected TextView toolbarTitle;
    protected DrawerLayout drawerLayout;

    protected TokenManager tokenManager;
    protected CommunityClient communityClient;
    protected UserClient userClient;
    private ActionBarDrawerToggle drawerToggle;
    private String typeKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Загружаем общий макет с Toolbar

        // Инициализация элементов
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);

        // Настройка Toolbar
        setupToolbar();

        // Настройка ActionBarDrawerToggle для свайпа
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,  // Строка для открытия (добавьте в strings.xml)
                R.string.drawer_close  // Строка для закрытия (добавьте в strings.xml)
        );

        // Установка кастомной иконки для гамбургера
        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this);
        drawerArrow.setColor(getIconColorForTheme()); // Установка цвета в зависимости от темы
        drawerToggle.setDrawerArrowDrawable(drawerArrow);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState(); // Синхронизация состояния
        // Разрешаем свайп для открытия бокового меню
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        // Загрузка контента активности
        View contentView = getLayoutInflater().inflate(getLayoutResource(), null);
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        contentFrame.addView(contentView);

        // Инициализация кнопок
        Button doVote_btn = findViewById(R.id.doVote_btn);
        Button result_btn = findViewById(R.id.result_btn);
        Button addVoting_btn = findViewById(R.id.addVoting_btn);
        Button community_btn = findViewById(R.id.community_btn);
        Button admin_btn = findViewById(R.id.admin_btn);
        Button moderating_btn = findViewById(R.id.moderating_btn);
        Button logout_btn = findViewById(R.id.logout_btn);
        Button account_btn = findViewById(R.id.account_btn);

        tokenManager = new TokenManager(this);
        communityClient = ApiClient.getInstance(this).create(CommunityClient.class);
        userClient = ApiClient.getInstance(this).create(UserClient.class);

        userClient.findThisAccount().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String photoUrl = user.getPhoto();

                    ImageView avatarImageView = findViewById(R.id.avatar);

                    Glide.with(avatarImageView.getContext())
                            .load((photoUrl != null && !photoUrl.isEmpty()) ? photoUrl : R.drawable.avatar2) // Если фото нет, грузим дефолтное
                            .apply(RequestOptions.circleCropTransform()) // Делаем круглым в любом случае
                            .into(avatarImageView);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ImageView avatarImageView = findViewById(R.id.avatar);
                Glide.with(avatarImageView.getContext())
                        .load(R.drawable.avatar2) // При ошибке грузим дефолтное фото
                        .apply(RequestOptions.circleCropTransform()) // Делаем круглым
                        .into(avatarImageView);
            }
        });



        // Обработчики нажатий на кнопки
        doVote_btn.setOnClickListener(v -> {
            typeKey = "DoVote";
            Intent intent = new Intent(BaseActivity.this, ListVotingActivity.class);
            intent.putExtra(StaticValue.TYPE_KEY, typeKey);
            startActivity(intent);
        });

        result_btn.setOnClickListener(v -> {
            typeKey = "Result";
            Intent intent = new Intent(BaseActivity.this, ListVotingActivity.class);
            intent.putExtra(StaticValue.TYPE_KEY, typeKey);
            startActivity(intent);
        });

        addVoting_btn.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, AddVotingActivity.class);
            startActivity(intent);
        });

        community_btn.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, ListCommunityActivity.class);
            startActivity(intent);
        });

        admin_btn.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, ListAdminActivity.class);
            startActivity(intent);
        });

        moderating_btn.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, ListModeratingVotingActivity.class);
            startActivity(intent);
        });

        logout_btn.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            tokenManager.clearTokens();
            startActivity(intent);
        });

        account_btn.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Проверка роли пользователя
        checkRole();
    }

    protected abstract int getLayoutResource(); // Метод, который переопределяют наследники

    protected abstract String getToolbarTitle(); // Метод, который переопределяют наследники

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Отключаем стандартное отображение названия
        }

        if (toolbarButton != null) {
            toolbarButton.setOnClickListener(view -> {
                if (drawerLayout.isDrawerOpen(findViewById(R.id.drawerContent))) {
                    drawerLayout.closeDrawer(findViewById(R.id.drawerContent));
                } else {
                    drawerLayout.openDrawer(findViewById(R.id.drawerContent));
                }
            });
        }

        if (toolbarTitle != null) {
            toolbarTitle.setText(getToolbarTitle()); // Устанавливаем название из переопределенного метода
        }
    }

    private void checkRole() {
        communityClient.isAdmin().enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (Boolean.TRUE.equals(response.body())) {
                    findViewById(R.id.admin_btn).setVisibility(View.VISIBLE);
                    findViewById(R.id.moderating_btn).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                showError(t);
            }
        });

        communityClient.isModerator().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (Boolean.TRUE.equals(response.body())) {
                    findViewById(R.id.moderating_btn).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                showError(t);
            }
        });
    }

    private void showError(Throwable t) {
        Log.e("RetrofitError", "Ошибка запроса: " + t.getMessage());
        Toast.makeText(this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private int getIconColorForTheme() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            return getResources().getColor(android.R.color.black);
        } else {
            return getResources().getColor(android.R.color.white);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
            // Разрешаем свайп для открытия бокового меню
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUserAvatar() {
        userClient.findThisAccount().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String photoUrl = user.getPhoto();
                    ImageView avatarImageView = findViewById(R.id.avatar);

                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(avatarImageView.getContext())
                                .load(photoUrl)
                                .placeholder(R.drawable.avatar2)
                                .error(R.drawable.avatar2)
                                .apply(RequestOptions.circleCropTransform())
                                .into(avatarImageView);
                    } else {
                        avatarImageView
                                .setImageResource(R.drawable.avatar2);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ImageView avatarImageView = findViewById(R.id.avatar);
                avatarImageView.setImageResource(R.drawable.avatar2);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUserAvatar(); // Перезапрашиваем данные пользователя и обновляем фото
    }

}
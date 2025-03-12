package com.example.firebaselearning;

import com.example.firebaselearning.R;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main; // Указываем макет для MainActivity
    }

    @Override
    protected String getToolbarTitle() {
        return "Голосовалка"; // Указываем заголовок для Toolbar
    }
}
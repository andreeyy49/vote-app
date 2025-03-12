package com.example.firebaselearning;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.firebaselearning.dto.CityDto;
import com.example.firebaselearning.dto.CountryDto;
import com.example.firebaselearning.dto.ImageFileDto;
import com.example.firebaselearning.model.User;
import com.example.firebaselearning.network.ApiClient;
import com.example.firebaselearning.network.GeoStorageService;
import com.example.firebaselearning.network.UserClient;
import com.example.firebaselearning.utils.CustomArrayAdapter;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView avatarImageView;
    private String userImageUrl;

    private AutoCompleteTextView countryAutoCompleteTextView;
    private AutoCompleteTextView cityAutoCompleteTextView;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    private String selectedCountryName;
    private String selectedCityName;
    private Long selectedCountryId;
    private Long selectedCityId;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        avatarImageView = findViewById(R.id.avatarImageView);
        countryAutoCompleteTextView = findViewById(R.id.countryAutoCompleteTextView);
        cityAutoCompleteTextView = findViewById(R.id.cityAutoCompleteTextView);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);

        loadUserData();

        // Загрузка стран с фильтрацией
        loadCountries();

        // Слушатель для поля стран
        countryAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            CountryDto selectedCountry = (CountryDto) parent.getItemAtPosition(position);
            selectedCountryName = selectedCountry.getTitle();
            selectedCountryId = selectedCountry.getId();
            loadCities(selectedCountryId);  // Загрузить города для выбранной страны
        });

        // Слушатель для поля городов
        cityAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            CityDto selectedCity = (CityDto) parent.getItemAtPosition(position);
            selectedCityName = selectedCity.getTitle();
            selectedCityId = selectedCity.getId();
            Log.d("ProfileActivity", "Выбран город: " + selectedCityName);
        });

        avatarImageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
        });

        findViewById(R.id.saveButton).setOnClickListener(v -> saveUserData());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform()) // Делаем фото круглым
                    .into(avatarImageView);
        }
    }


    private void uploadImageAndSaveUser(Uri imageUri) {
        if (imageUri == null) {
            Log.e("UPLOAD", "Файл не выбран");
            return;
        }

        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        GeoStorageService service = ApiClient.getInstance(this).create(GeoStorageService.class);
        Call<ImageFileDto> call = service.upload(body);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ImageFileDto> call, Response<ImageFileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getFileName();
                    Log.d("UPLOAD", "Картинка загружена: " + imageUrl);
                    userImageUrl = imageUrl;
                    updateUserData(imageUrl);  // После получения URL картинки, обновляем данные пользователя
                } else {
                    Log.e("UPLOAD", "Ошибка: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки картинки", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageFileDto> call, Throwable t) {
                Log.e("UPLOAD", "Ошибка сети: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void loadCountries() {
        GeoStorageService service = ApiClient.getInstance(this).create(GeoStorageService.class);
        Call<List<CountryDto>> call = service.findAllCountries();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<CountryDto>> call, Response<List<CountryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CountryDto> countries = response.body();
                    CustomArrayAdapter<CountryDto> adapter = new CustomArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_dropdown_item_1line, countries);
                    countryAutoCompleteTextView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("ProfileActivity", "Ошибка загрузки стран: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки стран", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CountryDto>> call, Throwable t) {
                Log.e("ProfileActivity", "Ошибка сети: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadCities(Long countryId) {
        GeoStorageService service = ApiClient.getInstance(this).create(GeoStorageService.class);
        Call<List<CityDto>> call = service.findAllCitiesInCountry(countryId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<CityDto>> call, Response<List<CityDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CityDto> cities = response.body();
                    CustomArrayAdapter<CityDto> adapter = new CustomArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_dropdown_item_1line, cities);
                    cityAutoCompleteTextView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("ProfileActivity", "Ошибка загрузки городов: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки городов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CityDto>> call, Throwable t) {
                Log.e("ProfileActivity", "Ошибка сети: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void saveUserData() {
        // Если пользователь выбрал изображение, загружаем его на сервер
        if (imageUri != null) {
            uploadImageAndSaveUser(imageUri);  // Загружаем изображение
        } else {
            // Если изображение не изменилось, обновляем пользователя без изменения фото
            updateUserData(currentUser.getPhoto());  // Отправляем текущее фото
        }
    }


    private void updateUserData(String photoUrl) {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String country = selectedCountryName;
        String city = selectedCityName;

        // Создание объекта User
        User updatedUser = new User();
        updatedUser.setName(name);
        updatedUser.setPhone(phone);
        updatedUser.setEmail(email);
        updatedUser.setPhoto(photoUrl); // Если фото не изменилось, отправляем старое фото
        updatedUser.setCountry(country);
        updatedUser.setCity(city);

        // Отправляем обновленные данные пользователя на сервер
        UserClient userClient = ApiClient.getInstance(this).create(UserClient.class);
        Call<User> call = userClient.update(updatedUser);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d("ProfileActivity", "Данные пользователя обновлены");
                    finish();  // Закрываем активность после успешного обновления
                } else {
                    Log.e("ProfileActivity", "Ошибка обновления данных пользователя");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ProfileActivity", "Ошибка сети при обновлении данных", t);
            }
        });
    }


    private void loadUserData() {
        UserClient userClient = ApiClient.getInstance(this).create(UserClient.class);
        Call<User> call = userClient.findThisAccount();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    // Обновляем UI с данными пользователя
                    updateUIWithUserData();
                } else {
                    Log.e("ProfileActivity", "Ошибка загрузки данных пользователя: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ProfileActivity", "Ошибка сети: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithUserData() {
        if (currentUser != null) {
            // Подставляем данные в поля
            nameEditText.setText(currentUser.getName());
            phoneEditText.setText(currentUser.getPhone());
            emailEditText.setText(currentUser.getEmail());

            // Подставляем страну и город
            selectedCountryName = currentUser.getCountry();
            selectedCityName = currentUser.getCity();

            // Установим страну в AutoCompleteTextView
            countryAutoCompleteTextView.setText(selectedCountryName);
            cityAutoCompleteTextView.setText(selectedCityName);

            // Здесь можно загрузить и отображать фото пользователя
            if (currentUser.getPhoto() != null && !currentUser.getPhoto().isEmpty()) {
                Glide.with(avatarImageView.getContext())
                        .load(currentUser.getPhoto()) // URL изображения
                        .placeholder(R.drawable.avatar2) // Заглушка, пока грузится
                        .error(R.drawable.avatar2) // Если ошибка загрузки
                        .apply(RequestOptions.circleCropTransform()) // Делаем круглым
                        .into(avatarImageView); // Загружаем в ImageView
            }
        }
    }
}

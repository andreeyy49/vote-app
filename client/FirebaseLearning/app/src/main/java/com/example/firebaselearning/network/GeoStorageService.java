package com.example.firebaselearning.network;

import com.example.firebaselearning.dto.CityDto;
import com.example.firebaselearning.dto.CountryDto;
import com.example.firebaselearning.dto.ImageFileDto;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface GeoStorageService {

    @Multipart
    @POST("storage/storageUserImage")
    Call<ImageFileDto> upload(@Part MultipartBody.Part file);

    @GET("geo/country")
    Call<List<CountryDto>> findAllCountries();

    @GET("geo/country/{countryId}/city")
    Call<List<CityDto>> findAllCitiesInCountry(@Path("countryId") Long countryId);

}

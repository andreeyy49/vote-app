package com.example.firebaselearning.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class UserDto implements Parcelable {
    private String id;
    private String email;
    private String name;
    private String password;
    private List<String> rolls;

    public UserDto(String id, String email, String name, String password, List<String> rolls) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.rolls = rolls;
    }

    public UserDto() {
    }

    protected UserDto(Parcel in) {
        id = in.readString();
        email = in.readString();
        name = in.readString();
        password = in.readString();
        rolls = in.createStringArrayList();
    }

    public static final Creator<UserDto> CREATOR = new Creator<UserDto>() {
        @Override
        public UserDto createFromParcel(Parcel in) {
            return new UserDto(in);
        }

        @Override
        public UserDto[] newArray(int size) {
            return new UserDto[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRolls() {
        return rolls;
    }

    public void setRolls(List<String> rolls) {
        this.rolls = rolls;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeStringList(rolls);
    }
}

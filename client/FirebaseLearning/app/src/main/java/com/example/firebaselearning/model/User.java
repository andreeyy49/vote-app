package com.example.firebaselearning.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String name;
    private String password;
    private String photo;
    private String city;
    private String country;
    private String phone;

}

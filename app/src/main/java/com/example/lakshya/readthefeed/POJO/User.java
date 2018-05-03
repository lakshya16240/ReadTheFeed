package com.example.lakshya.readthefeed.POJO;

public class User {

    private String name, age, mobileNumber, gender, email, password;
    

    public User() {
    }

    public User(String name, String age, String mobileNumber, String gender, String email, String password) {
        this.name = name;
        this.age = age;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

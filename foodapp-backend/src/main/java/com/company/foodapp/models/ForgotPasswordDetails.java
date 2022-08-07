package com.company.foodapp.models;

import javax.validation.constraints.Email;

public class ForgotPasswordDetails {
    public String username;
    @Email
    public String email;
    public Long duration;
    public String validationCode;
    public int id;

    public ForgotPasswordDetails() {

    }

    public ForgotPasswordDetails(String username, String email, Long duration, String validationCode, int id) {
        this.username = username;
        this.email = email;
        this.duration = duration;
        this.validationCode = validationCode;
        this.id = id;
    }
}

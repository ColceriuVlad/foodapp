package com.company.foodapp.dto;

import javax.validation.constraints.Email;

public class UserDetails {
    public String subject;
    public String role;
    @Email
    public String email;
    public Long duration;

    public UserDetails(String subject, String role, String email, Long duration) {
        this.subject = subject;
        this.role = role;
        this.duration = duration;
        this.email = email;
    }

    public UserDetails(String subject, String role, String email) {
        this.subject = subject;
        this.role = role;
        this.email = email;
    }

    public UserDetails(){

    }
}

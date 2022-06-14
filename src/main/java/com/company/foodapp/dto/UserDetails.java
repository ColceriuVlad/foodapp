package com.company.foodapp.dto;

public class UserDetails {
    public String subject;
    public String role;
    public Long duration;

    public UserDetails(String subject, String role, Long duration) {
        this.subject = subject;
        this.role = role;
        this.duration = duration;
    }

    public UserDetails(String subject, String role) {
        this.subject = subject;
        this.role = role;
    }
}

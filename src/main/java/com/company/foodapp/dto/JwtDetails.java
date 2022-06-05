package com.company.foodapp.dto;

public class JwtDetails {
    public String id;
    public String subject;
    public String role;
    public Long duration;

    public JwtDetails(String id, String subject, String role, Long duration){
        this.id = id;
        this.subject=subject;
        this.role=role;
        this.duration=duration;
    }
}

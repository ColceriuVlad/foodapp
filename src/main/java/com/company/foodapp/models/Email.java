package com.company.foodapp.models;

public class Email {
    public String to;
    public String subject;
    public String text;

    public Email(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

    public Email() {
    }
}

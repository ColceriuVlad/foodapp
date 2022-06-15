package com.company.foodapp.dto;

import javax.validation.constraints.Email;

public class MessageDetails {
    public String name;
    public String description;
    @Email
    public String email;
}

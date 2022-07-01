package com.company.foodapp.dto;

import javax.validation.constraints.Email;

public class MessageDto {
    public String name;
    public String description;
    @Email
    public String email;
}

package com.magmutual.users.model;

import lombok.Data;

@Data
public class UserRequest {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String profession;
    private String dateCreated;
    private String country;
    private String city;
}


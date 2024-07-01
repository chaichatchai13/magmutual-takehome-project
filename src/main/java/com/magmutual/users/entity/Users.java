package com.magmutual.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.sql.Timestamp;

import lombok.Data;

@Entity
@Data
public class Users {

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String profession;

    @Column(name = "datecreated", nullable = false)
    private Timestamp dateCreated;
    private String country;
    private String city;

}


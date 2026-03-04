package com.example.LegiTrack.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

//    @Column(name = "password")
//    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public UserEntity(String email, /*String password,*/ String firstName, String lastName, String userName, String state) {
        this.email = email;
        //this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.state = state;
        this.createdDate = LocalDateTime.now();
    }
}
package com.example.courseprojectapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String passwordHash;

    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_course",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    @OneToMany(mappedBy = "lecturer")
    private List<Course> lecturedCourses;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Submission> studentSubmissions;

    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL)
    private List<Submission> gradedSubmissions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TokenBlacklist> blacklistedTokens;
    }
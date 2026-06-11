package com.example.courseprojectapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tokenString;

    private LocalDateTime revokedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
package com.example.courseprojectapi.repository;

import com.example.courseprojectapi.model.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByTokenString(String tokenString);
    Boolean existsByTokenString(String tokenString);
}

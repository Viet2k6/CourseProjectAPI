package com.example.courseprojectapi.repository;

import com.example.courseprojectapi.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("select u from User u where " +
           "(:search is null or lower(u.username) like lower(concat('%', :search, '%')) or " +
           "lower(u.email) like lower(concat('%', :search, '%')))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
}

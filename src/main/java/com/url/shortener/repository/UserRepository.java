package com.url.shortener.repository;

import com.url.shortener.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long > {
    Optional<User> findByUserName(String userName); // ✅ works
    Optional<User> findByEmail(String email);
}

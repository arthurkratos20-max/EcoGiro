package com.example.ecogiro.repository;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteAllByUser(AppUser user);
}

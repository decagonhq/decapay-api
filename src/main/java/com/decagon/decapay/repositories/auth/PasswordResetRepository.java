package com.decagon.decapay.repositories.auth;

import com.decagon.decapay.model.auth.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByEmailAndDeviceId(String email, String deviceId);
    Optional<PasswordReset> findByToken(String token);
}

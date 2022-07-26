package com.decagon.decapay.repositories.auth;


import com.decagon.decapay.model.user.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findAuthByUserIdAndDeviceId(Long userId, String deviceId);
}

package com.decagon.decapay.repositories.auth;

import com.decagon.decapay.model.auth.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Integer> {
    boolean existsByToken(String token);
}

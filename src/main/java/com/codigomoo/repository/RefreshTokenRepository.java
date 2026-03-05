package com.codigomoo.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codigomoo.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
  long deleteByExpiresAtBefore(Instant now);
  long deleteByUser_Id(Long userId);
}

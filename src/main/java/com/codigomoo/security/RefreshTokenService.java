package com.codigomoo.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.codigomoo.exception.ApiError;
import com.codigomoo.model.RefreshToken;
import com.codigomoo.model.User;
import com.codigomoo.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshRepo;
  private final SecureRandom random = new SecureRandom();
  private final long expirationDays;

  public RefreshTokenService(RefreshTokenRepository refreshRepo,
                            @Value("${app.refresh.expiration-days:30}") long expirationDays) {
    this.refreshRepo = refreshRepo;
    this.expirationDays = expirationDays;
  }

  /** Devuelve el token RAW (para cookie) y guarda solo HASH en DB */
  @Transactional
  public String issueForUser(User user) {
    String raw = generateRawToken();
    String hash = sha256Hex(raw);

    RefreshToken rt = RefreshToken.builder()
        .user(user)
        .tokenHash(hash)
        .expiresAt(Instant.now().plus(expirationDays, ChronoUnit.DAYS))
        .revoked(false)
        .build();

    refreshRepo.save(rt);
    return raw;
  }

  /**
   * Valida y rota: revoca el actual y emite uno nuevo.
   * Devuelve: usuario + nuevo refresh RAW (para setear cookie).
   */
  @Transactional
  public RefreshResult rotate(String rawToken) {
    if (rawToken == null || rawToken.isBlank()) {
      throw new ApiError(HttpStatus.UNAUTHORIZED, "Missing refresh token");
    }

    String hash = sha256Hex(rawToken);

    RefreshToken current = refreshRepo.findByTokenHashAndRevokedFalse(hash)
        .orElseThrow(() -> new ApiError(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

    if (current.getExpiresAt().isBefore(Instant.now())) {
      current.setRevoked(true);
      current.setLastUsedAt(Instant.now());
      refreshRepo.save(current);
      throw new ApiError(HttpStatus.UNAUTHORIZED, "Refresh token expired");
    }

    // Rotación: revocar el token actual
    current.setLastUsedAt(Instant.now());
    current.setRevoked(true);
    refreshRepo.save(current);

    User user = current.getUser();

    // Emitir uno nuevo para el mismo usuario
    String newRaw = issueForUser(user);

    return new RefreshResult(user, newRaw);
  }

  @Transactional
  public void revokeAllForUser(Long userId) {
    refreshRepo.deleteByUser_Id(userId);
  }

  @Transactional
  public long cleanupExpired() {
    return refreshRepo.deleteByExpiresAtBefore(Instant.now());
  }

  // ---------------- helpers ----------------

  private String generateRawToken() {
    byte[] bytes = new byte[32]; // 256-bit
    random.nextBytes(bytes);
    return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String sha256Hex(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (Exception e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }
}

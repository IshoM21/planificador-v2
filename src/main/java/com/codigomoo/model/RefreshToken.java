package com.codigomoo.model;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
  name = "refresh_tokens",
  indexes = {
    @Index(name = "idx_refresh_user", columnList = "user_id"),
    @Index(name = "idx_refresh_hash", columnList = "token_hash")
  }
)
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "token_hash", nullable = false, length = 64) // sha-256 hex = 64
  private String tokenHash;

  @Column(nullable = false)
  private Instant expiresAt;

  @Builder.Default
  @Column(nullable = false)
  private boolean revoked = false;

  @Builder.Default
  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  private Instant lastUsedAt;
}

package com.codigomoo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profile")
public class UserProfile {

  @Id
  private Long userId;

  @MapsId
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private Theme theme;

  @Column(nullable = false, length = 120)
  private String clubName;
}

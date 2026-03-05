package com.codigomoo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 190)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Column(length = 120)
  private String name;

  @Builder.Default
  @Column(nullable = false)
  private boolean enabled = true;

  // opcional (roles de sistema)
  @Builder.Default
  @Column(nullable = false, length = 30)
  private String role = "USER";
}

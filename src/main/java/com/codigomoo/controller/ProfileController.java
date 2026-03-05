package com.codigomoo.controller;

import org.springframework.web.bind.annotation.*;

import com.codigomoo.dto.*;
import com.codigomoo.security.SecurityUtils;
import com.codigomoo.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

  private final AuthService authService;

  public ProfileController(AuthService authService) {
    this.authService = authService;
  }

  @PutMapping
  public MeResponse update(@Valid @RequestBody ProfileUpdateRequest req) {
    return authService.updateProfile(SecurityUtils.currentUserId(), req);
  }
}

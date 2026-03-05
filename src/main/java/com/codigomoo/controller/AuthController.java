package com.codigomoo.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codigomoo.dto.*;
import com.codigomoo.security.AuthCookies;
import com.codigomoo.security.SecurityUtils;
import com.codigomoo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final AuthCookies authCookies;

  public AuthController(AuthService authService, AuthCookies authCookies) {
    this.authService = authService;
    this.authCookies = authCookies;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
    AuthPair pair = authService.register(req);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE,
        authCookies.refreshCookie(pair.refreshTokenRaw(), Duration.ofDays(30)).toString());

    return ResponseEntity.ok()
        .headers(headers)
        .body(pair.auth());
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
    AuthPair pair = authService.login(req);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE,
        authCookies.refreshCookie(pair.refreshTokenRaw(), Duration.ofDays(30)).toString());

    return ResponseEntity.ok()
        .headers(headers)
        .body(pair.auth());
  }

  /**
   * Rota refresh token (cookie HttpOnly) y entrega nuevo access token.
   * En el front, llama este endpoint con credentials: "include".
   */
  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request) {
    String refreshRaw = readCookie(request, AuthCookies.REFRESH_COOKIE);

    RefreshTokensResponse tokens = authService.refresh(refreshRaw);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE,
        authCookies.refreshCookie(tokens.newRefreshToken(), Duration.ofDays(30)).toString());

    // Devuelve solo accessToken (no necesitas exponer refresh en JSON)
    return ResponseEntity.ok()
        .headers(headers)
        .body(Map.of("accessToken", tokens.accessToken()));
  }

  /**
   * Logout: revoca refresh tokens del usuario actual y borra cookie.
   * Este endpoint requiere access token (Bearer) porque usa currentUserId().
   */
  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    Long userId = SecurityUtils.currentUserId();
    authService.logout(userId);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, authCookies.clearRefreshCookie().toString());

    return ResponseEntity.ok().headers(headers).build();
  }

  @GetMapping("/me")
  public MeResponse me() {
    return authService.me(SecurityUtils.currentUserId());
  }

  // -----------------------
  // Helpers
  // -----------------------

  private String readCookie(HttpServletRequest req, String name) {
    Cookie[] cookies = req.getCookies();
    if (cookies == null) return null;

    for (Cookie c : cookies) {
      if (name.equals(c.getName())) {
        return c.getValue();
      }
    }
    return null;
  }
}

package com.codigomoo.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookies {

  public static final String REFRESH_COOKIE = "REFRESH_TOKEN";

  private final String cookieDomain;
  private final boolean secure;
  private final String sameSite;

  public AuthCookies(@Value("${app.cookie.domain:}") String cookieDomain,
                     @Value("${app.cookie.secure:true}") boolean secure,
                     @Value("${app.cookie.same-site:None}") String sameSite) {
    this.cookieDomain = cookieDomain;
    this.secure = secure;
    this.sameSite = sameSite;
  }

  public ResponseCookie refreshCookie(String rawToken, Duration maxAge) {
    ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(REFRESH_COOKIE, rawToken)
        .httpOnly(true)
        .secure(secure)
        .path("/api/auth/refresh") // solo se manda a refresh
        .maxAge(maxAge)
        .sameSite(sameSite);

    // Si domain está vacío (local), no lo setees (host-only)
    if (cookieDomain != null && !cookieDomain.isBlank()) {
      b.domain(cookieDomain);
    }
    return b.build();
  }

  public ResponseCookie clearRefreshCookie() {
    ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(REFRESH_COOKIE, "")
        .httpOnly(true)
        .secure(secure)
        .path("/api/auth/refresh")
        .maxAge(0)
        .sameSite(sameSite);

    if (cookieDomain != null && !cookieDomain.isBlank()) {
      b.domain(cookieDomain);
    }
    return b.build();
  }
}

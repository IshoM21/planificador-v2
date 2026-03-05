package com.codigomoo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  public static Long currentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal p)) {
      throw new IllegalStateException("No authenticated user");
    }
    return p.getId();
  }

  public static UserPrincipal principal() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal p)) {
      throw new IllegalStateException("No authenticated user");
    }
    return p;
  }
}

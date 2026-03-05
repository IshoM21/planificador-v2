package com.codigomoo.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.codigomoo.repository.UserRepository;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepo;

  public JwtAuthFilter(JwtService jwtService, UserRepository userRepo) {
    this.jwtService = jwtService;
    this.userRepo = userRepo;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String header = req.getHeader("Authorization");
    if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
      chain.doFilter(req, res);
      return;
    }

    String token = header.substring(7);
    try {
      Long userId = jwtService.getUserId(token);

      var userOpt = userRepo.findById(userId);
      if (userOpt.isEmpty()) {
        chain.doFilter(req, res);
        return;
      }

      var principal = new UserPrincipal(userOpt.get());
      var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
      auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
      SecurityContextHolder.getContext().setAuthentication(auth);

    } catch (Exception ignored) {
      // token inválido -> request seguirá como no autenticado
    }

    chain.doFilter(req, res);
  }
}

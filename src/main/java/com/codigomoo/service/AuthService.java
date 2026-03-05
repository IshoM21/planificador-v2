package com.codigomoo.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codigomoo.dto.AuthPair;
import com.codigomoo.dto.AuthResponse;
import com.codigomoo.dto.LoginRequest;
import com.codigomoo.dto.MeResponse;
import com.codigomoo.dto.ProfileUpdateRequest;
import com.codigomoo.dto.RefreshTokensResponse;
import com.codigomoo.dto.RegisterRequest;
import com.codigomoo.exception.ApiError;
import com.codigomoo.model.User;
import com.codigomoo.model.UserProfile;
import com.codigomoo.repository.UserProfileRepository;
import com.codigomoo.repository.UserRepository;
import com.codigomoo.security.JwtService;
import com.codigomoo.security.RefreshResult;
import com.codigomoo.security.RefreshTokenService;
import com.codigomoo.security.UserPrincipal;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

  private final UserRepository userRepo;
  private final UserProfileRepository profileRepo;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public AuthService(UserRepository userRepo,
                     UserProfileRepository profileRepo,
                     PasswordEncoder encoder,
                     AuthenticationManager authManager,
                     JwtService jwtService,
                     RefreshTokenService refreshTokenService) {
    this.userRepo = userRepo;
    this.profileRepo = profileRepo;
    this.encoder = encoder;
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
  }

  @Transactional
  public AuthPair register(RegisterRequest req) {
    if (userRepo.existsByEmail(req.email())) {
      throw new ApiError(HttpStatus.CONFLICT, "Email ya registrado");
    }

    User u = User.builder()
        .email(req.email().toLowerCase().trim())
        .passwordHash(encoder.encode(req.password()))
        .name(req.name())
        .role("USER")
        .enabled(true)
        .build();

    u = userRepo.save(u);

    UserProfile profile = UserProfile.builder()
        .user(u)
        .theme(req.theme())
        .clubName(req.clubName())
        .build();

    profileRepo.save(profile);

    // ACCESS token (JSON)
    String access = accessTokenFor(u);

    // REFRESH token (cookie HttpOnly) -> raw token regresa al controller
    String refreshRaw = refreshTokenService.issueForUser(u);

    AuthResponse auth = new AuthResponse(access, toMe(u, profile));
    return new AuthPair(auth, refreshRaw);
  }

  public AuthPair login(LoginRequest req) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.email(), req.password())
    );

    UserPrincipal p = (UserPrincipal) auth.getPrincipal();
    User u = p.getUser();

    UserProfile profile = profileRepo.findById(u.getId())
        .orElseThrow(() -> new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Perfil no encontrado"));

    // ACCESS token (JSON)
    String access = accessTokenFor(u);

    // REFRESH token (cookie HttpOnly)
    String refreshRaw = refreshTokenService.issueForUser(u);

    AuthResponse payload = new AuthResponse(access, toMe(u, profile));
    return new AuthPair(payload, refreshRaw);
  }

  /**
   * Usa refresh token RAW (desde cookie) para rotar y devolver:
   * - nuevo access token
   * - nuevo refresh token RAW (para setear cookie)
   */
  @Transactional
  public RefreshTokensResponse refresh(String refreshRaw) {
    RefreshResult result = refreshTokenService.rotate(refreshRaw);

    String access = accessTokenFor(result.user());
    return new RefreshTokensResponse(access, result.newRefreshRaw());
  }

  @Transactional
  public void logout(Long userId) {
    refreshTokenService.revokeAllForUser(userId);
  }

  public MeResponse me(Long userId) {
    User u = userRepo.findById(userId)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "User not found"));

    UserProfile profile = profileRepo.findById(userId)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Profile not found"));

    return toMe(u, profile);
  }

  @Transactional
  public MeResponse updateProfile(Long userId, ProfileUpdateRequest req) {
    UserProfile profile = profileRepo.findById(userId)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Profile not found"));

    profile.setTheme(req.theme());
    profile.setClubName(req.clubName());
    profileRepo.save(profile);

    User u = profile.getUser();
    return toMe(u, profile);
  }

  private MeResponse toMe(User u, UserProfile p) {
    return new MeResponse(u.getId(), u.getEmail(), u.getName(), u.getRole(), p.getTheme(), p.getClubName());
  }

  private String accessTokenFor(User u) {
    return jwtService.generateToken(u.getId(), u.getEmail(), u.getRole());
  }
}

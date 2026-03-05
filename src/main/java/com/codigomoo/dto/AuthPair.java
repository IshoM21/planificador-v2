package com.codigomoo.dto;

public record AuthPair(AuthResponse auth, String refreshTokenRaw) {}

package com.codigomoo.dto;

public record AuthResponse(
    String accessToken,
    MeResponse me
) {}

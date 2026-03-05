package com.codigomoo.dto;

import com.codigomoo.model.Theme;

public record MeResponse(
    Long id,
    String email,
    String name,
    String role,
    Theme theme,
    String clubName
) {}

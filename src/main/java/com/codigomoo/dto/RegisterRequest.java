package com.codigomoo.dto;

import com.codigomoo.model.Theme;

import jakarta.validation.constraints.*;

public record RegisterRequest(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 6, max = 72) String password,
    @NotBlank @Size(max = 120) String name,
    @NotNull Theme theme,
    @NotBlank @Size(max = 120) String clubName
) {}

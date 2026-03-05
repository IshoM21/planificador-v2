package com.codigomoo.dto;

import com.codigomoo.model.Theme;
import jakarta.validation.constraints.*;

public record ProfileUpdateRequest(
    @NotNull Theme theme,
    @NotBlank @Size(max = 120) String clubName
) {}

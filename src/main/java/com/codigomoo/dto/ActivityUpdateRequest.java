package com.codigomoo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public record ActivityUpdateRequest(
		LocalDate fechaInicio,
	    LocalDate fechaFin,

	    @Size(max = 20) String dia,
	    @Size(max = 120) String actividad,
	    @Size(max = 120) String responsable,

	    String desarrollo,

	    @Size(max = 120) String lugar,

	    BigDecimal costo,

	    Integer orderIndex
) {}

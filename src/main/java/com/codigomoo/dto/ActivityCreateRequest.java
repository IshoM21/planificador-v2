package com.codigomoo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.AssertTrue;

public record ActivityCreateRequest(

		@NotNull(message = "year es requerido") 
		@Min(value = 2000, message = "year inválido") 
		@Max(value = 2100, message = "year inválido") 
		Integer year,

		@NotNull(message = "month es requerido") @Min(value = 1, message = "month debe estar entre 1 y 12") @Max(value = 12, message = "month debe estar entre 1 y 12") Integer month,
		@NotNull(message = "fechaInicio es requerida")
		LocalDate fechaInicio, 
		LocalDate fechaFin,

		@Size(max = 20, message = "dia máximo 20 caracteres") String dia,

		@NotBlank(message = "actividad es requerida") @Size(max = 120, message = "actividad máximo 120 caracteres") String actividad,

		@Size(max = 120, message = "responsable máximo 120 caracteres") @NotNull(message = "responsable es requerido") String responsable,

		@Size(max = 20000, message = "desarrollo demasiado largo") String desarrollo,

		@Size(max = 120, message = "lugar máximo 120 caracteres") String lugar,

		@PositiveOrZero(message = "costo debe ser mayor o igual a 0") BigDecimal costo,

		@NotNull(message = "orderIndex es requerido") @Min(value = 0, message = "orderIndex no puede ser negativo") Integer orderIndex

) {

	/**
	 * Validación de rango de fechas.
	 */
	@AssertTrue(message = "fechaFin no puede ser menor que fechaInicio")
	public boolean isFechaValida() {
		if (fechaInicio == null || fechaFin == null) {
			return true;
		}
		return !fechaFin.isBefore(fechaInicio);
	}

	/**
	 * Validación opcional: Si hay fechaInicio, que coincida con el mes enviado.
	 */
	@AssertTrue(message = "El mes no coincide con fechaInicio")
	public boolean isMesConsistente() {
		return fechaInicio.getMonthValue() == month;
	}
}
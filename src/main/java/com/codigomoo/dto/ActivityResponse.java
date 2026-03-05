package com.codigomoo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.codigomoo.model.Activity;

public record ActivityResponse(
	    Long id,
	    Integer year,
	    Integer month,
	    LocalDate fechaInicio,
	    LocalDate fechaFin,
	    String fechaDisplay,
	    String dia,
	    String actividad,
	    String responsable,
	    String desarrollo,
	    String lugar,
	    BigDecimal costo,
	    Integer orderIndex
	) {
	  public static ActivityResponse from(Activity a) {
	    return new ActivityResponse(
	        a.getId(),
	        a.getYear(),
	        a.getMonth(),
	        a.getFechaInicio(),
	        a.getFechaFin(),
	        a.getFechaDisplay(),
	        a.getDia(),
	        a.getActividad(),
	        a.getResponsable(),
	        a.getDesarrollo(),
	        a.getLugar(),
	        a.getCosto(),
	        a.getOrderIndex()
	    );
	  }
	}

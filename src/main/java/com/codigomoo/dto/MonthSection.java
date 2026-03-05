package com.codigomoo.dto;

import java.util.List;

import com.codigomoo.model.Activity;

public record MonthSection(
	    Integer month,          // 1-12
	    String nombreMes,       // "ENERO 2026"
	    List<Activity> actividades
) {}

package com.codigomoo.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "activity")
public class Activity {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @Column(name = "plan_year", nullable = false)
	  private Integer year;

	  @Column(name = "plan_month", nullable = false)
	  private Integer month; // 1-12

	  @Column(name = "fecha_inicio")
	  private LocalDate fechaInicio;

	  @Column(name = "fecha_fin")
	  private LocalDate fechaFin; // nullable

	  @Column(length = 20)
	  private String dia;

	  @Column(length = 120)
	  private String actividad;

	  @Column(length = 120)
	  private String responsable;

	  @Column(columnDefinition = "TEXT")
	  private String desarrollo;

	  @Column(length = 120)
	  private String lugar;

	  private BigDecimal costo;

	  @Column(nullable = false)
	  private Integer orderIndex = 0;

	  @Column(nullable = false)
	  private OffsetDateTime createdAt = OffsetDateTime.now();

	  @Column(nullable = false)
	  private OffsetDateTime updatedAt = OffsetDateTime.now();

	  @PreUpdate
	  void preUpdate() {
	    this.updatedAt = OffsetDateTime.now();
	  }
	  
	  @ManyToOne(fetch = FetchType.LAZY, optional = false)
	  @JoinColumn(name = "owner_user_id", nullable = false)
	  private User owner;

	  @Transient
	  public String getFechaDisplay() {
	    if (fechaInicio == null) return "";
	    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    if (fechaFin == null || fechaFin.equals(fechaInicio)) return fechaInicio.format(fmt);
	    return fechaInicio.format(fmt) + " - " + fechaFin.format(fmt);
	  }

	  


}

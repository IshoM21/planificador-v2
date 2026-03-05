package com.codigomoo.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codigomoo.dto.ActivityCreateRequest;
import com.codigomoo.dto.ActivityUpdateRequest;
import com.codigomoo.exception.ApiError;
import com.codigomoo.model.Activity;
import com.codigomoo.model.User;
import com.codigomoo.repository.ActivityRepository;

@Service
public class ActivityService {

	private final ActivityRepository repo;

	public ActivityService(ActivityRepository repo) {
		this.repo = repo;
	}

	@Transactional
	public Activity create(Long userId, ActivityCreateRequest req) {
		validateFechas(req.fechaInicio(), req.fechaFin());

		Activity a = new Activity();
		a.setOwner(User.builder().id(userId).build());
		a.setYear(req.year());
		a.setMonth(req.month());
		a.setFechaInicio(req.fechaInicio());
		a.setFechaFin(req.fechaFin());
		a.setDia(req.dia());
		a.setActividad(req.actividad());
		a.setResponsable(req.responsable());
		a.setDesarrollo(req.desarrollo());
		a.setLugar(req.lugar());
		a.setCosto(req.costo());
		a.setOrderIndex(req.orderIndex());

		return repo.save(a);
	}

	@Transactional(readOnly = true)
	public List<Activity> listByMonth(Long userId, Integer year, Integer month) {
		// return repo.findByYearAndMonthOrderByOrderIndexAscFechaInicioAsc(year,
		// month);
		var list = repo.listMonth(userId, year, month);
		return sortActivities(list);
	}

	@Transactional(readOnly = true)
	public List<Activity> listByYear(Long userId, Integer year) {
		// return repo.findByYearAndMonthOrderByOrderIndexAscFechaInicioAsc(year,
		// month);
		var list = repo.listYear(userId, year);
		return sortActivities(list);
	}

	@Transactional
	public Activity update(Long id, Long userId, ActivityUpdateRequest req) {
		// Activity a = repo.findById(id)
		// .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Activity not found"));

		Activity a = repo.findByIdAndOwner_Id(id, userId)
				.orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Activity not found"));
		// construir "futuro" inicio/fin para validar bien
		LocalDate newInicio = req.fechaInicio() != null ? req.fechaInicio() : a.getFechaInicio();
		LocalDate newFin = req.fechaFin() != null ? req.fechaFin() : a.getFechaFin();
		validateFechas(newInicio, newFin);

		if (req.fechaInicio() != null)
			a.setFechaInicio(req.fechaInicio());
		if (req.fechaFin() != null)
			a.setFechaFin(req.fechaFin());
		if (req.dia() != null)
			a.setDia(req.dia());
		if (req.actividad() != null)
			a.setActividad(req.actividad());
		if (req.responsable() != null)
			a.setResponsable(req.responsable());
		if (req.desarrollo() != null)
			a.setDesarrollo(req.desarrollo());
		if (req.lugar() != null)
			a.setLugar(req.lugar());
		if (req.costo() != null)
			a.setCosto(req.costo());
		if (req.orderIndex() != null)
			a.setOrderIndex(req.orderIndex());

		return repo.save(a);
	}

	@Transactional
	public void delete(Long userId, Long id) {
		if (!repo.existsByIdAndOwner_Id(id, userId)) {
			throw new ApiError(HttpStatus.NOT_FOUND, "Activity not found");
		}
		repo.deleteById(id);
	}

	private void validateFechas(LocalDate inicio, LocalDate fin) {
		if (inicio != null && fin != null && fin.isBefore(inicio)) {
			throw new ApiError(HttpStatus.BAD_REQUEST, "fechaFin no puede ser anterior a fechaInicio");
		}
	}

	private static final Comparator<Activity> ACTIVITY_ORDER = Comparator
			.comparing(Activity::getFechaInicio, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Activity::getFechaFin, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Activity::getDia, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
			.thenComparing(Activity::getActividad, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
			.thenComparing(Activity::getId);

	private List<Activity> sortActivities(List<Activity> list) {
		return list.stream().sorted(ACTIVITY_ORDER).toList();
	}
}

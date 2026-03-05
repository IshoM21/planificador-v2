package com.codigomoo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codigomoo.dto.ActivityCreateRequest;
import com.codigomoo.dto.ActivityResponse;
import com.codigomoo.dto.ActivityUpdateRequest;
import com.codigomoo.model.Activity;
import com.codigomoo.security.SecurityUtils;
import com.codigomoo.service.ActivityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ActivityController {
	
	 
	 private final ActivityService service;


	  public ActivityController(ActivityService service) {
	    this.service = service;
	  }

	  @PostMapping("/activities")
	  public ActivityResponse create(@Valid @RequestBody ActivityCreateRequest req) {
		Long userId = SecurityUtils.currentUserId();
	    Activity a = service.create(userId, req);
	    return ActivityResponse.from(a);
	  }

	  @GetMapping("/months/{year}/{month}/activities")
	  public List<ActivityResponse> list(@PathVariable Integer year, @PathVariable Integer month) {
		  Long userId = SecurityUtils.currentUserId();
	    return service.listByMonth(userId, year, month).stream().map(ActivityResponse::from).toList();
	  }
	  
	  @GetMapping("/months/{year}/activities")
	  public List<ActivityResponse> listYear(@PathVariable Integer year) {
		  Long userId = SecurityUtils.currentUserId();
	    return service.listByYear(userId, year).stream().map(ActivityResponse::from).toList();
	  }

	  @PutMapping("/activities/{id}")
	  public ActivityResponse update(@PathVariable Long id, @Valid @RequestBody ActivityUpdateRequest req) {
		  
		  Long userId = SecurityUtils.currentUserId();
		  
	    Activity a = service.update(id, userId, req);
	    
	    return ActivityResponse.from(a);
	  }

	  @DeleteMapping("/activities/{id}")
	  public void delete(@PathVariable Long id) {
		  Long userId = SecurityUtils.currentUserId();
	    service.delete(userId, id);
	  }
}

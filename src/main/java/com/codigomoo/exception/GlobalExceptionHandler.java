package com.codigomoo.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(ApiError.class)
	  public ResponseEntity<?> handleApiError(ApiError e) {
	    return ResponseEntity.status(e.getStatus())
	        .body(Map.of("message", e.getMessage()));
	  }

	  @ExceptionHandler(MethodArgumentNotValidException.class)
	  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
	    return ResponseEntity.badRequest()
	        .body(Map.of("message", "Validation error", "details",
	            e.getBindingResult().getFieldErrors().stream()
	                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
	                .toList()
	        ));
	  }

	  @ExceptionHandler(Exception.class)
	  public ResponseEntity<?> handleGeneric(Exception e){
		  log.error("Unhandled exception" , e);
		  
		  return ResponseEntity.internalServerError()
				  .body(Map.of(
						  "message","Internal Error",
						  "detail", e.getClass().getName() + ": " + e.getMessage()
						  ));
	  }
	  
	  
	  
}

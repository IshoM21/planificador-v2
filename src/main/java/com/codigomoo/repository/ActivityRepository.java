package com.codigomoo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codigomoo.model.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

//	List<Activity> findByYearAndMonthOrderByOrderIndexAscFechaInicioAsc(Integer year, Integer month);
//
//	@Query("""
//			  select a from Activity a
//			  where a.year = :year and a.month = :month
//			  order by a.orderIndex asc, a.fechaInicio asc
//			""")
//	List<Activity> listMonth(@Param("year") Integer year, @Param("month") Integer month);
//
//	@Query("""
//			  select a from Activity a
//			  where a.year = :year
//			  order by a.month asc, a.orderIndex asc, a.fechaInicio asc
//			""")
//	List<Activity> listYear(@Param("year") Integer year);
//	
	
	@Query("""
		    select a from Activity a
		    where a.owner.id = :userId and a.year = :year and a.month = :month
		    order by a.orderIndex asc, a.fechaInicio asc
		  """)
		  List<Activity> listMonth(@Param("userId") Long userId,
		                           @Param("year") Integer year,
		                           @Param("month") Integer month);


	@Query("""
		    select a from Activity a
		    where a.owner.id = :userId and a.year = :year
		    order by a.month asc, a.orderIndex asc, a.fechaInicio asc
		  """)
		  List<Activity> listYear(@Param("userId") Long userId,
		                          @Param("year") Integer year);

	Optional<Activity> findByIdAndOwner_Id(Long id, Long userId);

	boolean existsByIdAndOwner_Id(Long id, Long userId);

}

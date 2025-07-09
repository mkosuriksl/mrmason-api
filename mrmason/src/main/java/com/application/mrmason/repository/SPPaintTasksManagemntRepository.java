package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.SPPaintTasksManagemnt;

public interface SPPaintTasksManagemntRepository extends JpaRepository<SPPaintTasksManagemnt, String>{

	@Query("SELECT COUNT(e) FROM SPPaintTasksManagemnt e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
	long countByPrefix(@Param("prefix") String prefix);
	
//	@Query("SELECT t FROM SPPaintTasksManagemnt t WHERE "
//		     + "(:serviceCategory IS NULL OR t.serviceCategory = :serviceCategory) AND "
//		     + "(:taskId IS NULL OR t.taskId = :taskId) AND "
//		     + "(:taskName IS NULL OR t.taskName = :taskName)")
//		List<SPPaintTasksManagemnt> findByFilters(
//		    @Param("serviceCategory") String serviceCategory,
//		    @Param("taskId") String taskId,
//		    @Param("taskName") String taskName);
	
	@Query("SELECT s FROM SPPaintTasksManagemnt s " +
		       "WHERE (:serviceCategory IS NULL OR s.serviceCategory = :serviceCategory) " +
		       "AND (:taskId IS NULL OR s.taskId = :taskId) " +
		       "AND (:taskName IS NULL OR s.taskName = :taskName)")
		Page<SPPaintTasksManagemnt> findByFilters(@Param("serviceCategory") String serviceCategory,
		                                          @Param("taskId") String taskId,
		                                          @Param("taskName") String taskName,
		                                          Pageable pageable);



}

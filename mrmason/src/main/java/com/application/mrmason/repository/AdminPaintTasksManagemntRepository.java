package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminPaintTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface AdminPaintTasksManagemntRepository extends JpaRepository<AdminPaintTasksManagemnt, String>{
	@Query("SELECT t FROM AdminPaintTasksManagemnt t WHERE "
		     + "(:serviceCategory IS NULL OR t.serviceCategory = :serviceCategory) AND "
		     + "(:taskId IS NULL OR t.taskId = :taskId) AND "
		     + "(:taskName IS NULL OR t.taskName = :taskName)")
		List<AdminPaintTasksManagemnt> findByFilters(
		    @Param("serviceCategory") String serviceCategory,
		    @Param("taskId") String taskId,
		    @Param("taskName") String taskName);
	
	@Query("SELECT COUNT(e) FROM AdminPaintTasksManagemnt e WHERE e.adminTaskId LIKE :prefix%")
	long countByPrefix(@Param("prefix") String prefix);
	

}

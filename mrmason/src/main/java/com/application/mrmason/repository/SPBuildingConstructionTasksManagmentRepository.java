package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.SPBuildingConstructionTasksManagment;

public interface SPBuildingConstructionTasksManagmentRepository extends JpaRepository<SPBuildingConstructionTasksManagment, String> {

	@Query("SELECT COUNT(e) FROM SPBuildingConstructionTasksManagment e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
	long countByPrefix(@Param("prefix") String prefix);
	
	 @Query("SELECT t FROM SPBuildingConstructionTasksManagment t WHERE "
		     + "(:serviceCategory IS NULL OR t.serviceCategory = :serviceCategory) AND "
		     + "(:taskId IS NULL OR t.taskId = :taskId) AND "
		     + "(:taskName IS NULL OR t.taskName = :taskName)")
		List<SPBuildingConstructionTasksManagment> findByFilters(
		    @Param("serviceCategory") String serviceCategory,
		    @Param("taskId") String taskId,
		    @Param("taskName") String taskName);

}

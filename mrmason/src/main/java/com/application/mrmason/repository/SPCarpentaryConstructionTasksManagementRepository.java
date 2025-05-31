package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.SPCarpentaryConstructionTasksManagement;
import com.application.mrmason.entity.SPPaintTasksManagemnt;

public interface SPCarpentaryConstructionTasksManagementRepository extends JpaRepository<SPCarpentaryConstructionTasksManagement, String>{

	@Query("SELECT COUNT(e) FROM SPCarpentaryConstructionTasksManagement e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
	long countByPrefix(@Param("prefix") String prefix);

	@Query("SELECT t FROM SPCarpentaryConstructionTasksManagement t WHERE "
		     + "(:serviceCategory IS NULL OR t.serviceCategory = :serviceCategory) AND "
		     + "(:taskId IS NULL OR t.taskId = :taskId) AND "
		     + "(:taskName IS NULL OR t.taskName = :taskName)")
		List<SPCarpentaryConstructionTasksManagement> findByFilters(
		    @Param("serviceCategory") String serviceCategory,
		    @Param("taskId") String taskId,
		    @Param("taskName") String taskName);
}

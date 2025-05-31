package com.application.mrmason.repository;

import com.application.mrmason.entity.SPBuildingConstructionTasksManagment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SPBuildingConstructionTasksManagmentRepository extends JpaRepository<SPBuildingConstructionTasksManagment, String> {

	@Query("SELECT COUNT(e) FROM SPBuildingConstructionTasksManagment e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
	long countByPrefix(@Param("prefix") String prefix);

}

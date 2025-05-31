package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.SPCarpentaryConstructionTasksManagement;

public interface SPCarpentaryConstructionTasksManagementRepository extends JpaRepository<SPCarpentaryConstructionTasksManagement, String>{

	@Query("SELECT COUNT(e) FROM SPCarpentaryConstructionTasksManagement e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
	long countByPrefix(@Param("prefix") String prefix);

}

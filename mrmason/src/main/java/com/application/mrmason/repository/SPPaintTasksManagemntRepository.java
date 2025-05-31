package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.SPPaintTasksManagemnt;

public interface SPPaintTasksManagemntRepository extends JpaRepository<SPPaintTasksManagemnt, String>{

	@Query("SELECT COUNT(e) FROM SPPaintTasksManagemnt e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
	long countByPrefix(@Param("prefix") String prefix);


}

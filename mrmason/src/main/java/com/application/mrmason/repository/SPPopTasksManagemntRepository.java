package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.application.mrmason.entity.SPPopTasksManagemnt;

public interface SPPopTasksManagemntRepository extends JpaRepository<SPPopTasksManagemnt, String>{
    @Query("SELECT COUNT(e) FROM SPPopTasksManagemnt e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
    long countByPrefix(@Param("prefix") String prefix);
}

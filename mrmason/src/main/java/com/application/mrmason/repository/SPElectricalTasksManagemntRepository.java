package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.application.mrmason.entity.SPElectricalTasksManagemnt;

public interface SPElectricalTasksManagemntRepository extends JpaRepository<SPElectricalTasksManagemnt, String>{
    @Query("SELECT COUNT(e) FROM SPElectricalTasksManagemnt e WHERE e.userIdServiceCategoryTaskId LIKE :prefix%")
    long countByPrefix(@Param("prefix") String prefix);
}

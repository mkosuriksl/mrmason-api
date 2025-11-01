package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.SPAvailabilityHistory;

@Repository
public interface SPAvailabilityHistoryRepository extends JpaRepository<SPAvailabilityHistory, Integer> {
}

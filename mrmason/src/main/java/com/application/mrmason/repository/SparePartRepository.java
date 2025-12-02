package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.dto.SparePartEntity;

public interface SparePartRepository extends JpaRepository<SparePartEntity, String> {
}


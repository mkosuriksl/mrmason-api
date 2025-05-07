package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.application.mrmason.entity.SiteMeasurement;

public interface SiteMeasurementRepository extends JpaRepository<SiteMeasurement, String> {
    SiteMeasurement findByServiceRequestId(String serviceRequestId);
}
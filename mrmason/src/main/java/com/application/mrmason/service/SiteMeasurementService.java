package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.SiteMeasurement;

public interface SiteMeasurementService {
	SiteMeasurement addSiteMeasurement(SiteMeasurement measurement);
    SiteMeasurement updateSiteMeasurement(SiteMeasurement measurement);
    SiteMeasurement findByServiceRequestId(String serviceRequestId);
//    public List<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location);
    public Page<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location, Pageable pageable);
}
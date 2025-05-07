package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.SiteMeasurement;

public interface SiteMeasurementService {
	SiteMeasurement addSiteMeasurement(SiteMeasurement measurement);
    SiteMeasurement updateSiteMeasurement(SiteMeasurement measurement);
    SiteMeasurement findByServiceRequestId(String serviceRequestId);
    public List<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location);
}
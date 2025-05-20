package com.application.mrmason.service;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.UpdateSiteMeasurementStatusRequestDTO;
import com.application.mrmason.dto.UpdateSiteMeasurementStatusResponseDTO;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.enums.RegSource;

public interface SiteMeasurementService {
	SiteMeasurement addSiteMeasurement(SiteMeasurement measurement,RegSource regSource);
    SiteMeasurement updateSiteMeasurement(SiteMeasurement measurement,RegSource regSource);
    SiteMeasurement findByServiceRequestId(String serviceRequestId);
//    public List<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location);
    public Page<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location,String userId,Date fromRequestDate, Date toRequestDate,String expectedFromMonth,
            String expectedToMonth, Pageable pageable);
    public UpdateSiteMeasurementStatusResponseDTO updateStatus(UpdateSiteMeasurementStatusRequestDTO dto,RegSource regSource);
}
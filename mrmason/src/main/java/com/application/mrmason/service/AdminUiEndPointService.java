package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.AdminUiEndPointDto;
import com.application.mrmason.entity.AdminUiEndPoint;

public interface AdminUiEndPointService {

	public AdminUiEndPointDto createAdminAcademicQualification(AdminUiEndPointDto adminUiEndPointDto,String adminName);
	
	public List<AdminUiEndPoint> getAdminUiEndPointDto(String systemId,String ipUrlToUi,String updatedBy);

	public AdminUiEndPointDto updateAdminUiEndPoint(AdminUiEndPointDto adminUiEndPointDto, String systemId);
}

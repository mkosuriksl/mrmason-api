package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import com.application.mrmason.dto.SparePartDto;
import com.application.mrmason.dto.SparePartEntity;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;

public interface SparePartService {
	public SparePartDto addSparePart(SparePartDto dto, RegSource regSource, UserType userType);
	public SparePartDto updateSparePart(SparePartDto dto, RegSource regSource, UserType userType);
	public Page<SparePartEntity> getSparePart(String requestId,
			String sparePart, String brand, String model,String updatedBy,Pageable pageable)
			throws AccessDeniedException;
}

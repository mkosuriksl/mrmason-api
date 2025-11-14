package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponsePositionTypeDto;
import com.application.mrmason.entity.FrPositionType;

public interface FrPositionTypeService {

	public GenericResponse<FrPositionType> addPositionType(FrPositionType frPositionType);
	public ResponsePositionTypeDto getPositionType(String frUserId, List<String> positionType, int page, int size);
	public GenericResponse<FrPositionType> updatePositionType(FrPositionType dto);
}

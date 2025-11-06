package com.application.mrmason.service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrPositionType;

public interface FrPositionTypeService {

	public GenericResponse<FrPositionType> addPositionType(FrPositionType frPositionType);
}

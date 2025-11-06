package com.application.mrmason.service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrAvaiableLocation;

public interface FrAvailableLocationService {

	public GenericResponse<FrAvaiableLocation> addAvailableLocation(FrAvaiableLocation frAvaiableLocation);
}

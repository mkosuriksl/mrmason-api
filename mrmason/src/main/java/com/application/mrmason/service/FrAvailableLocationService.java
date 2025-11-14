package com.application.mrmason.service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseAvailableDto;
import com.application.mrmason.entity.FrAvaiableLocation;

public interface FrAvailableLocationService {

	public GenericResponse<FrAvaiableLocation> addAvailableLocation(FrAvaiableLocation frAvaiableLocation);
	public ResponseAvailableDto getAvailableLocations(
	        String frUserId, String city, String countrycode,
	        int page, int size);
    public GenericResponse<FrAvaiableLocation> updateLocation(FrAvaiableLocation dto);
}

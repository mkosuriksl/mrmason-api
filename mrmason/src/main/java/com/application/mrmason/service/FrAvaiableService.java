package com.application.mrmason.service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseAvailableDto;
import com.application.mrmason.entity.FrAvailable;

public interface FrAvaiableService {

	public GenericResponse<FrAvailable> addAvaiable(FrAvailable frServiceRole);
	public ResponseAvailableDto getAvailable(String frUserId, String remote, String onsite, int page,
			int size);
    public GenericResponse<FrAvailable> updateAvaiable(FrAvailable dto);
}

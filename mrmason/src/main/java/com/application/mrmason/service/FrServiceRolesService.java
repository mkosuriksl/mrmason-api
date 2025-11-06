package com.application.mrmason.service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrServiceRole;

public interface FrServiceRolesService {

	public GenericResponse<FrServiceRole> addServiceRole(FrServiceRole frServiceRole);
}

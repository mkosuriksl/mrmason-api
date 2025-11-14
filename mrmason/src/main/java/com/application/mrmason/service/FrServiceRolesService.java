package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseServiceRoleDto;
import com.application.mrmason.entity.FrServiceRole;

public interface FrServiceRolesService {

	public GenericResponse<FrServiceRole> addServiceRole(FrServiceRole frServiceRole);

	public ResponseServiceRoleDto getServiceRole(String frUserId, List<String> training, List<String> developer,
			String interviewer, int page, int size);

	public GenericResponse<FrServiceRole> updateServiceRole(FrServiceRole dto);
}

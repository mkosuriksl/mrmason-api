package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.entity.FrServiceRole;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.repository.FrServiceRoleRepository;
import com.application.mrmason.service.FrServiceRolesService;

@Service
public class FrServiceRolesServiceImpl implements FrServiceRolesService{
	@Autowired
	private FrServiceRoleRepository frServiceRoleRepository;

	@Autowired
	private FrRegRepository frRegRepo;

	@Override
	public GenericResponse<FrServiceRole> addServiceRole(FrServiceRole frServiceRole) {
		// Check if frUserId exists in fr_reg
		Optional<FrReg> frRegOptional = frRegRepo.findByFrUserId(frServiceRole.getFrUserId());
		if (frRegOptional.isEmpty()) {
			return new GenericResponse<>("User ID not found in registration records.", false, null);
		}

		FrReg frReg = frRegOptional.get();

		// Check verification
		if (!"yes".equalsIgnoreCase(frReg.getEmailVerified()) && !"yes".equalsIgnoreCase(frReg.getMobileVerified())) {
			return new GenericResponse<>("Email or Mobile must be verified before updating profile.", false, null);
		}

		// Update or Create profile
		Optional<FrServiceRole> existingProfileOpt = frServiceRoleRepository.findByFrUserId(frServiceRole.getFrUserId());
		FrServiceRole savedProfile;

		if (existingProfileOpt.isPresent()) {
			FrServiceRole existing = existingProfileOpt.get();
			existing.setDeveloper(frServiceRole.getDeveloper());
			existing.setTraining(frServiceRole.getTraining());
			existing.setInterviewer(frServiceRole.getInterviewer());
			existing.setUpdatedBy(frReg.getFrUserId());
			savedProfile = frServiceRoleRepository.save(existing);
			return new GenericResponse<>("Service Role updated successfully.", true, savedProfile);
		} else {
			savedProfile = frServiceRoleRepository.save(frServiceRole);
			return new GenericResponse<>("Service Role created successfully.", true, savedProfile);
		}
	}

}

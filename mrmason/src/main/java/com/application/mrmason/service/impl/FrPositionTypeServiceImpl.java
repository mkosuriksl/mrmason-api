package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrPositionType;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.repository.FrPositionTypeRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.service.FrPositionTypeService;

@Service
public class FrPositionTypeServiceImpl implements FrPositionTypeService{
	@Autowired
	private FrPositionTypeRepository frPositionTypeRepository;

	@Autowired
	private FrRegRepository frRegRepo;

	@Override
	public GenericResponse<FrPositionType> addPositionType(FrPositionType frPositionType) {
		// Check if frUserId exists in fr_reg
		Optional<FrReg> frRegOptional = frRegRepo.findByFrUserId(frPositionType.getFrUserId());
		if (frRegOptional.isEmpty()) {
			return new GenericResponse<>("User ID not found in registration records.", false, null);
		}

		FrReg frReg = frRegOptional.get();

		// Check verification
		if (!"yes".equalsIgnoreCase(frReg.getEmailVerified()) && !"yes".equalsIgnoreCase(frReg.getMobileVerified())) {
			return new GenericResponse<>("Email or Mobile must be verified before updating profile.", false, null);
		}

		// Update or Create profile
		Optional<FrPositionType> existingProfileOpt = frPositionTypeRepository.findByFrUserId(frPositionType.getFrUserId());
		FrPositionType savedProfile;

		if (existingProfileOpt.isPresent()) {
			FrPositionType existing = existingProfileOpt.get();
			existing.setPositionType(frPositionType.getPositionType());
			existing.setUpdatedBy(frReg.getFrUserId());
			savedProfile = frPositionTypeRepository.save(existing);
			return new GenericResponse<>("Freelance Position Type updated successfully.", true, savedProfile);
		} else {
			savedProfile = frPositionTypeRepository.save(frPositionType);
			return new GenericResponse<>("Freelance Position Type created successfully.", true, savedProfile);
		}
	}

}

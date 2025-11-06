package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrAvaiableLocation;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.repository.FrAvailableLocationRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.service.FrAvailableLocationService;

@Service
public class FrAvailableLocationServiceImpl implements FrAvailableLocationService{
	@Autowired
	private FrAvailableLocationRepository availableLocationRepository;

	@Autowired
	private FrRegRepository frRegRepo;

	@Override
	public GenericResponse<FrAvaiableLocation> addAvailableLocation(FrAvaiableLocation frAvaiableLocation)	
	{
		// Check if frUserId exists in fr_reg
		Optional<FrReg> frRegOptional = frRegRepo.findByFrUserId(frAvaiableLocation.getFrUserId());
		if (frRegOptional.isEmpty()) {
			return new GenericResponse<>("User ID not found in registration records.", false, null);
		}

		FrReg frReg = frRegOptional.get();

		// Check verification
		if (!"yes".equalsIgnoreCase(frReg.getEmailVerified()) && !"yes".equalsIgnoreCase(frReg.getMobileVerified())) {
			return new GenericResponse<>("Email or Mobile must be verified before updating profile.", false, null);
		}

		// Update or Create profile
		Optional<FrAvaiableLocation> existingProfileOpt = availableLocationRepository.findByFrUserId(frAvaiableLocation.getFrUserId());
		FrAvaiableLocation savedProfile;

		if (existingProfileOpt.isPresent()) {
			FrAvaiableLocation existing = existingProfileOpt.get();
			existing.setCity(frAvaiableLocation.getCity());
			existing.setCountrycode(frAvaiableLocation.getCountrycode());
			existing.setUpdatedBy(frReg.getFrUserId());
			savedProfile = availableLocationRepository.save(existing);
			return new GenericResponse<>("Freelance Avaiable location updated successfully.", true, savedProfile);
		} else {
			savedProfile = availableLocationRepository.save(frAvaiableLocation);
			return new GenericResponse<>("Freelance Avaiable locat=ion created successfully.", true, savedProfile);
		}
	}

}

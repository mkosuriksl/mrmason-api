package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrAvailable;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.repository.FrAvaiableRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.service.FrAvaiableService;

@Service
public class FrAvaiableServiceImpl implements FrAvaiableService{
	@Autowired
	private FrAvaiableRepository frAvaiableRepository;

	@Autowired
	private FrRegRepository frRegRepo;

	@Override
	public GenericResponse<FrAvailable> addAvaiable(FrAvailable frAvaialble)	{
		// Check if frUserId exists in fr_reg
		Optional<FrReg> frRegOptional = frRegRepo.findByFrUserId(frAvaialble.getFrUserId());
		if (frRegOptional.isEmpty()) {
			return new GenericResponse<>("User ID not found in registration records.", false, null);
		}

		FrReg frReg = frRegOptional.get();

		// Check verification
		if (!"yes".equalsIgnoreCase(frReg.getEmailVerified()) && !"yes".equalsIgnoreCase(frReg.getMobileVerified())) {
			return new GenericResponse<>("Email or Mobile must be verified before updating profile.", false, null);
		}

		// Update or Create profile
		Optional<FrAvailable> existingProfileOpt = frAvaiableRepository.findByFrUserId(frAvaialble.getFrUserId());
		FrAvailable savedProfile;

		if (existingProfileOpt.isPresent()) {
			FrAvailable existing = existingProfileOpt.get();
			existing.setOnsite(frAvaialble.getOnsite());
			existing.setRemote(frAvaialble.getRemote());
			existing.setUpdatedBy(frReg.getFrUserId());
			savedProfile = frAvaiableRepository.save(existing);
			return new GenericResponse<>("Freelance Avaiable updated successfully.", true, savedProfile);
		} else {
			savedProfile = frAvaiableRepository.save(frAvaialble);
			return new GenericResponse<>("Freelance Avaiable created successfully.", true, savedProfile);
		}
	}

}

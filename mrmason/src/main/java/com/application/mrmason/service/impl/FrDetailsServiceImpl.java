package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ResponseGetdetailsDto;
import com.application.mrmason.entity.FrAvaiableLocation;
import com.application.mrmason.entity.FrAvailable;
import com.application.mrmason.entity.FrPositionType;
import com.application.mrmason.entity.FrProfile;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.entity.FrServiceRole;
import com.application.mrmason.repository.FrAvaiableRepository;
import com.application.mrmason.repository.FrAvailableLocationRepository;
import com.application.mrmason.repository.FrPositionTypeRepository;
import com.application.mrmason.repository.FrProfileRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.repository.FrServiceRoleRepository;
import com.application.mrmason.service.FrDetailsService;

@Service
public class FrDetailsServiceImpl implements FrDetailsService {

	@Autowired
	private FrAvailableLocationRepository locationRepo;
	@Autowired
	private FrAvaiableRepository availableRepo;
	@Autowired
	private FrPositionTypeRepository positionRepo;
	@Autowired
	private FrProfileRepository profileRepo;
	@Autowired
	private FrRegRepository regRepo;
	@Autowired
	private FrServiceRoleRepository serviceRoleRepo;

	@Override
	public ResponseGetdetailsDto getFreelancerDetails(String city, String primarySkill, String frEmail,
			String secondarySkill, String positionType, String training, int page, int size) {

		ResponseGetdetailsDto response = new ResponseGetdetailsDto();

		// Step 1️⃣: collect matching userIds for each filter
		Set<String> matchingIds = null;

		// --- city filter ---
		if (city != null && !city.isEmpty()) {
			Set<String> cityIds = locationRepo.findByCityIgnoreCase(city).stream().map(FrAvaiableLocation::getFrUserId)
					.collect(Collectors.toSet());
			matchingIds = intersect(matchingIds, cityIds);
		}

		// --- primarySkill filter ---
		if (primarySkill != null && !primarySkill.isEmpty()) {
			Set<String> primaryIds = profileRepo.findByPrimarySkillLikeIgnoreCase(primarySkill).stream()
					.map(FrProfile::getFrUserId).collect(Collectors.toSet());
			matchingIds = intersect(matchingIds, primaryIds);
		}

		// --- secondarySkill filter ---
		if (secondarySkill != null && !secondarySkill.isEmpty()) {
			Set<String> secondaryIds = profileRepo.findBySecondarySkillLikeIgnoreCase(secondarySkill).stream()
					.map(FrProfile::getFrUserId).collect(Collectors.toSet());
			matchingIds = intersect(matchingIds, secondaryIds);
		}

		// --- frEmail filter ---
		if (frEmail != null && !frEmail.isEmpty()) {
			Set<String> emailIds = regRepo.findByFrEmailIgnoreCase(frEmail).stream().map(FrReg::getFrUserId)
					.collect(Collectors.toSet());
			matchingIds = intersect(matchingIds, emailIds);
		}

		// --- positionType filter ---
		if (positionType != null && !positionType.isEmpty()) {
			Set<String> posIds = positionRepo.findByPositionTypeContainingIgnoreCase(positionType).stream()
					.map(FrPositionType::getFrUserId).collect(Collectors.toSet());
			matchingIds = intersect(matchingIds, posIds);
		}

		// --- training filter ---
		if (training != null && !training.isEmpty()) {
			Set<String> trainingIds = serviceRoleRepo.findByTrainingContainingIgnoreCase(training).stream()
					.map(FrServiceRole::getFrUserId).collect(Collectors.toSet());
			matchingIds = intersect(matchingIds, trainingIds);
		}

		// If no matches found
		if (matchingIds == null || matchingIds.isEmpty()) {
			response.setMessage("No freelancers found for given filters");
			response.setStatus(false);
			return response;
		}

		// Step 2️⃣: fetch full objects for matching frUserIds
		List<FrAvaiableLocation> locations = locationRepo.findAllById(matchingIds);
		List<FrAvailable> availables = availableRepo.findAllById(matchingIds);
		List<FrPositionType> positions = positionRepo.findAllById(matchingIds);
		List<FrProfile> profiles = profileRepo.findAllById(matchingIds);
		List<FrReg> regs = regRepo.findAllById(matchingIds);
		List<FrServiceRole> serviceRoles = serviceRoleRepo.findAllById(matchingIds);

		// Step 3️⃣: set response
		response.setMessage("Fetched matching freelancers successfully");
		response.setStatus(true);
		response.setFrAvaiableLocation(locations);
		response.setFrAvaiable(availables);
		response.setFrPositionType(positions);
		response.setFrProfile(profiles);
		response.setFrReg(regs);
		response.setFrServiceRole(serviceRoles);
		response.setTotalElements(matchingIds.size());
		response.setTotalPages(1);
		response.setPageSize(size);
		response.setCurrentPage(page);

		return response;
	}

	// Utility to handle intersection
	private Set<String> intersect(Set<String> base, Set<String> next) {
		if (base == null)
			return next; // first filter
		base.retainAll(next);
		return base;
	}
}

package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseServiceRoleDto;
import com.application.mrmason.entity.FrPositionType;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.entity.FrServiceRole;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.repository.FrServiceRoleRepository;
import com.application.mrmason.service.FrServiceRolesService;

import jakarta.transaction.Transactional;

@Service
public class FrServiceRolesServiceImpl implements FrServiceRolesService {
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
		Optional<FrServiceRole> existingProfileOpt = frServiceRoleRepository
				.findByFrUserId(frServiceRole.getFrUserId());
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

	@Override
	public ResponseServiceRoleDto getServiceRole(String frUserId, List<String> training, List<String> developer,
			String interviewer, int page, int size) {

		// Fetch all records
		List<FrServiceRole> allRecords = frServiceRoleRepository.findAll();

		// Apply filters
		List<FrServiceRole> filtered = allRecords.stream()
				.filter(a -> frUserId == null || a.getFrUserId().equals(frUserId))
				.filter(a -> developer == null
						|| !developer.isEmpty() && a.getDeveloper().stream().anyMatch(developer::contains))
				.filter(a -> training == null
						|| !training.isEmpty() && a.getTraining().stream().anyMatch(training::contains))
				.filter(a -> interviewer == null || a.getInterviewer().equals(interviewer)).toList();

		// Pagination
		int totalElements = filtered.size();
		int totalPages = (int) Math.ceil((double) totalElements / size);
		int fromIndex = Math.min(page * size, totalElements);
		int toIndex = Math.min(fromIndex + size, totalElements);

		List<FrServiceRole> pagedList = filtered.subList(fromIndex, toIndex).stream().map(this::convertToDto).toList();

		// Build response
		ResponseServiceRoleDto response = new ResponseServiceRoleDto();
		response.setMessage("Position types fetched successfully");
		response.setStatus(true);
		response.setFrServiceRoles(pagedList);
		response.setCurrentPage(page);
		response.setPageSize(size);
		response.setTotalElements(totalElements);
		response.setTotalPages(totalPages);

		return response;
	}

	private FrServiceRole convertToDto(FrServiceRole entity) {
		// Return a copy of entity (avoid modifying original)
		return FrServiceRole.builder().frUserId(entity.getFrUserId()).training(entity.getTraining())
				.developer(entity.getDeveloper()).interviewer(entity.getInterviewer()).updatedBy(entity.getUpdatedBy())
				.updatedDate(entity.getUpdatedDate()).build();
	}
	
	@Override
	@Transactional
	public GenericResponse<FrServiceRole> updateServiceRole(FrServiceRole dto) {

		FrServiceRole existing = frServiceRoleRepository.findById(dto.getFrUserId()).orElse(null);

		if (existing == null) {
			return new GenericResponse<>("FrUserId not found", false, null);
		}

		// Update fields
		if (dto.getTraining() != null) {
			existing.setTraining(dto.getTraining());
		}
		if (dto.getDeveloper() != null) {
			existing.setDeveloper(dto.getDeveloper());
		}
		if (dto.getInterviewer() != null) {
			existing.setInterviewer(dto.getInterviewer());
		}

		frServiceRoleRepository.save(existing);

		// Convert entity to response DTO
		FrServiceRole responseDto = new FrServiceRole();
		responseDto.setFrUserId(existing.getFrUserId());
		responseDto.setTraining(existing.getTraining());
		responseDto.setDeveloper(existing.getDeveloper());
		responseDto.setInterviewer(existing.getInterviewer());
		responseDto.setUpdatedBy(existing.getFrUserId());
		responseDto.setUpdatedDate(LocalDateTime.now());

		return new GenericResponse<>("Avaiable updated successfully", true, responseDto);
	}
}

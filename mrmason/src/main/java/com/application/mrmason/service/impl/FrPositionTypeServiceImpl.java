package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseAvailableDto;
import com.application.mrmason.dto.ResponsePositionTypeDto;
import com.application.mrmason.entity.FrAvailable;
import com.application.mrmason.entity.FrPositionType;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.repository.FrPositionTypeRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.service.FrPositionTypeService;

import jakarta.transaction.Transactional;

@Service
public class FrPositionTypeServiceImpl implements FrPositionTypeService {
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
		Optional<FrPositionType> existingProfileOpt = frPositionTypeRepository
				.findByFrUserId(frPositionType.getFrUserId());
		FrPositionType savedProfile;

		if (existingProfileOpt.isPresent()) {
			FrPositionType existing = existingProfileOpt.get();
			existing.setPositionType(frPositionType.getPositionType());
			existing.setUpdatedBy(frReg.getFrUserId());
			existing.setUpdatedDate(frReg.getUpdatedDate());
			savedProfile = frPositionTypeRepository.save(existing);
			return new GenericResponse<>("Freelance Position Type updated successfully.", true, savedProfile);
		} else {
			savedProfile = frPositionTypeRepository.save(frPositionType);
			return new GenericResponse<>("Freelance Position Type created successfully.", true, savedProfile);
		}
	}

	@Override
	public ResponsePositionTypeDto getPositionType(String frUserId, List<String> positionType, int page, int size) {

		// Fetch all records
		List<FrPositionType> allRecords = frPositionTypeRepository.findAll();

		// Apply filters
		List<FrPositionType> filtered = allRecords.stream()
				.filter(a -> frUserId == null || a.getFrUserId().equals(frUserId))
				.filter(a -> positionType == null
						|| !positionType.isEmpty() && a.getPositionType().stream().anyMatch(positionType::contains))
				.toList();

		// Pagination
		int totalElements = filtered.size();
		int totalPages = (int) Math.ceil((double) totalElements / size);
		int fromIndex = Math.min(page * size, totalElements);
		int toIndex = Math.min(fromIndex + size, totalElements);

		List<FrPositionType> pagedList = filtered.subList(fromIndex, toIndex).stream().map(this::convertToDto).toList();

		// Build response
		ResponsePositionTypeDto response = new ResponsePositionTypeDto();
		response.setMessage("Position types fetched successfully");
		response.setStatus(true);
		response.setPositionTypes(pagedList);
		response.setCurrentPage(page);
		response.setPageSize(size);
		response.setTotalElements(totalElements);
		response.setTotalPages(totalPages);

		return response;
	}

	private FrPositionType convertToDto(FrPositionType entity) {
		// Return a copy of entity (avoid modifying original)
		return FrPositionType.builder().frUserId(entity.getFrUserId()).positionType(entity.getPositionType())
				.updatedBy(entity.getUpdatedBy()).updatedDate(entity.getUpdatedDate()).build();
	}

	@Override
	@Transactional
	public GenericResponse<FrPositionType> updatePositionType(FrPositionType dto) {

		FrPositionType existing = frPositionTypeRepository.findById(dto.getFrUserId()).orElse(null);

		if (existing == null) {
			return new GenericResponse<>("FrUserId not found", false, null);
		}

		// Update fields
		if (dto.getPositionType() != null) {
			existing.setPositionType(dto.getPositionType());
		}

		frPositionTypeRepository.save(existing);

		// Convert entity to response DTO
		FrPositionType responseDto = new FrPositionType();
		responseDto.setFrUserId(existing.getFrUserId());
		responseDto.setPositionType(existing.getPositionType());
		responseDto.setUpdatedBy(existing.getFrUserId());
		responseDto.setUpdatedDate(LocalDateTime.now());

		return new GenericResponse<>("Avaiable updated successfully", true, responseDto);
	}

}

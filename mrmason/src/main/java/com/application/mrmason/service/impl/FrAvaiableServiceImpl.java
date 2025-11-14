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
import com.application.mrmason.entity.FrAvailable;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.repository.FrAvaiableRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.service.FrAvaiableService;

import jakarta.transaction.Transactional;

@Service
public class FrAvaiableServiceImpl implements FrAvaiableService {
	@Autowired
	private FrAvaiableRepository frAvaiableRepository;

	@Autowired
	private FrRegRepository frRegRepo;

	@Override
	public GenericResponse<FrAvailable> addAvaiable(FrAvailable frAvaialble) {
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
			existing.setUpdatedDate(LocalDateTime.now());
			savedProfile = frAvaiableRepository.save(existing);
			return new GenericResponse<>("Freelance Avaiable updated successfully.", true, savedProfile);
		} else {
			savedProfile = frAvaiableRepository.save(frAvaialble);
			return new GenericResponse<>("Freelance Avaiable created successfully.", true, savedProfile);
		}
	}

	@Override
	public ResponseAvailableDto getAvailable(String frUserId, String remote, String onsite, int page, int size) {

		Pageable pageable = PageRequest.of(page, size);

		// Dynamic filters
		List<FrAvailable> list = frAvaiableRepository.findAll().stream()
				.filter(a -> frUserId == null || a.getFrUserId().equals(frUserId))
				.filter(a -> remote == null || a.getRemote().equalsIgnoreCase(remote))
				.filter(a -> onsite == null || a.getOnsite().equalsIgnoreCase(onsite)).toList();

		// Pagination manually
		int start = Math.min(page * size, list.size());
		int end = Math.min(start + size, list.size());

		List<FrAvailable> dtoList = list.subList(start, end).stream().map(this::convertToDto).toList();

		ResponseAvailableDto response = new ResponseAvailableDto();
		response.setMessage("Freelance Available fetched successfully");
		response.setStatus(true);
		response.setAvailable(dtoList);
		response.setCurrentPage(page);
		response.setPageSize(size);
		response.setTotalElements(list.size());
		response.setTotalPages((int) Math.ceil((double) list.size() / size));

		return response;
	}

	private FrAvailable convertToDto(FrAvailable entity) {
		FrAvailable dto = new FrAvailable();
		dto.setFrUserId(entity.getFrUserId());
		dto.setRemote(entity.getRemote());
		dto.setOnsite(entity.getOnsite());
		dto.setFrUserId(entity.getFrUserId());
		dto.setUpdatedBy(entity.getUpdatedBy());
		dto.setUpdatedDate(entity.getUpdatedDate());
		return dto;
	}

	@Override
	@Transactional
	public GenericResponse<FrAvailable> updateAvaiable(FrAvailable dto) {

		FrAvailable existing = frAvaiableRepository.findById(dto.getFrUserId()).orElse(null);

		if (existing == null) {
			return new GenericResponse<>("FrUserId not found", false, null);
		}

		// Update fields
		if (dto.getOnsite() != null) {
			existing.setOnsite(dto.getOnsite());
		}

		if (dto.getRemote() != null) {
			existing.setRemote(dto.getRemote());
		}

		frAvaiableRepository.save(existing);

		// Convert entity to response DTO
		FrAvailable responseDto = new FrAvailable();
		responseDto.setFrUserId(existing.getFrUserId());
		responseDto.setRemote(existing.getRemote());
		responseDto.setOnsite(existing.getOnsite());
		responseDto.setUpdatedBy(existing.getFrUserId());
		responseDto.setUpdatedDate(LocalDateTime.now());

		return new GenericResponse<>("Avaiable updated successfully", true, responseDto);
	}
}

package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseAvailableLocationDto;
import com.application.mrmason.entity.FrAvaiableLocation;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.repository.FrAvailableLocationRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.service.FrAvailableLocationService;

import jakarta.transaction.Transactional;

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
			existing.setUpdatedDate(LocalDateTime.now());
			savedProfile = availableLocationRepository.save(existing);
			return new GenericResponse<>("Freelance Avaiable location updated successfully.", true, savedProfile);
		} else {
			savedProfile = availableLocationRepository.save(frAvaiableLocation);
			return new GenericResponse<>("Freelance Avaiable locat=ion created successfully.", true, savedProfile);
		}
	}

	@Override
	public ResponseAvailableLocationDto getAvailableLocations(
	        String frUserId, String city, String countrycode,
	        int page, int size) {

	    Pageable pageable = PageRequest.of(page, size);

	    // Dynamic filters
	    List<FrAvaiableLocation> list = availableLocationRepository.findAll().stream()
	            .filter(a -> frUserId == null || a.getFrUserId().equals(frUserId))
	            .filter(a -> city == null || a.getCity().equalsIgnoreCase(city))
	            .filter(a -> countrycode == null || a.getCountrycode().equalsIgnoreCase(countrycode))
	            .toList();

	    // Pagination manually
	    int start = Math.min(page * size, list.size());
	    int end = Math.min(start + size, list.size());

	    List<FrAvaiableLocation> dtoList = list.subList(start, end)
	            .stream()
	            .map(this::convertToDto)
	            .toList();

	    ResponseAvailableLocationDto response = new ResponseAvailableLocationDto();
	    response.setMessage("Available Locations fetched successfully");
	    response.setStatus(true);
	    response.setAvaiableLocations(dtoList);
	    response.setCurrentPage(page);
	    response.setPageSize(size);
	    response.setTotalElements(list.size());
	    response.setTotalPages((int) Math.ceil((double) list.size() / size));

	    return response;
	}

	private FrAvaiableLocation convertToDto(FrAvaiableLocation entity) {
		FrAvaiableLocation dto = new FrAvaiableLocation();
	    dto.setFrUserId(entity.getFrUserId());
	    dto.setCity(entity.getCity());
	    dto.setCountrycode(entity.getCountrycode());
	    dto.setFrUserId(entity.getFrUserId());
	    dto.setUpdatedBy(entity.getUpdatedBy());
	    return dto;
	}
	
	@Override
	@Transactional
    public GenericResponse<FrAvaiableLocation> updateLocation(FrAvaiableLocation dto) {

        FrAvaiableLocation existing = availableLocationRepository.findById(dto.getFrUserId())
                .orElse(null);

        if (existing == null) {
            return new GenericResponse<>("FrUserId not found", false, null);
        }

        // Update fields
        if (dto.getCity() != null) {
            existing.setCity(dto.getCity());
        }

        if (dto.getCountrycode() != null) {
            existing.setCountrycode(dto.getCountrycode());
        }

        availableLocationRepository.save(existing);

        // Convert entity to response DTO
        FrAvaiableLocation responseDto = new FrAvaiableLocation();
        responseDto.setFrUserId(existing.getFrUserId());
        responseDto.setCity(existing.getCity());
        responseDto.setCountrycode(existing.getCountrycode());
        responseDto.setUpdatedBy(existing.getFrUserId());
        responseDto.setUpdatedDate(LocalDateTime.now());

        return new GenericResponse<>("Location updated successfully", true, responseDto);
    }
}

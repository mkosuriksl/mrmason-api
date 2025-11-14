package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.PrimarySkillResponseDto;
import com.application.mrmason.dto.ResponseSkillDto;
import com.application.mrmason.dto.SecondarySkillResponseDto;
import com.application.mrmason.dto.SkillRequestDto;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.entity.PrimarySkill;
import com.application.mrmason.entity.SecondarySkill;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.repository.PrimarySkillRepository;
import com.application.mrmason.repository.SecondarySkillRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
public class FrSkillServiceImpl {

	@Autowired
	private FrRegRepository frRegRepo;
	@Autowired
	private PrimarySkillRepository primarySkillRepo;
	@Autowired
	private SecondarySkillRepository secondarySkillRepo;

	public GenericResponse<SkillRequestDto> saveSkills(SkillRequestDto dto) {

		Optional<FrReg> frRegOptional = frRegRepo.findByFrUserId(dto.getFrUserId());
		if (frRegOptional.isEmpty()) {
			return new GenericResponse<>("User ID not found in registration records.", false, null);
		}

		FrReg frReg = frRegOptional.get();

		if (!"yes".equalsIgnoreCase(frReg.getEmailVerified()) && !"yes".equalsIgnoreCase(frReg.getMobileVerified())) {
			return new GenericResponse<>("Email or Mobile must be verified before saving skills.", false, null);
		}

		// DELETE EXISTING
		primarySkillRepo.deleteByFrUserId(dto.getFrUserId());
		secondarySkillRepo.deleteByFrUserId(dto.getFrUserId());

		// SAVE PRIMARY SKILLS
		int primaryCounter = 1001;
		List<PrimarySkill> savedPrimary = new ArrayList<>();

		if (dto.getPrimarySkills() != null && !dto.getPrimarySkills().isEmpty()) {

			for (PrimarySkill skill : dto.getPrimarySkills()) {

				PrimarySkill ps = new PrimarySkill();
				ps.setPrimarySkillId(dto.getFrUserId() + "_" + primaryCounter++);
				ps.setFrUserId(dto.getFrUserId());
				ps.setPrimaryCoursename(skill.getPrimaryCoursename());
				ps.setPrimaryYearsOfExperience(skill.getPrimaryYearsOfExperience());
				ps.setPrimaryRating(skill.getPrimaryRating());

				savedPrimary.add(primarySkillRepo.save(ps));
			}
		}

		// SAVE SECONDARY SKILLS
		int secondaryCounter = 2001;
		List<SecondarySkill> savedSecondary = new ArrayList<>();

		if (dto.getSecondarySkills() != null && !dto.getSecondarySkills().isEmpty()) {

			for (SecondarySkill skill : dto.getSecondarySkills()) {

				SecondarySkill ss = new SecondarySkill();
				ss.setSecondarySkillId(dto.getFrUserId() + "_" + secondaryCounter++);
				ss.setFrUserId(dto.getFrUserId());
				ss.setSecondaryCoursename(skill.getSecondaryCoursename());
				ss.setSecondaryYearsOfExperience(skill.getSecondaryYearsOfExperience());
				ss.setSecondaryRating(skill.getSecondaryRating());

				savedSecondary.add(secondarySkillRepo.save(ss));
			}
		}

		// ------------------------------------------
		// BUILD RESPONSE DTO FROM SAVED DATA
		// ------------------------------------------
		SkillRequestDto responseDto = new SkillRequestDto();
		responseDto.setFrUserId(dto.getFrUserId());
		responseDto.setPrimarySkills(savedPrimary);
		responseDto.setSecondarySkills(savedSecondary);

		return new GenericResponse<>("Skills saved successfully.", true, responseDto);
	}

	public ResponseSkillDto getSkills(String frUserId, String primarySkillId, String primaryCoursename,
			String primaryYearsOfExperience, String primaryRating, String secondarySkillId, String secondaryCoursename,
			String secondaryYearsOfExperience, String secondaryRating, int page, int size,
			Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("frUserId", "primarySkillId", "primaryCoursename",
				"primaryYearsOfExperience", "primaryRating", "secondarySkillId", "secondaryCoursename",
				"secondaryYearsOfExperience", "secondaryRating");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		Pageable pageable = PageRequest.of(page, size);

		// ---------------------
		// Primary Skill Filter
		// ---------------------
		Specification<PrimarySkill> primarySpec = (root, query, cb) -> {
			Predicate p = cb.conjunction();

			if (frUserId != null && !frUserId.isBlank())
				p = cb.and(p, cb.equal(root.get("frUserId"), frUserId));

			if (primarySkillId != null && !primarySkillId.isBlank())
				p = cb.and(p, cb.equal(root.get("primarySkillId"), primarySkillId));

			if (primaryCoursename != null && !primaryCoursename.isBlank())
				p = cb.and(p,
						cb.like(cb.lower(root.get("primaryCoursename")), "%" + primaryCoursename.toLowerCase() + "%"));

			if (primaryYearsOfExperience != null && !primaryYearsOfExperience.isBlank())
				p = cb.and(p, cb.equal(root.get("primaryYearsOfExperience"), primaryYearsOfExperience));

			if (primaryRating != null && !primaryRating.isBlank())
				p = cb.and(p, cb.equal(root.get("primaryRating"), primaryRating));

			return p;
		};

		// -----------------------
		// Secondary Skill Filter
		// -----------------------
		Specification<SecondarySkill> secondarySpec = (root, query, cb) -> {
			Predicate p = cb.conjunction();

			if (frUserId != null && !frUserId.isBlank())
				p = cb.and(p, cb.equal(root.get("frUserId"), frUserId));

			if (secondarySkillId != null && !secondarySkillId.isBlank())
				p = cb.and(p, cb.equal(root.get("secondarySkillId"), secondarySkillId));

			if (secondaryCoursename != null && !secondaryCoursename.isBlank())
				p = cb.and(p, cb.like(cb.lower(root.get("secondaryCoursename")),
						"%" + secondaryCoursename.toLowerCase() + "%"));

			if (secondaryYearsOfExperience != null && !secondaryYearsOfExperience.isBlank())
				p = cb.and(p, cb.equal(root.get("secondaryYearsOfExperience"), secondaryYearsOfExperience));

			if (secondaryRating != null && !secondaryRating.isBlank())
				p = cb.and(p, cb.equal(root.get("secondaryRating"), secondaryRating));

			return p;
		};

		// Query DB
		Page<PrimarySkill> primaryPage = primarySkillRepo.findAll(primarySpec, pageable);
		Page<SecondarySkill> secondaryPage = secondarySkillRepo.findAll(secondarySpec, pageable);

		// Convert to DTOs
		List<PrimarySkillResponseDto> primarySkillDtos = primaryPage.getContent().stream().map(p -> {
			PrimarySkillResponseDto dto = new PrimarySkillResponseDto();
			dto.setPrimarySkillId(p.getPrimarySkillId());
			dto.setFrUserId(p.getFrUserId());
			dto.setPrimaryCoursename(p.getPrimaryCoursename());
			dto.setPrimaryYearsOfExperience(p.getPrimaryYearsOfExperience());
			dto.setPrimaryRating(p.getPrimaryRating());
			return dto;
		}).toList();

		List<SecondarySkillResponseDto> secondarySkillDtos = secondaryPage.getContent().stream().map(s -> {
			SecondarySkillResponseDto dto = new SecondarySkillResponseDto();
			dto.setSecondarySkillId(s.getSecondarySkillId());
			dto.setFrUserId(s.getFrUserId());
			dto.setSecondaryCoursename(s.getSecondaryCoursename());
			dto.setSecondaryYearsOfExperience(s.getSecondaryYearsOfExperience());
			dto.setSecondaryRating(s.getSecondaryRating());
			return dto;
		}).toList();

		// Prepare Response
		ResponseSkillDto response = new ResponseSkillDto();
		response.setMessage("Skills fetched successfully.");
		response.setStatus(true);
		response.setPrimarySkills(primarySkillDtos);
		response.setSecondarySkills(secondarySkillDtos);

		response.setCurrentPage(page);
		response.setPageSize(size);
		response.setTotalElements(primaryPage.getTotalElements() + secondaryPage.getTotalElements());
		response.setTotalPages(Math.max(primaryPage.getTotalPages(), secondaryPage.getTotalPages()));

		return response;
	}

	@Transactional
	public GenericResponse<SkillRequestDto> updateSkills(SkillRequestDto dto) {

	    // ---- PRIMARY SKILLS UPDATE ----
	    if (dto.getPrimarySkills() != null) {
	        for (PrimarySkill p : dto.getPrimarySkills()) {

	            PrimarySkill existing = primarySkillRepo.findById(p.getPrimarySkillId())
	                    .orElse(null);

	            if (existing != null) {
	                existing.setPrimaryCoursename(p.getPrimaryCoursename());
	                existing.setPrimaryYearsOfExperience(p.getPrimaryYearsOfExperience());
	                existing.setPrimaryRating(p.getPrimaryRating());
	                existing.setFrUserId(dto.getFrUserId());   // FIX
	                primarySkillRepo.save(existing);
	            }
	        }
	    }

	    // ---- SECONDARY SKILLS UPDATE ----
	    if (dto.getSecondarySkills() != null) {
	        for (SecondarySkill s : dto.getSecondarySkills()) {

	            SecondarySkill existing = secondarySkillRepo.findById(s.getSecondarySkillId())
	                    .orElse(null);

	            if (existing != null) {
	                existing.setSecondaryCoursename(s.getSecondaryCoursename());
	                existing.setSecondaryYearsOfExperience(s.getSecondaryYearsOfExperience());
	                existing.setSecondaryRating(s.getSecondaryRating());
	                existing.setFrUserId(dto.getFrUserId());   // FIX
	                secondarySkillRepo.save(existing);
	            }
	        }
	    }

	    // FETCH UPDATED RECORDS — not request body
	    SkillRequestDto updated = new SkillRequestDto();
	    updated.setFrUserId(dto.getFrUserId());
	    updated.setPrimarySkills(primarySkillRepo.findByFrUserId(dto.getFrUserId()));
	    updated.setSecondarySkills(secondarySkillRepo.findByFrUserId(dto.getFrUserId()));

	    GenericResponse<SkillRequestDto> response = new GenericResponse<>();
	    response.setMessage("Skills updated successfully.");
	    response.setSuccess(true);
	    response.setData(updated);

	    return response;
	}


}

package com.application.mrmason.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseSkillDto;
import com.application.mrmason.dto.SkillRequestDto;
import com.application.mrmason.service.impl.FrSkillServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrSkillController {

	private final FrSkillServiceImpl frSkillService;

	@PostMapping("/save-skills")
	public ResponseEntity<GenericResponse<SkillRequestDto>> saveSkills(@RequestBody SkillRequestDto dto) {
		GenericResponse<SkillRequestDto> response = frSkillService.saveSkills(dto);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-skills")
	public ResponseEntity<ResponseSkillDto> getSkills(@RequestParam(required = false) String frUserId,
			@RequestParam(required = false) String primarySkillId,
			@RequestParam(required = false) String primaryCoursename,
			@RequestParam(required = false) String primaryYearsOfExperience,
			@RequestParam(required = false) String primaryRating,
			@RequestParam(required = false) String secondarySkillId,
			@RequestParam(required = false) String secondaryCoursename,
			@RequestParam(required = false) String secondaryYearsOfExperience,
			@RequestParam(required = false) String secondaryRating, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> requestParams) {

		ResponseSkillDto response = frSkillService.getSkills(frUserId, primarySkillId, primaryCoursename,
				primaryYearsOfExperience, primaryRating, secondarySkillId, secondaryCoursename,
				secondaryYearsOfExperience, secondaryRating, page, size, requestParams);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-skills")
	public ResponseEntity<GenericResponse<SkillRequestDto>> updateSkills(@RequestBody SkillRequestDto dto) {

		GenericResponse<SkillRequestDto> response = frSkillService.updateSkills(dto);
		return ResponseEntity.ok(response);
	}

}

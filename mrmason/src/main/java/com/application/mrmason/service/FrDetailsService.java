package com.application.mrmason.service;

import com.application.mrmason.dto.ResponseGetdetailsDto;

public interface FrDetailsService {
	ResponseGetdetailsDto getFreelancerDetails(String city, String primarySkill, String frEmail, String secondarySkill,
			String positionType, String training, int page, int size);
}

package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.CustomerEmailOtpRepo;
import com.application.mrmason.service.CustomerEmailOtpService;
import com.application.mrmason.service.CustomerLoginService;

@Service
public class CustomerEmailOtpServiceImpl implements CustomerEmailOtpService{

	@Autowired
	CustomerEmailOtpRepo emailLoginRepo;
	@Autowired
	CustomerLoginService loginService;
	
	@Override
	public String isEmailExistsAndRegSource(String email, RegSource regSource) {
	    return Optional.ofNullable(emailLoginRepo.findByEmailAndRegSource(email, regSource))
	                   .map(CustomerEmailOtp::getEmail)
	                   .orElse(null);
	}


	@Override
	public CustomerEmailOtp updateData(String otp,String email,RegSource regSource) {
		Optional<CustomerEmailOtp> existedById = Optional.of(emailLoginRepo.findByEmailAndRegSource(email,regSource));
		if(existedById.isPresent()) {
			existedById.get().setOtp(otp);
			loginService.updateDataWithEmailAndRegSource(email,regSource);
			return emailLoginRepo.save(existedById.get());
		}
		return null;
	}
	
}

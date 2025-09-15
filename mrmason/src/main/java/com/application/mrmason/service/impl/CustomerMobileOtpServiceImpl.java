package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.CustomerMobileOtp;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.CustomerMobileOtpRepo;
import com.application.mrmason.service.CustomerLoginService;
import com.application.mrmason.service.CustomerMobileOtpService;

@Service
public class CustomerMobileOtpServiceImpl implements CustomerMobileOtpService{
	@Autowired
	CustomerMobileOtpRepo mobileRepo;
	@Autowired
	CustomerLoginService loginService;
	
	@Override
	public CustomerMobileOtp updateData(String otp, String mobile,RegSource regSource) {
		Optional<CustomerMobileOtp> existedById = Optional.of(mobileRepo.findByMobileNumAndRegSource(mobile,regSource));
		if(existedById.isPresent()) {
			existedById.get().setOtp(otp);
			loginService.updateDataWithMobile(mobile,regSource);
			return mobileRepo.save(existedById.get());
		}
		return null;
	}
	public String  isMobileNumExistsAndRegSource(String mobile,RegSource regSource) {
		if(mobileRepo.findByMobileNumAndRegSource(mobile,regSource)==null) {
			return null;
		}
		return mobile;
	}


}


package com.application.mrmason.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.Logindto;
import com.application.mrmason.entity.CustomerMobileOtp;
import com.application.mrmason.repository.CustomerMobileOtpRepo;
import com.application.mrmason.service.CustomerMobileOtpService;
import com.application.mrmason.service.CustomerRegistrationService;
import com.application.mrmason.service.OtpGenerationService;

import jakarta.annotation.security.PermitAll;

@RestController
public class CustomerMobileOtpController {
	@Autowired
	OtpGenerationService otpService;
	@Autowired
	CustomerMobileOtpService mobileService;
	@Autowired
	CustomerRegistrationService regService;
	@Autowired
	CustomerMobileOtpRepo otpRepo;

	@PostMapping("/sendSmsOtp")
	public ResponseEntity<String> sendEmail(@RequestBody Logindto login) {
		String mobile = login.getMobile();
		if (otpRepo.findByMobileNum(mobile) == null) {
			return new ResponseEntity<String>("Invalid mobile number..!", HttpStatus.NOT_FOUND);
		} else {
			Optional<CustomerMobileOtp> user = Optional.of(otpRepo.findByMobileNum(mobile));

			if (user.get().getOtp() == null) {
				otpService.generateMobileOtp(mobile);
				return new ResponseEntity<String>("Otp sent to the registered mobile number.", HttpStatus.OK);
			}
			return new ResponseEntity<String>("mobile already verified.", HttpStatus.CREATED);
		}
	}

	@PostMapping("/verifySmsOtp")
	public ResponseEntity<String> verifyCustomer(@RequestBody Logindto login) {
		String mobile = login.getMobile();
		String otp = login.getOtp();

		if (otpService.verifyMobileOtp(mobile, otp)) {

			mobileService.updateData(otp, mobile);
			return new ResponseEntity<String>("Mobile number verified successfully..", HttpStatus.OK);

		}
		return new ResponseEntity<String>("Invalid Otp..!", HttpStatus.BAD_REQUEST);

	}
}

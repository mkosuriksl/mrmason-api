package com.application.mrmason.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.LoginDto;
import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.repository.CustomerEmailOtpRepo;
import com.application.mrmason.service.CustomerEmailOtpService;
import com.application.mrmason.service.CustomerRegistrationService;
import com.application.mrmason.service.OtpGenerationService;

@RestController
public class CustomerEmailOtpController {

	@Autowired
	OtpGenerationService otpService;
	@Autowired
	CustomerEmailOtpService emailLoginService;
	@Autowired
	CustomerRegistrationService regService;
	@Autowired
	CustomerEmailOtpRepo otpRepo;

	@PostMapping("/sendOtp")
	public ResponseEntity<String> sendEmail(@RequestBody LoginDto login) {
		String userEmail = login.getUserEmail();
		if (emailLoginService.isEmailExists(userEmail) == null) {
			return new ResponseEntity<String>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
		} else {
			Optional<CustomerEmailOtp> user = Optional.of(otpRepo.findByEmail(userEmail));

			if (user.get().getOtp() == null) {
				otpService.generateOtp(userEmail);
				return new ResponseEntity<String>("Otp sent to the registered EmailId.", HttpStatus.OK);
			}
			return new ResponseEntity<String>("Email already verified.", HttpStatus.CREATED);
		}
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<String> verifyCustomer(@RequestBody LoginDto login) {
		String userEmail = login.getUserEmail();
		String otp = login.getOtp();

		if (otpService.verifyOtp(userEmail, otp)) {

			emailLoginService.updateData(otp, userEmail);
			return new ResponseEntity<String>("Email verified Successfully..", HttpStatus.OK);

		}
		return new ResponseEntity<String>("Invalid Otp..!", HttpStatus.BAD_REQUEST);

	}
}

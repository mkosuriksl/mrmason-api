package com.application.mrmason.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.Logindto;
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.repository.CustomerEmailOtpRepo;
import com.application.mrmason.repository.CustomerLoginRepo;
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
	
	ResponseMessageDto response=new ResponseMessageDto();
	@Autowired
	CustomerLoginRepo customerLoginRepo;

	@PostMapping("/sendOtp")
	public ResponseEntity<ResponseMessageDto> sendEmail(@RequestBody Logindto login) {
	    String userEmail = login.getEmail();
	    ResponseMessageDto response = new ResponseMessageDto();

	    // Check if email exists
	    if (emailLoginService.isEmailExists(userEmail) == null) {
	        response.setMessage("Invalid EmailId..!");
	        response.setStatus(false);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

	    // Check if already verified from c_login table
	    CustomerLogin loginEntity = customerLoginRepo.findByUserEmail(userEmail);
	    if (loginEntity != null && "yes".equalsIgnoreCase(loginEntity.getEmailVerified())) {
	        response.setMessage("Email already verified.");
	        response.setStatus(false);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

	    // Generate OTP
	    otpService.generateEmailOtpByCustomer(userEmail);
	    response.setMessage("OTP Sent to Registered EmailId.");
	    response.setStatus(true);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

//	public ResponseEntity<ResponseMessageDto> sendEmail(@RequestBody Logindto login) {
//		String userEmail = login.getEmail();
//		if (emailLoginService.isEmailExists(userEmail) == null) {
//			response.setMessage("Invalid EmailId..!");
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		} else {
//			Optional<CustomerEmailOtp> user = Optional.of(otpRepo.findByEmail(userEmail));
//
//			if (user.get().getOtp() == null) {
//				otpService.generateEmailOtpByCustomer(userEmail);
//				response.setMessage("OTP Sent to Registered EmailId.");
//				response.setStatus(true);
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//			response.setMessage("Email already verified.");
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<ResponseMessageDto> verifyCustomer(@RequestBody Logindto login) {
		String userEmail = login.getEmail();
		String otp = login.getOtp();

		if (otpService.verifyEmailOtpWithCustomer(userEmail, otp)) {

			emailLoginService.updateData(otp, userEmail);
			response.setStatus(true);
			response.setMessage(" Email Verified successful");
			return new ResponseEntity<>(response, HttpStatus.OK);

		}
		response.setMessage("Incorrect OTP, Please enter correct Otp");
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
}

package com.application.mrmason.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.Logindto;
import com.application.mrmason.entity.CustomerMobileOtp;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.service.impl.OtpGenerationServiceImpl;
import com.application.mrmason.service.impl.ServicePersonLoginService;
import com.application.mrmason.service.impl.UserService;

@RestController

public class ServicePersonLoginController {

	@Autowired
	ServicePersonLoginService loginService;

	@Autowired
	OtpGenerationServiceImpl otpService;
	

	@Autowired
	UserService userService;

	@Autowired
	ServicePersonLoginDAO servicePersonDao;

	@PostMapping("/sp-send-email-otp")
	public ResponseEntity<String> sendEmail(@RequestBody ServicePersonLogin login) {
		String email = login.getEmail();

		try {
			if (loginService.isEmailExists(email) == null) {
				return new ResponseEntity<String>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
			} else {
				Optional<ServicePersonLogin> user = Optional.of(servicePersonDao.findByEmail(email));

				if (user.get().getEVerify().equalsIgnoreCase("no")) {
					otpService.generateOtp(email);
					return new ResponseEntity<String>("Otp sent to the registered EmailId.", HttpStatus.OK);
				}
				return new ResponseEntity<String>("Email already verified.", HttpStatus.CREATED);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/sp-verify-email-otp")
	public ResponseEntity<String> verifyUserEmail(@RequestBody ServicePersonLogin login) {
		String email = login.getEmail();
		String eOtp = login.getEOtp();

		try {
			if (otpService.verifyOtp(email, eOtp)) {

				loginService.updateEmailData(eOtp, email);
				return new ResponseEntity<>(" Email Verified successful", HttpStatus.OK);

			}
			return new ResponseEntity<String>("Incorrect OTP, Please enter correct Otp", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	@PostMapping("/sp-send-mobile-otp")
	public ResponseEntity<String> sendSms(@RequestBody Logindto login) {
		String mobile = login.getMobile();
		if (servicePersonDao.findByMobile(mobile) == null) {
			return new ResponseEntity<String>("Invalid mobile number..!", HttpStatus.NOT_FOUND);
		} else {
			Optional<ServicePersonLogin> user = Optional.of(servicePersonDao.findByMobile(mobile));

			if (user.get().getMobVerify().equalsIgnoreCase("no")) {
				otpService.generateMobileOtp(mobile);
				return new ResponseEntity<String>("Otp sent to the registered mobile number.", HttpStatus.OK);
			}
			return new ResponseEntity<String>("mobile already verified.", HttpStatus.CREATED);
		}
	}

	@PostMapping("/sp-verify-mobile-otp")
	public ResponseEntity<String> verifyMobile(@RequestBody Logindto login) {
		String mobile = login.getMobile();
		String otp = login.getOtp();

		if (otpService.verifyMobileOtp(mobile, otp)) {

			loginService.updateMobileData(otp, mobile);
			return new ResponseEntity<String>("Mobile number verified successfully..", HttpStatus.OK);

		}
		return new ResponseEntity<String>("Invalid Otp..!", HttpStatus.BAD_REQUEST);

	}

	
}
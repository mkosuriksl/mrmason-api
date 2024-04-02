package com.applicaion.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.applicaion.mrmason.dto.Logindto;
import com.applicaion.mrmason.dto.Userdto;
import com.applicaion.mrmason.entity.ServicePersonLogin;
import com.applicaion.mrmason.repository.ServicePersonLoginDAO;
import com.applicaion.mrmason.service.impl.OtpGenerationServiceImpl;
import com.applicaion.mrmason.service.impl.ServicePersonLoginService;
import com.applicaion.mrmason.service.impl.UserService;

@RestController
public class ServicePersonLoginController {

	@Autowired
	ServicePersonLoginService loginService;
	
	@Autowired
	OtpGenerationServiceImpl otpService ;
	
	@Autowired
	UserService userService;
	
	@PostMapping("/sp-send-otp")
	public ResponseEntity<String> sendEmail(@RequestBody ServicePersonLogin login) {
		String email= login.getEmail();
		if (loginService.isEmailExists(email) == null) {
			return new ResponseEntity<String>("Email Id doesn't Exists", HttpStatus.NOT_FOUND);
		} else {
			otpService.generateOtp(email);
			return new ResponseEntity<String>("Otp hasbeen sent to registered email Id", HttpStatus.OK);
		}
	}

	@PostMapping("/sp-verify-otp")
	public ResponseEntity<String> verifyUserEmail(@RequestBody ServicePersonLogin login) {
		String email=login.getEmail();
		String eOtp=login.getEOtp();
		

			if (otpService.verifyOtp(email, eOtp)) {

				loginService.updateData(eOtp, email);
				return new ResponseEntity<>(" Email Verified successful",HttpStatus.OK);

			}
			return new ResponseEntity<String>("Incorrect OTP, Please enter correct Otp",HttpStatus.BAD_REQUEST);
		
	}


	
	@PostMapping("/sp-login")
	public ResponseEntity<?> login(@RequestBody Logindto login) {
		String email = login.getEmail();
		String mobile = login.getMobile();
		String password = login.getPassword();

		
			if (loginService.loginDetails(email, mobile, password).equals("login") ) {
				return new ResponseEntity<>(loginService.getUserDto(email,mobile), HttpStatus.OK);
			} else if (loginService.loginDetails(email, mobile, password).equals("InvalidPassword")) {
				return new ResponseEntity<>("Invalid Password,Please enter correct password", HttpStatus.UNAUTHORIZED);
			} else if (loginService.loginDetails(email, mobile, password).equals("verifyEmail") ) {
				return new ResponseEntity<>("Please Verify Email Before Login.", HttpStatus.UNAUTHORIZED);
			} else if (loginService.loginDetails(email, mobile, password).equals("verifyMobile")) {
				return new ResponseEntity<>("Please Verify MobileNumber Before Login.", HttpStatus.UNAUTHORIZED);
			}else if (loginService.loginDetails(email, mobile, password).equals("inactive")) {
				return new ResponseEntity<>("Admin blocked you, Please contact admin once.", HttpStatus.UNAUTHORIZED);
			}
		
		return new ResponseEntity<>("Invalid User....!", HttpStatus.NOT_FOUND);
	}
}
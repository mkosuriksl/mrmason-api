package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ChangePasswordDto;
import com.application.mrmason.dto.Logindto;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.service.CustomerLoginService;
import com.application.mrmason.service.CustomerRegistrationService;

@RestController
public class CustomerLoginController {
	@Autowired
	CustomerLoginService loginService;
	@Autowired
	CustomerRegistrationService regService;

	@PostMapping("/loginWithPass")
	public ResponseEntity<?> login(@RequestBody Logindto login) {
		String userEmail = login.getEmail();
		String userMobile = login.getMobile();
		String userPassword = login.getPassword();

		try {
			if (loginService.loginDetails(userEmail, userMobile, userPassword) == "login") {
				ResponseLoginDto response=new ResponseLoginDto();
				response.setLoginDetails(loginService.getCustomerDetails(userEmail, userMobile));
				response.setMessage("Login Successful..!");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}else if (loginService.loginDetails(userEmail, userMobile, userPassword) == "inactive") {
				return new ResponseEntity<>("Admin blocked you.!, please try after some time.", HttpStatus.UNAUTHORIZED);
			}else if (loginService.loginDetails(userEmail, userMobile, userPassword) == "InvalidPassword") {
				return new ResponseEntity<>("Invalid Password.!", HttpStatus.UNAUTHORIZED);
			} else if (loginService.loginDetails(userEmail, userMobile, userPassword) == "verifyEmail") {
				return new ResponseEntity<>("Please Verify Email Before Login.", HttpStatus.UNAUTHORIZED);
			} else if (loginService.loginDetails(userEmail, userMobile, userPassword) == "verifyMobile") {
				return new ResponseEntity<>("Please Verify MobileNumber Before Login.", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);
	}

	@PostMapping("/forgetPassword/sendOtp")
	public ResponseEntity<String> sendOtpForPasswordChange(@RequestBody Logindto login) {
		try {
			String userMail=login.getEmail();
			if (loginService.sendMail(userMail) != null) {
				return new ResponseEntity<String>("OTP Sent to Registered EmailId.", HttpStatus.OK);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
	}

	@PostMapping("/forgetPassword/verifyOtpAndChangePassword")
	public ResponseEntity<String> verifyOtpForPasswordChange(@RequestBody ChangePasswordDto request) {
		String userMail = request.getUserMail();
		String otp = request.getOtp();
		String newPass = request.getNewPass();
		String confPass = request.getConfPass();
		try {
			if (loginService.forgetPassword(userMail, otp, newPass, confPass) == "changed") {
				return new ResponseEntity<>("Password Changed Successfully..", HttpStatus.OK);
			} else if (loginService.forgetPassword(userMail, otp, newPass, confPass) == "notMatched") {
				return new ResponseEntity<>("New Passwords Not Matched.!", HttpStatus.BAD_REQUEST);
			} else if (loginService.forgetPassword(userMail, otp, newPass, confPass) == "incorrect") {
				return new ResponseEntity<>("Invalid OTP..!", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
	}
}

package com.application.mrmason.controller;

import com.application.mrmason.dto.Logindto;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ChangePasswordDto;

import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.service.CustomerLoginService;
import com.application.mrmason.service.CustomerRegistrationService;

@RestController
public class CustomerLoginController {
	@Autowired
	CustomerLoginService loginService;
	@Autowired
	CustomerRegistrationService regService;
	ResponseMessageDto response=new ResponseMessageDto();
	@PostMapping("/forgetPassword/sendOtp")
	public ResponseEntity<ResponseMessageDto> sendOtpForPasswordChange(@RequestBody Logindto login) {
		try {
			String userMail=login.getEmail();
			if (loginService.sendMail(userMail) != null) {
				response.setMessage("OTP Sent to Registered EmailId.");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		response.setMessage("Invalid EmailId..!");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@PostMapping("/forgetPassword/verifyOtpAndChangePassword")
	public ResponseEntity<ResponseMessageDto> verifyOtpForPasswordChange(@RequestBody ChangePasswordDto request) {
		String userMail = request.getUserMail();
		String otp = request.getOtp();
		String newPass = request.getNewPass();
		String confPass = request.getConfPass();
		try {
			if (loginService.forgetPassword(userMail, otp, newPass, confPass) == "changed") {
				response.setMessage("Password Changed Successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if (loginService.forgetPassword(userMail, otp, newPass, confPass) == "notMatched") {
				response.setMessage("New Passwords Not Matched.!");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			} else if (loginService.forgetPassword(userMail, otp, newPass, confPass) == "incorrect") {
				response.setMessage("Invalid OTP..!");
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		response.setMessage("Invalid EmailId..!");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
}

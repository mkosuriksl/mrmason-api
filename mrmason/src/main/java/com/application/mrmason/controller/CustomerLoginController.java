package com.application.mrmason.controller;

import com.application.mrmason.dto.Logindto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.application.mrmason.dto.ChangePasswordDto;
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.enums.RegSource;
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
		String email=login.getEmail();
		String mobile=login.getMobile();
		try {
			if(email!=null) {
				if (loginService.sendMail(email,login.getRegSource()) != null) {
					response.setMessage("OTP Sent to Registered EmailId.");
					response.setStatus(true);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setMessage("Invalid Email ID.!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}else {
				if (loginService.sendSms(mobile,login.getRegSource()) != null) {
					response.setMessage("OTP Sent to Registered mobile number.");
					response.setStatus(true);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				response.setMessage("Invalid Mobile Number..!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		
	}

	@PostMapping("/forgetPassword/verifyOtpAndChangePassword")
	public ResponseEntity<ResponseMessageDto> verifyOtpForPasswordChange(@RequestBody ChangePasswordDto request) {
		String email = request.getEmail();
		String otp = request.getOtp();
		String newPass = request.getNewPass();
		String confPass = request.getConfPass();
		String mobile=request.getMobile();
		RegSource regSource=request.getRegSource();
		try {
			String data=loginService.forgetPassword(mobile,email, otp, newPass, confPass,regSource);
			if (data == "changed") {
				response.setMessage("Password Changed Successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if (data == "notMatched") {
				response.setMessage("New Passwords Not Matched.!");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if (data== "incorrect") {
				response.setMessage("Invalid OTP..!");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}else if (data == "incorrectEmail") {
				response.setMessage("Invalid email id..!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}else{
				response.setMessage("Invalid mobile number..!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}
}

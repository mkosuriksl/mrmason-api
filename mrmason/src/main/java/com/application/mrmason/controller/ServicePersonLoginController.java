package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.OtpSendRequest;
import com.application.mrmason.dto.OtpVerificationRequest;
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.User;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.impl.OtpGenerationServiceImpl;
import com.application.mrmason.service.impl.ServicePersonLoginService;
import com.application.mrmason.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ServicePersonLoginController {

	@Autowired
	ServicePersonLoginService loginService;

	@Autowired
	OtpGenerationServiceImpl otpService;

	@Autowired
	UserService userService;

	@Autowired
	UserDAO userDAO;

	@Autowired
	ServicePersonLoginDAO servicePersonDao;

	ResponseMessageDto response = new ResponseMessageDto();

	@PostMapping("/sp-send-email-otp")
	public ResponseEntity<ResponseMessageDto> sendEmail(@RequestBody OtpSendRequest login) {
		log.info(">> sendEmail({})", login);
		String email = login.getContactDetail();
		ServicePersonLogin user = servicePersonDao.findByEmailAndRegSource(email, login.getRegSource())
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found By : " + login.getContactDetail()));
		if (user.getEVerify().equals("yes")) {
			response.setStatus(false);
			response.setMessage("Email already verified.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			String otp = otpService.generateOtp(email);
			user.setEOtp(otp);
			servicePersonDao.save(user);
			response.setStatus(true);
			response.setMessage("Otp sent to the registered EmailId.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/sp-verify-email-otp")
	public ResponseEntity<ResponseMessageDto> verifyUserEmail(@RequestBody OtpVerificationRequest login) {
		try {
			ServicePersonLogin optVerify = servicePersonDao
					.findByEmailAndRegSource(login.getContactDetail(), login.getRegSource()).orElseThrow(
							() -> new ResourceNotFoundException("User Not Found By : " + login.getContactDetail()));
			if (optVerify.getEOtp() == null && optVerify.getEVerify().equals("yes")) {
				response.setStatus(false);
				response.setMessage("Email Already Verified.");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			if (optVerify.getEOtp() != null && !optVerify.getEOtp().equals(login.getOtp())) {
				response.setStatus(false);
				response.setMessage("Incorrect OTP, Please enter correct Otp");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				optVerify.setEOtp(null);
				optVerify.setEVerify("yes");
				response.setStatus(true);
				servicePersonDao.save(optVerify);
				response.setMessage("Email Verified successfully");
				User user = userDAO.findByEmailAndRegSource(login.getContactDetail(), login.getRegSource()).orElseThrow(
						() -> new ResourceNotFoundException("User Not Found By : " + login.getContactDetail()));
				user.setStatus("active");
				userDAO.save(user);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

	@PostMapping("/sp-send-mobile-otp")
	public ResponseEntity<ResponseMessageDto> sendSms(@RequestBody OtpSendRequest login) {
		String mobile = login.getContactDetail();
		ServicePersonLogin user = servicePersonDao.findByMobileAndRegSource(mobile, login.getRegSource())
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found By : " + login.getContactDetail()));
		if (user.getMobVerify().equals("yes")) {
			response.setStatus(false);
			response.setMessage("Mobile Number already verified.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			String smsOtp = otpService.generateMobileOtp(mobile,login.getRegSource());
			user.setMOtp(smsOtp);
			servicePersonDao.save(user);
			response.setStatus(true);
			response.setMessage("Otp sent to the registered mobile number.");
			//findByMobileAndRegSource
			User userStatus = userDAO.findByMobileAndRegSource(login.getContactDetail(), login.getRegSource()).orElseThrow(
					() -> new ResourceNotFoundException("User Not Found By : " + login.getContactDetail()));
			userStatus.setStatus("active");
			userDAO.save(userStatus);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/sp-verify-mobile-otp")
	public ResponseEntity<ResponseMessageDto> verifyMobile(@RequestBody OtpVerificationRequest login) {
		try {
			ServicePersonLogin optVerify = servicePersonDao
					.findByMobileAndRegSource(login.getContactDetail(), login.getRegSource()).orElseThrow(
							() -> new ResourceNotFoundException("User Not Found By : " + login.getContactDetail()));
			if (optVerify.getMOtp() == null && optVerify.getMobVerify().equals("yes")) {
				response.setStatus(false);
				response.setMessage("Mobile Number Already Verified.");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			if (optVerify.getMOtp() != null && !optVerify.getMOtp().equals(login.getOtp())) {
				response.setStatus(false);
				response.setMessage("Incorrect OTP, Please enter correct Otp");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				optVerify.setMOtp(null);
				optVerify.setMobVerify("yes");
				response.setStatus(true);
				servicePersonDao.save(optVerify);
				response.setMessage("Mobile Number Verified successfully");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

}
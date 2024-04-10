package com.application.mrmason.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.Logindto;
import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.CustomerEmailOtp;
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

	@PostMapping("/sp-send-otp")
	public ResponseEntity<String> sendEmail(@RequestBody ServicePersonLogin login) {
		String email = login.getEmail();

		try {
			if (loginService.isEmailExists(email) == null) {
				return new ResponseEntity<String>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
			} else {
				Optional<ServicePersonLogin> user = Optional.of(servicePersonDao.findByEmail(email));

				if (user.get().getEOtp() == null) {
					otpService.generateOtp(email);
					return new ResponseEntity<String>("Otp sent to the registered EmailId.", HttpStatus.OK);
				}
				return new ResponseEntity<String>("Email already verified.", HttpStatus.CREATED);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/sp-verify-otp")
	public ResponseEntity<String> verifyUserEmail(@RequestBody ServicePersonLogin login) {
		String email = login.getEmail();
		String eOtp = login.getEOtp();

		try {
			if (otpService.verifyOtp(email, eOtp)) {

				loginService.updateData(eOtp, email);
				return new ResponseEntity<>(" Email Verified successful", HttpStatus.OK);

			}
			return new ResponseEntity<String>("Incorrect OTP, Please enter correct Otp", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/sp-login")
	public ResponseEntity<?> login(@RequestBody Logindto login) {
		String email = login.getEmail();
		String mobile = login.getMobile();
		String password = login.getPassword();

		try {
			if (loginService.loginDetails(email, mobile, password).equals("login")) {
				return new ResponseEntity<>(loginService.getUserDto(email, mobile), HttpStatus.OK);
			} else if (loginService.loginDetails(email, mobile, password).equals("InvalidPassword")) {
				return new ResponseEntity<>("Invalid Password,Please enter correct password", HttpStatus.UNAUTHORIZED);
			} else if (loginService.loginDetails(email, mobile, password).equals("verifyEmail")) {
				return new ResponseEntity<>("Please Verify Email Before Login.", HttpStatus.UNAUTHORIZED);
			} else if (loginService.loginDetails(email, mobile, password).equals("verifyMobile")) {
				return new ResponseEntity<>("Please Verify MobileNumber Before Login.", HttpStatus.UNAUTHORIZED);
			} else if (loginService.loginDetails(email, mobile, password).equals("inactive")) {
				return new ResponseEntity<>("Admin blocked you, Please contact admin once.", HttpStatus.UNAUTHORIZED);
			}

			return new ResponseEntity<>("Invalid User....!", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
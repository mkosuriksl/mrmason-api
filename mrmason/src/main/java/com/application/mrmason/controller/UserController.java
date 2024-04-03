package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ChangeForfotdto;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.SPAvailabilityRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.impl.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserDAO userDAO;

	@Autowired
	SPAvailabilityRepo availabilityReo;

	@PostMapping("/sp-register")
	public ResponseEntity<String> create(@RequestBody User registrationDetails) {

		if (userService.isEmailExists(registrationDetails.getEmail())) {
			return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
		}
		if (userService.isMobileExists(registrationDetails.getMobile())) {
			return new ResponseEntity<>("PhoneNumber already exists", HttpStatus.BAD_REQUEST);
		}

		userService.addDetails(registrationDetails);
		return new ResponseEntity<>("User added successfully", HttpStatus.OK);
	}

	@PutMapping("/sp-update-profile")
	public ResponseEntity<?> updateServiceProfile(@RequestBody User registrationDetails) {

		String email=registrationDetails.getEmail();
		User updatedUser = userService.updateProfile(registrationDetails, email);
		if (updatedUser == null) {
			return new ResponseEntity<>("invalid Email", HttpStatus.NOT_FOUND);
		} else {
			String successMessage = "Profile updated successfully";
			return ResponseEntity.ok().body(successMessage);
		}
	}

	@PostMapping("/change-password")
	public ResponseEntity<String> changeCustomerPassword(@RequestBody ChangeForfotdto cfPwd) {

		String email = cfPwd.getEmail();
		String oldPassword = cfPwd.getOldPassword();
		String newPassword = cfPwd.getNewPassword();
		String confirmPassword = cfPwd.getConfirmPassword();

		try {
			if (userService.changePassword(email, oldPassword, newPassword, confirmPassword) == "changed") {
				return new ResponseEntity<>("Password Changed Successfully..", HttpStatus.OK);
			} else if (userService.changePassword(email, oldPassword, newPassword, confirmPassword) == "notMatched") {
				return new ResponseEntity<>("New Passwords Not Matched.!", HttpStatus.BAD_REQUEST);
			} else if (userService.changePassword(email, oldPassword, newPassword, confirmPassword) == "incorrect") {
				return new ResponseEntity<>("Old Password is Incorrect", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);
	}

	@PostMapping("/forget-pwd-send-otp")
	public ResponseEntity<String> sendOtpForPasswordChange(@RequestBody ChangeForfotdto cfPwd) {
		String email = cfPwd.getEmail();
		try {
			if (userService.sendMail(email) != null) {
				return new ResponseEntity<String>("OTP Sent to Registered EmailId.", HttpStatus.OK);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
	}

	@PostMapping("/forget-pwd-change")
	public ResponseEntity<String> verifyOtpForPasswordChange(@RequestBody ChangeForfotdto cfPwd) {

		String email = cfPwd.getEmail();
		String otp = cfPwd.getOtp();
		String newPassword = cfPwd.getNewPassword();
		String confirmPassword = cfPwd.getConfirmPassword();

		try {
			if (userService.forgetPassword(email, otp, newPassword, confirmPassword) == "changed") {
				return new ResponseEntity<>("Password Changed Successfully..", HttpStatus.OK);
			} else if (userService.forgetPassword(email, otp, newPassword, confirmPassword) == "notMatched") {
				return new ResponseEntity<>("New Passwords Not Matched.!", HttpStatus.BAD_REQUEST);
			} else if (userService.forgetPassword(email, otp, newPassword, confirmPassword) == "incorrect") {
				return new ResponseEntity<>("Invalid OTP..!", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
	}

	@GetMapping("/sp-get-profile")
	public ResponseEntity<?> getProfile(@RequestBody ChangeForfotdto cfPwd) {
		String email = cfPwd.getEmail();
		if (userService.getServiceProfile(email) == null) {
			return new ResponseEntity<>("Invalid Email ....!", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(userService.getServiceProfile(email), HttpStatus.OK);

	}

}

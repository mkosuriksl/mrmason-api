package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ChangeForfotdto;
import com.application.mrmason.dto.FilterCustomerAndUser;
import com.application.mrmason.dto.Logindto;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.dto.ResponseSpLoginDto;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.SPAvailabilityRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.impl.ServicePersonLoginService;
import com.application.mrmason.service.impl.UserService;

@RestController

public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserDAO userDAO;

	@Autowired
	ServicePersonLoginService loginService;

	@Autowired
	SPAvailabilityRepo availabilityReo;
	
	ResponseMessageDto response2=new ResponseMessageDto();

	@PostMapping("/sp-register")
	public ResponseEntity<String> create(@RequestBody User registrationDetails) {
		try {
			if (userService.isEmailExists(registrationDetails.getEmail())) {
				return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
			}
			if (userService.isMobileExists(registrationDetails.getMobile())) {
				return new ResponseEntity<>("PhoneNumber already exists", HttpStatus.BAD_REQUEST);
			}

			userService.addDetails(registrationDetails);
			return new ResponseEntity<>("User added successfully", HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('Developer')")
	@PutMapping("/sp-update-profile")
	public ResponseEntity<?> updateServiceProfile(@RequestBody User registrationDetails) {

		String email = registrationDetails.getEmail();
		User updatedUser = userService.updateProfile(registrationDetails, email);

		try {
			if (updatedUser == null) {
				return new ResponseEntity<>("invalid Email", HttpStatus.NOT_FOUND);
			} else {
				String successMessage = "Profile updated successfully";
				return ResponseEntity.ok().body(successMessage);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('Developer')")
	@PostMapping("/change-password")
	public ResponseEntity<ResponseMessageDto> changeCustomerPassword(@RequestBody ChangeForfotdto cfPwd) {

		String email = cfPwd.getEmail();
		String oldPassword = cfPwd.getOldPassword();
		String newPassword = cfPwd.getNewPassword();
		String confirmPassword = cfPwd.getConfirmPassword();

		try {
			if (userService.changePassword(email, oldPassword, newPassword, confirmPassword) == "changed") {
				response2.setMessage("Password Changed Successfully..");
				response2.setStatus(true);
				return new ResponseEntity<>(response2, HttpStatus.OK);
				
			} else if (userService.changePassword(email, oldPassword, newPassword, confirmPassword) == "notMatched") {
				response2.setMessage("New Passwords Not Matched.!");
				return new ResponseEntity<>(response2, HttpStatus.BAD_REQUEST);
			} else if (userService.changePassword(email, oldPassword, newPassword, confirmPassword) == "incorrect") {
				response2.setMessage("Old Password is Incorrect");
				return new ResponseEntity<>(response2, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response2);
		}
		response2.setMessage("Invalid User.!");
		return new ResponseEntity<>(response2, HttpStatus.NOT_FOUND);
	}

	@PostMapping("/forget-pwd-send-otp")
	public ResponseEntity<ResponseMessageDto> sendOtpForPasswordChange(@RequestBody ChangeForfotdto cfPwd) {
		String email = cfPwd.getEmail();
		try {
			if (userService.sendMail(email) != null) {
				response2.setMessage("OTP Sent to Registered EmailId.");
				response2.setStatus(true);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			}
		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response2);
		}
		response2.setMessage("Invalid EmailId..!");
		return new ResponseEntity<>(response2, HttpStatus.NOT_FOUND);
	}

	@PostMapping("/forget-pwd-change")
	public ResponseEntity<ResponseMessageDto> verifyOtpForPasswordChange(@RequestBody ChangeForfotdto cfPwd) {

		String email = cfPwd.getEmail();
		String otp = cfPwd.getOtp();
		String newPassword = cfPwd.getNewPassword();
		String confirmPassword = cfPwd.getConfirmPassword();

		try {
			if (userService.forgetPassword(email, otp, newPassword, confirmPassword) == "changed") {
				response2.setMessage("Password Changed Successfully..");
				response2.setStatus(true);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			} else if (userService.forgetPassword(email, otp, newPassword, confirmPassword) == "notMatched") {
				response2.setMessage("New Passwords Not Matched.!");
				return new ResponseEntity<>(response2, HttpStatus.BAD_REQUEST);
			} else if (userService.forgetPassword(email, otp, newPassword, confirmPassword) == "incorrect") {
				response2.setMessage("Invalid OTP..!");
				return new ResponseEntity<>(response2, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response2);
		}
		response2.setMessage("Invalid EmailId..!");
		return new ResponseEntity<>(response2, HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasAuthority('Developer')")
	@GetMapping("/sp-get-profile")
	public ResponseEntity<?> getProfile(@RequestBody ChangeForfotdto cfPwd) {
		String email = cfPwd.getEmail();

		try {
			if (userService.getServiceProfile(email) == null) {
				return new ResponseEntity<>("Invalid Email ....!", HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<>(userService.getServiceProfile(email), HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}

	@PreAuthorize("hasAuthority('Adm')")
	@GetMapping("/filterServicePerson")
	public ResponseEntity<?> getCustomers(@RequestBody FilterCustomerAndUser user) {

		String userEmail = user.getUserEmail();
		String userMobile = user.getUserMobile();
		String userState = user.getUserState();
		String fromDate = user.getFromDate();
		String toDate = user.getToDate();
		String status = user.getStatus();
		String category = user.getServiceCategory();
		try {
			List<User> entity = userService.getServicePersonData(userEmail, userMobile, userState, status, category,
					fromDate, toDate);
			if (!entity.isEmpty()) {
				return ResponseEntity.ok(entity);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Error: Failed to fetch users. Please try again later.");
			}
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
			ResponseSpLoginDto response = userService.loginDetails(email, mobile, password);
			if (response.getJwtToken() != null) {
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/getData")
	public ResponseEntity<User> get(@RequestParam(name = "email") String email) {

		return new ResponseEntity<>(userService.getServiceDataProfile(email), HttpStatus.OK);

	}

	@GetMapping("/error")
	@PostMapping("/error")
	@PutMapping("/error")
	@DeleteMapping("/error")
	public ResponseEntity<ResponseLoginDto> error() {
		ResponseLoginDto response = new ResponseLoginDto();
		response.setMessage("Access Denied");
		return new ResponseEntity<ResponseLoginDto>(response, HttpStatus.UNAUTHORIZED);

	}

}

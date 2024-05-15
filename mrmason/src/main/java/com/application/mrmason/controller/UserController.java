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
import com.application.mrmason.dto.ResponseSpLoginDto;
import com.application.mrmason.dto.ResponseUserDTO;
import com.application.mrmason.dto.ResponseUserUpdateDto;
import com.application.mrmason.dto.Userdto;
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
	
	ResponseUserDTO response =  new ResponseUserDTO();
	ResponseUserUpdateDto userResponse = new ResponseUserUpdateDto();
	@PostMapping("/sp-register")
	public ResponseEntity<?> create(@RequestBody User registrationDetails) {
		try {
			if (userService.isEmailExists(registrationDetails.getEmail())) {
				response.setMessage("Email already exists");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			if (userService.isMobileExists(registrationDetails.getMobile())) {
				response.setMessage("Mobile already exists");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Userdto userDetails=userService.addDetails(registrationDetails);
			response.setMessage("User added successfully");
			response.setStatus(true);
			response.setUserData(userDetails);
			return new ResponseEntity<>( response,HttpStatus.OK);
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
				response.setMessage("invalid Email");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			} else {
				response.setMessage("Profile updated successfully");
				
				response.setStatus(true);
				return ResponseEntity.ok().body(response);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('Developer')")
	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangeForfotdto cfPwd) {

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
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasAuthority('Developer')")
	@GetMapping("/sp-get-profile")
	public ResponseEntity<?> getProfile(@RequestBody ChangeForfotdto cfPwd) {
		String email = cfPwd.getEmail();
		Userdto profile=userService.getServiceProfile(email);
		try {
			if ( profile == null) {
				response.setMessage("Invalid Email ....!");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<>(profile, HttpStatus.OK);
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

package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ChangePasswordDto;
import com.application.mrmason.dto.FilterCustomerAndUser;
import com.application.mrmason.dto.Logindto;
import com.application.mrmason.dto.ResponseCustomerRegDto;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.dto.ResponseUpdateDto;
import com.application.mrmason.dto.UpdateProfileDto;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.service.CustomerRegistrationService;

import jakarta.annotation.security.PermitAll;

@RestController
@PermitAll
public class CustomerRegistrationController {

	@Autowired
	public CustomerRegistrationService service;

	@PostMapping("/addNewUser")
	public ResponseEntity<?> newCustomer(@RequestBody CustomerRegistration customer) {
		if (!service.isUserUnique(customer)) {
			return new ResponseEntity<>("Email or Phone Number already exists.", HttpStatus.BAD_REQUEST);
		}
		ResponseCustomerRegDto response = new ResponseCustomerRegDto();
		response.setRegister(service.saveData(customer));
		response.setMessage("Customer added Successfully..");

		return ResponseEntity.ok(response);
	}

	@GetMapping("/filterCustomers")
	public ResponseEntity<?> getCustomers(@RequestBody FilterCustomerAndUser customer) {

		String userEmail = customer.getUserEmail();
		String userMobile = customer.getUserMobile();
		String userState = customer.getUserState();
		String fromDate = customer.getFromDate();
		String toDate = customer.getToDate();
		try {
			List<CustomerRegistration> entity = service.getCustomerData(userEmail, userMobile, userState, fromDate,
					toDate);
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

	@GetMapping("/getProfile")
	public ResponseEntity<?> getProfile(Authentication authentication) {
		CustomerRegistration userPrincipal = (CustomerRegistration) authentication.getPrincipal();
		return new ResponseEntity<>(service.getProfileData(userPrincipal.getUserid()), HttpStatus.OK);
	}

	@PutMapping("/updateProfile")
	public ResponseEntity<?> updateCustomer(@RequestBody UpdateProfileDto request) {
		String userName = request.getUserName();
		String userTown = request.getUserTown();
		String userState = request.getUserState();
		String userDistrict = request.getUserDistrict();
		String userPinCode = request.getUserPincode();
		String userid = request.getUserid();

		ResponseUpdateDto response = new ResponseUpdateDto();
		try {
			if (service.updateCustomerData(userName, userTown, userState, userDistrict, userPinCode, userid) != null) {
				response.setUpdateProfile(service.getProfileData(userid));
				response.setMessage("Successfully Updated.");
				return new ResponseEntity<>(response, HttpStatus.OK);

			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Profile Not Found.", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Profile Not Found.", HttpStatus.NOT_FOUND);
	}

	@PostMapping("/changePassword")
	public ResponseEntity<String> changeCustomerPassword(@RequestBody ChangePasswordDto request) {
		String userMail = request.getUserMail();
		String oldPass = request.getOldPass();
		String newPass = request.getNewPass();
		String confPass = request.getConfPass();
		String userMobile = request.getUserMobile();

		try {
			if (service.changePassword(userMail, oldPass, newPass, confPass, userMobile) == "changed") {
				return new ResponseEntity<>("Password Changed Successfully..", HttpStatus.OK);
			} else if (service.changePassword(userMail, oldPass, newPass, confPass, userMobile) == "notMatched") {
				return new ResponseEntity<>("New Passwords Not Matched.!", HttpStatus.BAD_REQUEST);
			} else if (service.changePassword(userMail, oldPass, newPass, confPass, userMobile) == "incorrect") {
				return new ResponseEntity<>("Old Password is Incorrect", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);

	}

	@PostMapping("/login")
	public ResponseEntity<ResponseLoginDto> login(@RequestBody Logindto requestDto) {
		String userEmail = requestDto.getEmail();
		String phno = requestDto.getMobile();
		String userPassword = requestDto.getPassword();

		ResponseLoginDto response = service.loginDetails(userEmail, phno, userPassword);
		if (response.getJwtToken() == null) {
			response.setMessage("Invalid username and password");
			return new ResponseEntity<ResponseLoginDto>(response, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<ResponseLoginDto>(response, HttpStatus.OK);

	}
}

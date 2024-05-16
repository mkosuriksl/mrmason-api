package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.dto.ResponseUpdateDto;
import com.application.mrmason.dto.ResponseUserDTO;
import com.application.mrmason.dto.ResponseUserUpdateDto;
import com.application.mrmason.dto.UpdateProfileDto;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.service.CustomerRegistrationService;

@RestController
public class CustomerRegistrationController {

	@Autowired
	public CustomerRegistrationService service;
	ResponseMessageDto response2 = new ResponseMessageDto();

	@PostMapping("/addNewUser")
	public ResponseEntity<?> newCustomer(@RequestBody CustomerRegistration customer) {
		if (!service.isUserUnique(customer)) {
			return new ResponseEntity<>("Email or Phone Number already exists.", HttpStatus.BAD_REQUEST);
		}
		ResponseCustomerRegDto response = new ResponseCustomerRegDto();
		response.setRegister(service.saveData(customer));
		response.setMessage("Customer added Successfully..");
		response.setStatus(true);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('Adm')")
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
		try {
			CustomerRegistration userPrincipal = (CustomerRegistration) authentication.getPrincipal();
			return new ResponseEntity<>(service.getProfileData(userPrincipal.getUserid()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
		}
	}

	@PreAuthorize("hasAuthority('EC')")
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
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);

			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Profile Not Found.", HttpStatus.NOT_FOUND);
		}
		response.setMessage("Profile Not Found.");
		response.setStatus(false);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasAuthority('EC')")
	@PostMapping("/changePassword")
	public ResponseEntity<?> changeCustomerPassword(@RequestBody ChangePasswordDto request) {
		String userMail = request.getUserMail();
		String oldPass = request.getOldPass();
		String newPass = request.getNewPass();
		String confPass = request.getConfPass();
		String userMobile = request.getUserMobile();

		try {
			if (service.changePassword(userMail, oldPass, newPass, confPass, userMobile) == "changed") {
				response2.setMessage("Password Changed Successfully..");
				response2.setStatus(true);
				return new ResponseEntity<>(response2, HttpStatus.OK);

			} else if (service.changePassword(userMail, oldPass, newPass, confPass, userMobile) == "notMatched") {
				response2.setMessage("New Passwords Not Matched.!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.BAD_REQUEST);
			} else if (service.changePassword(userMail, oldPass, newPass, confPass, userMobile) == "incorrect") {
				response2.setMessage("Old Password is Incorrect");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response2);
		}
		response2.setMessage("Invalid User.!");
		response2.setStatus(false);
		return new ResponseEntity<>(response2, HttpStatus.NOT_FOUND);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseLoginDto> login(@RequestBody Logindto requestDto) {
		String userEmail = requestDto.getEmail();
		String phno = requestDto.getMobile();
		String userPassword = requestDto.getPassword();

		ResponseLoginDto response = service.loginDetails(userEmail, phno, userPassword);
		if (response.getJwtToken() != null) {
			return new ResponseEntity<ResponseLoginDto>(response, HttpStatus.OK);
		}
		return new ResponseEntity<ResponseLoginDto>(response, HttpStatus.UNAUTHORIZED);

	}
}

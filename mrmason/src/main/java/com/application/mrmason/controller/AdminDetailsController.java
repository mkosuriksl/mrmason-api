package com.application.mrmason.controller;



import com.application.mrmason.dto.Logindto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.AdminDetailsDto;
import com.application.mrmason.dto.ChangePasswordDto;
import com.application.mrmason.dto.ResponceAdminDetailsDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.service.AdminDetailsService;

import jakarta.annotation.security.PermitAll;

@RestController
public class AdminDetailsController {
	@Autowired
	public AdminDetailsService adminService;

	@PostMapping("/addAdminDetails")
	public ResponseEntity<?> saveAdminDetails(@RequestBody AdminDetails admin) {
		ResponceAdminDetailsDto response = new ResponceAdminDetailsDto();
		String userEmail = admin.getEmail();
		String userMobile = admin.getMobile();
		try {
			if (adminService.registerDetails(admin) != null) {

				response.setData(adminService.getDetails(userEmail, userMobile));
				response.setMessage("Admin details added successfully..");
				return ResponseEntity.ok(response);
			}
			return new ResponseEntity<>("Email or Mobile already exists.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}
	@PreAuthorize("hasAuthority('Adm')")
	@PutMapping("/updateAdminDetails")
	public ResponseEntity<?> updateCustomer(@RequestBody AdminDetails admin) {
		ResponceAdminDetailsDto response = new ResponceAdminDetailsDto();
		String userEmail = admin.getEmail();
		String userMobile = admin.getMobile();
		try {
			if (adminService.updateAdminData(admin) != null) {
				response.setData(adminService.getDetails(userEmail, userMobile));
				response.setMessage("Successfully Updated.");
				return ResponseEntity.ok(response);

			}
			return new ResponseEntity<>("Admin Not Found.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

	}
	@PreAuthorize("hasAuthority('Adm')")
	@GetMapping("/getAdminDetails")
	public ResponseEntity<?> getAdminDetails(@RequestBody AdminDetailsDto admin) {
		try {
			AdminDetailsDto entity = adminService.getAdminDetails(admin);
			if (entity == null) {
				return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<>(entity, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}
	
	@PostMapping("/adminLoginWithPass")
	public ResponseEntity<?> login(@RequestBody Logindto login) {
		String userEmail = login.getEmail();
		String userMobile = login.getMobile();
		String userPassword = login.getPassword();
		ResponceAdminDetailsDto response=adminService.adminLoginDetails(userEmail, userMobile, userPassword);
		
		try {
			if (response.getJwtToken()!=null) {
				
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid Admin.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	
	}
	@PreAuthorize("hasAuthority('Adm')")
	@PostMapping("/changeAdminPassword")
	public ResponseEntity<String> changeCustomerPassword(@RequestBody ChangePasswordDto request) {
		String userMail = request.getUserMail();
		String oldPass = request.getOldPass();
		String newPass = request.getNewPass();
		String confPass = request.getConfPass();
		String userMobile=request.getUserMobile();
		
		try {
			if (adminService.changePassword(userMail, oldPass, newPass, confPass,userMobile) == "changed") {
				return new ResponseEntity<>("Password Changed Successfully..", HttpStatus.OK);
			} else if (adminService.changePassword(userMail, oldPass, newPass, confPass,userMobile) == "notMatched") {
				return new ResponseEntity<>("New Passwords Not Matched.!", HttpStatus.BAD_REQUEST);
			} else if (adminService.changePassword(userMail, oldPass, newPass, confPass,userMobile) == "incorrect") {
				return new ResponseEntity<>("Old Password is Incorrect", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid User.!", HttpStatus.NOT_FOUND);

	}
	@PreAuthorize("hasAuthority('Adm')")
	@PostMapping("/admin/forgetPassword/sendOtp")
	public ResponseEntity<String> sendOtpForPasswordChange(@RequestBody Logindto login) {
		try {
			String userMail=login.getEmail();
			if (adminService.sendMail(userMail) != null) {
				return new ResponseEntity<String>("OTP Sent to Registered EmailId.", HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			
			return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
		}
		
	}
	@PreAuthorize("hasAuthority('Adm')")
	@PostMapping("/admin/forgetPassword/verifyOtpAndChangePassword")
	public ResponseEntity<String> verifyOtpForPasswordChange(@RequestBody ChangePasswordDto request) {
		String userMail = request.getUserMail();
		String otp = request.getOtp();
		String newPass = request.getNewPass();
		String confPass = request.getConfPass();
		String mobile = request.getUserMobile();
		try {
			if (adminService.forgetPassword(userMail, confPass, otp, newPass, confPass) == "changed") {
				return new ResponseEntity<>("Password Changed Successfully..", HttpStatus.OK);
			} else if (adminService.forgetPassword(userMail, mobile, otp, newPass, confPass) == "notMatched") {
				return new ResponseEntity<>("New Passwords Not Matched.!", HttpStatus.BAD_REQUEST);
			} else if (adminService.forgetPassword(userMail,mobile, otp, newPass, confPass) == "incorrect") {
				return new ResponseEntity<>("Invalid OTP..!", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("Invalid EmailId..!", HttpStatus.NOT_FOUND);
	}
	
}

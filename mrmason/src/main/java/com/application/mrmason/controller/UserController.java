package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.dto.ChangeForfotdto;
import com.application.mrmason.dto.DeleteAccountRequest;
import com.application.mrmason.dto.LoginRequest;
import com.application.mrmason.dto.Logindto;
import com.application.mrmason.dto.ResponseListUserDto;
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.dto.ResponseSpLoginDto;
import com.application.mrmason.dto.ResponseUserDTO;
import com.application.mrmason.dto.ResponseUserUpdateDto;
import com.application.mrmason.dto.UpdateProfileRequest;
import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.User;
import com.application.mrmason.enums.RegSource;
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

	ResponseUserDTO response = new ResponseUserDTO();

	ResponseUserUpdateDto userResponse = new ResponseUserUpdateDto();

	ResponseMessageDto response2 = new ResponseMessageDto();

	@PostMapping("/sp-register")
	public ResponseEntity<?> create(@RequestBody User request) {
		ResponseUserDTO rs = new ResponseUserDTO();
		Optional<User> user = userService.checkExistingUser(request.getEmail(), request.getMobile(),
				request.getRegSource());
		if (user.isPresent()) {
			if (user.get().getEmail().equals(request.getEmail())
					&& user.get().getRegSource().equals(request.getRegSource())) {
				rs.setMessage("Email already exists");
				return new ResponseEntity<>(rs, HttpStatus.OK);
			}
			if (user.get().getMobile().equals(request.getMobile())
					&& user.get().getRegSource().equals(request.getRegSource())) {
				rs.setMessage("Mobile already exists");
				return new ResponseEntity<>(rs, HttpStatus.OK);
			}
		}

		Userdto userDetails = userService.addDetails(request);
		response.setMessage("Thanks for registering with us. please verify your registered email and mobile");
		response.setStatus(true);
		response.setUserData(userDetails);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/sp-update-profile")
	public ResponseEntity<?> updateServiceProfile(@RequestBody UpdateProfileRequest registrationDetails) {

		String bodSeqNo = registrationDetails.getBodSeqNo();
		User updatedUser = userService.updateProfile(registrationDetails, bodSeqNo);

		try {
			if (updatedUser == null) {
				response.setMessage("invalid Email");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				userResponse.setMessage("Profile updated successfully");
				userResponse.setStatus(true);
				userResponse.setUserData(updatedUser);
				return ResponseEntity.ok().body(userResponse);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}

	 @PostMapping("upload_photo")
		public ResponseEntity<?> uploadCabDocs(@RequestParam("bodSeqNo") String bodSeqNo,
				@RequestParam(value = "photo", required = false) MultipartFile photo, @RequestParam RegSource regSource) throws AccessDeniedException{
			return userService.uploadprofileimage(bodSeqNo, photo,regSource);
		}

	@PostMapping("/change-password")
	public ResponseEntity<ResponseMessageDto> changeCustomerPassword(@RequestBody ChangeForfotdto cfPwd) {

		String email = cfPwd.getEmail();
		String oldPassword = cfPwd.getOldPassword();
		String newPassword = cfPwd.getNewPassword();
		String confirmPassword = cfPwd.getConfirmPassword();

		try {
			if (userService.changePassword(email, oldPassword, newPassword, confirmPassword,
					cfPwd.getRegSource()) == "changed") {
				response2.setMessage("Password Changed Successfully..");
				response2.setStatus(true);
				return new ResponseEntity<>(response2, HttpStatus.OK);

			} else if (userService.changePassword(email, oldPassword, newPassword, confirmPassword,
					cfPwd.getRegSource()) == "notMatched") {
				response2.setMessage("New Passwords Not Matched.!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			} else if (userService.changePassword(email, oldPassword, newPassword, confirmPassword,
					cfPwd.getRegSource()) == "incorrect") {
				response2.setMessage("Old Password is Incorrect");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			}
		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			response2.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(response2);
		}
		response2.setMessage("Invalid User.!");
		response2.setStatus(false);
		return new ResponseEntity<>(response2, HttpStatus.OK);
	}

	@PostMapping("/forget-pwd-send-otp")
	public ResponseEntity<ResponseMessageDto> sendOtpForPasswordChange(@RequestBody Logindto login) {
		String userMail = login.getEmail();
		String mobile = login.getMobile();
		try {
			if (userMail != null) {
				if (userService.sendMail(userMail, login.getRegSource()) != null) {
					response2.setMessage("OTP Sent to Registered EmailId.");
					response2.setStatus(true);
					return new ResponseEntity<>(response2, HttpStatus.OK);
				}
				response2.setMessage("EmailId is either Invalid/Wrong Email.Please Verify Email Account!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.OK);

			} else {
				if (userService.sendSms(mobile, login.getRegSource()) != null) {
					response2.setMessage("OTP Sent to Registered Mobile Number.");
					response2.setStatus(true);
					return new ResponseEntity<>(response2, HttpStatus.OK);
				}
				response2.setMessage("Mobile number is either Invalid/Wrong Mobile number.Please Verify Mobile!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			}

		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			response2.setStatus(false);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response2);
		}

	}

	@PostMapping("/forget-pwd-change")
	public ResponseEntity<ResponseMessageDto> verifyOtpForPasswordChange(@RequestBody ChangeForfotdto cfPwd) {

		String email = cfPwd.getEmail();
		String otp = cfPwd.getOtp();
		String newPassword = cfPwd.getNewPassword();
		String mobile = cfPwd.getMobile();
		String confirmPassword = cfPwd.getConfirmPassword();

		try {
			String data = userService.forgetPassword(mobile, email, otp, newPassword, confirmPassword,
					cfPwd.getRegSource());
			if (data == "changed") {
				response2.setMessage("Password Changed Successfully..");
				response2.setStatus(true);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			} else if (data == "notMatched") {
				response2.setMessage("New Passwords Not Matched.!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			} else if (data == "incorrect") {
				response2.setMessage("Invalid OTP..!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			} else if (data == "incorrectEmail") {
				response2.setMessage("Invalid Email ID.!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			}
		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			response2.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(response2);
		}
		response2.setMessage("Invalid Mobile Number..!");
		response2.setStatus(false);
		return new ResponseEntity<>(response2, HttpStatus.OK);
	}

	@GetMapping("/sp-get-profile")
	public ResponseEntity<?> getProfile(@RequestParam(required = false) String bodSeqNo) {

		Userdto profile = userService.getServiceProfile(bodSeqNo);
		try {
			if (profile == null) {
				response.setMessage("Invalid Email ....!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Profile fetched successfully.");
			response.setStatus(true);
			response.setUserData(profile);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@PostMapping("/user/sp-delete-account")
	public ResponseEntity<?> servicePersonDeletAccoutn(@RequestBody DeleteAccountRequest accountRequest) {
		return new ResponseEntity<ResponseMessageDto>(userService.servicePersonDeleteAccount(accountRequest),
				HttpStatus.CREATED);
	}

	@PostMapping("/sp-login")
	public ResponseEntity<?> login(@RequestBody LoginRequest login) {
		try {
			ResponseSpLoginDto response = userService.loginDetails(login);
			if (response.getJwtToken() != null) {
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}

//	@PreAuthorize("hasAuthority('Adm')")
	@GetMapping("/filterServicePerson")
	public ResponseEntity<ResponseListUserDto> getCustomers(
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "mobile", required = false) String mobile,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "city", required = false) String city) {
		ResponseListUserDto response3 = new ResponseListUserDto();

		try {
			List<User> entity = userService.getServicePersonData(email, mobile, location, status, category, fromDate,
					toDate);
			if (!entity.isEmpty()) {
				response3.setMessage("Service person details fetched successfully.!");
				response3.setStatus(true);
				response3.setData(entity);
				return ResponseEntity.ok(response3);
			} else {
				response3.setMessage("No data found for the given details.!");
				response3.setStatus(true);
				return ResponseEntity.status(HttpStatus.OK).body(response3);
			}
		} catch (Exception e) {
			response3.setMessage(e.getMessage());
			response3.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(response3);
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
	public ResponseEntity<ResponseMessageDto> error() {
		response2.setMessage("Access Denied");
		response2.setStatus(false);
		return new ResponseEntity<ResponseMessageDto>(response2, HttpStatus.UNAUTHORIZED);
	}

}

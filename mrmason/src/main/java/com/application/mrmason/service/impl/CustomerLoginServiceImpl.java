package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.CustomerLoginRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.security.JwtService;
import com.application.mrmason.service.CustomerLoginService;
import com.application.mrmason.service.CustomerRegistrationService;
import com.application.mrmason.service.OtpGenerationService;


@Service
public class CustomerLoginServiceImpl implements CustomerLoginService {

	@Autowired
	CustomerLoginRepo loginRepo;

	@Autowired
	OtpGenerationService otpService;
	@Autowired
	CustomerRegistrationService regService;

	@Autowired
	CustomerRegistrationRepo customerRegistrationRepo;
	@Autowired
	JwtService jwtService;
	@Autowired
	BCryptPasswordEncoder byCrypt;

//	@Override
//	public String readHtmlContent(String filePath) {
//		try {
//			Resource resource = new ClassPathResource(filePath);
//			byte[] contentBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
//			return new String(contentBytes, StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			// Handle exception
//			return "";
//		}
//	}

	@Override
	public String existedInData(String email) {
		if (loginRepo.findByUserEmail(email) == null) {
			return null;
		}
		return email;
	}

	@Override
	public CustomerLogin updateDataWithEmailAndRegSource(String email,RegSource regSource) {
		Optional<CustomerLogin> existedById = Optional.of(loginRepo.findByUserEmailAndRegSource(email,regSource));
		if (existedById.isPresent()) {
			existedById.get().setEmailVerified("yes");
			existedById.get().setStatus("active");
			return loginRepo.save(existedById.get());
		}
		return null;
	}
	
	@Override
	public CustomerLogin updateDataWithMobile(String mobile,RegSource regSource) {
		Optional<CustomerLogin> existedById = Optional.of(loginRepo.findByUserMobileAndRegSource(mobile,regSource));
		if (existedById.isPresent()) {
			existedById.get().setMobileVerified("yes");
			existedById.get().setStatus("active");
			return loginRepo.save(existedById.get());
		}
		return null;
	}

	@Override
	public ResponseLoginDto loginDetails(String userEmail, String phno, String userPassword,RegSource regSource) {
		ResponseLoginDto response = new ResponseLoginDto();
		CustomerLogin loginDb = loginRepo.findByUserEmailOrUserMobileAndRegSource(userEmail, phno,regSource);
		if (loginDb != null) {
			if (loginDb.getStatus().equalsIgnoreCase("active")) {
				CustomerRegistration customerRegistration = customerRegistrationRepo
						.findByUserEmailAndRegSource(loginDb.getUserEmail(),loginDb.getRegSource());
				if (userEmail != null && phno == null) {
					if (loginDb.getEmailVerified().equalsIgnoreCase("yes")) {
						if (byCrypt.matches(userPassword, loginDb.getUserPassword())) {
							String jwtToken = jwtService.generateToken(customerRegistration,customerRegistration.getUserid());
							response.setMessage("login");
							response.setJwtToken(jwtToken);
							return response;

						} else {
							response.setMessage("InvalidPassword");
						}
					} else {
						response.setMessage("verifyEmail");
					}
				} else if (userEmail == null && phno != null) {
					if (loginDb.getMobileVerified().equalsIgnoreCase("yes")) {
						if (byCrypt.matches(userPassword, loginDb.getUserPassword())) {
							String jwtToken = jwtService.generateToken(customerRegistration,customerRegistration.getUserid());
							response.setJwtToken(jwtToken);
							response.setMessage("login");
							return response;
						} else {
							response.setMessage("InvalidPassword");
						}
					} else {
						response.setMessage("verifyMobile");
					}
				}
			} else {
				response.setMessage("inactive");
			}
		}

		response.setMessage("invalid");
		return null;
	}

	@Override
	public CustomerRegistrationDto getCustomerDetails(String email, String phno) {
		Optional<CustomerRegistration> customerOp = Optional.of(regService.getCustomer(email, phno));
		CustomerRegistrationDto customerDto = new CustomerRegistrationDto();
		customerDto.setId(customerOp.get().getId());
		customerDto.setRegDate(customerOp.get().getRegDate());
		customerDto.setUserDistrict((customerOp.get().getUserDistrict()));
		customerDto.setUserEmail(customerOp.get().getUserEmail());
		customerDto.setUserid(customerOp.get().getUserid());
		customerDto.setUserMobile(customerOp.get().getUserMobile());
		customerDto.setUserName(customerOp.get().getUsername());
		customerDto.setUserPincode(customerOp.get().getUserPincode());
		customerDto.setUserState(customerOp.get().getUserState());
		customerDto.setUserTown(customerOp.get().getUserTown());
		customerDto.setUsertype(String.valueOf(customerOp.get().getUserType()));
		return customerDto;
	}

	@Override
	public String sendMail(String email,RegSource regSource) {
		Optional<CustomerLogin> userOp = Optional.ofNullable(loginRepo.findByUserEmailAndRegSource(email,regSource));
		if (userOp.isPresent()) {
			otpService.generateCustomerOtp(email,regSource);
			return "otp";
		}
		return null;
	}
	@Override
	public String sendSms(String mobile,RegSource regSource) {
		Optional<CustomerLogin> userOp = Optional.ofNullable(loginRepo.findByUserMobileAndRegSource(mobile,regSource));
		if (userOp.isPresent()) {
			otpService.generateCustomerMobileOtp(mobile,regSource);
			return "otp";
		}
		return null;
	}
	
	@Override
	public String forgetPassword(String mobile,String email, String otp, String newPass, String confPass,RegSource regSource) {
		Optional<CustomerLogin> userEmail = Optional.ofNullable(loginRepo.findByUserEmailAndRegSource(email,regSource));
		Optional<CustomerLogin> userMobile = Optional.ofNullable(loginRepo.findByUserMobileAndRegSource(mobile,regSource));
		if (userEmail.isPresent()) {
			if (otpService.verifyCustomerEmailOtp(email, otp,regSource)) {
				if (newPass.equals(confPass)) {
					String encryptPassword = byCrypt.encode(confPass);
					userEmail.get().setUserPassword(encryptPassword);
					loginRepo.save(userEmail.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		}else if(userMobile.isPresent()) {
			if (otpService.verifyMobileOtpRegSource(mobile, otp,regSource)) {
				if (newPass.equals(confPass)) {
					String encryptPassword = byCrypt.encode(confPass);
					userMobile.get().setUserPassword(encryptPassword);
					loginRepo.save(userMobile.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		}
		else if(!userEmail.isPresent()&& userMobile.isPresent()) {
			return "incorrectEmail";
		}
		return null;

	}

}

package com.application.mrmason.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.repository.CustomerLoginRepo;
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

	@Override
	public String readHtmlContent(String filePath) {
		try {
			Resource resource = new ClassPathResource(filePath);
			byte[] contentBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
			return new String(contentBytes, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// Handle exception
			return "";
		}
	}

	@Override
	public String existedInData(String email) {
		if (loginRepo.findByUserEmail(email) == null) {
			return null;
		}
		return email;
	}

	@Override
	public CustomerLogin updateDataWithEmail(String email) {
		Optional<CustomerLogin> existedById = Optional.of(loginRepo.findByUserEmail(email));
		if (existedById.isPresent()) {
			existedById.get().setEmailVerified("yes");
			existedById.get().setStatus("active");
			return loginRepo.save(existedById.get());
		}
		return null;
	}

	@Override
	public String loginDetails(String userEmail, String phno, String userPassword) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
		if (loginRepo.findByUserEmailOrUserMobile(userEmail, phno) != null) {
			Optional<CustomerLogin> user = Optional.of(loginRepo.findByUserEmailOrUserMobile(userEmail, phno));
			if (user.isPresent()) {
				CustomerLogin loginDb = user.get();
				if (loginDb.getStatus().equalsIgnoreCase("active")) {
					if (userEmail != null && phno == null) {
						if (loginDb.getEmailVerified().equalsIgnoreCase("yes")) {
							if (byCrypt.matches(userPassword, loginDb.getUserPassword())) {
								return "login";
							} else {
								return "InvalidPassword";
							}
						} else {

							return "verifyEmail";
						}
					} else if (userEmail == null && phno != null) {
						if (loginDb.getMobileVerified().equalsIgnoreCase("yes")) {
							if (byCrypt.matches(userPassword, loginDb.getUserPassword())) {
								return "login";
							} else {
								return "InvalidPassword";
							}
						} else {
							return "verifyMobile";
						}
					}
				} else {
					return "inactive";
				}
			}
		}
		return "invalid";
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
		customerDto.setUserName(customerOp.get().getUserName());
		customerDto.setUserPincode(customerOp.get().getUserPincode());
		customerDto.setUserState(customerOp.get().getUserState());
		customerDto.setUserTown(customerOp.get().getUserTown());
		customerDto.setUsertype(customerOp.get().getUsertype());
		return customerDto;
	}

	@Override
	public String sendMail(String email) {
		Optional<CustomerLogin> userOp = Optional.of(loginRepo.findByUserEmail(email));
		if (userOp.isPresent()) {
			otpService.generateOtp(email);
			return "otp";
		}
		return null;
	}

	@Override
	public String forgetPassword(String email, String otp, String newPass, String confPass) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();

		Optional<CustomerLogin> userOp = Optional.of(loginRepo.findByUserEmail(email));
		if (userOp.isPresent()) {
			if (otpService.verifyOtp(email, otp)) {
				if (newPass.equals(confPass)) {
					String encryptPassword = byCrypt.encode(confPass);
					userOp.get().setUserPassword(encryptPassword);
					loginRepo.save(userOp.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		}
		return null;

	}

}

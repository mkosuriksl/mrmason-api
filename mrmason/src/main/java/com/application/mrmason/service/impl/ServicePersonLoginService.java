package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.UserDAO;

@Service
public class ServicePersonLoginService {

	@Autowired
	ServicePersonLoginDAO emailLoginRepo;

	@Autowired
	UserService userService;

	@Autowired
	UserDAO userDAO;

	@Autowired
	OtpGenerationServiceImpl otpService;

	public String isEmailExists(String email) {
		if (emailLoginRepo.findByEmail(email) != null) {
			return email;
		} else {

		}
		return null;
	}

	public ServicePersonLogin updateData(String otp, String email) {
		Optional<ServicePersonLogin> existedById = Optional.of(emailLoginRepo.findByEmail(email));
		if (existedById.isPresent()) {
			existedById.get().setEOtp(otp);
			existedById.get().setEVerify("Yes");
			userService.updateDataWithEmail(email);

			return emailLoginRepo.save(existedById.get());
		}
		return null;
	}

	public Userdto getUserDto(String email, String mobile) {
		Optional<User> user = Optional.of(userDAO.findByEmailOrMobile(email, mobile));
		User userdb = user.get();

		Userdto dto = new Userdto();

		dto.setName(userdb.getName());
		dto.setMobile(userdb.getMobile());
		dto.setEmail(userdb.getEmail());
		dto.setAddress(userdb.getAddress());
		dto.setCity(userdb.getCity());
		dto.setDistrict(userdb.getDistrict());
		dto.setState(userdb.getState());
		dto.setPincodeNo(userdb.getPincodeNo());
		dto.setVerified(userdb.getVerified());
		dto.setUserType(userdb.getUserType());
		dto.setStatus(userdb.getStatus());
		dto.setBusinessName(userdb.getBusinessName());
		dto.setBodSeqNo(userdb.getBodSeqNo());
		dto.setRegisteredDate(userdb.getRegisteredDate());
		dto.setUpdatedDate(userdb.getUpdatedDate());
		dto.setServiceCategory(userdb.getServiceCategory());
		return dto;

	}

	public String loginDetails(String email, String mobile, String password) {

		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();

		Optional<ServicePersonLogin> loginDb = Optional.of((emailLoginRepo.findByEmailOrMobile(email, mobile)));

		if (loginDb.isPresent()) {
			Optional<User> userEmailMobile = Optional.of(userDAO.findByEmailOrMobile(email, mobile));
			User user = userEmailMobile.get();
			String status = user.getStatus();

			if (userEmailMobile.isPresent()) {
				if (status != null && status.equalsIgnoreCase("active")) {

					if (email != null && mobile == null) {
						if (loginDb.get().getEVerify().equalsIgnoreCase("yes")) {

							if (byCrypt.matches(password, user.getPassword())) {
								return "login";
							} else {
								return "InvalidPassword";
							}
						} else {
							return "verifyEmail";
						}
					} else if (email == null && mobile != null) {
						if (loginDb.get().getMobVerify().equalsIgnoreCase("yes")) {

							if (byCrypt.matches(password, user.getPassword())) {
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
		return "inavalid user";
	}

}

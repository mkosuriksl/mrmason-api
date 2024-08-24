package com.application.mrmason.service.impl;

import java.util.Optional;

import com.application.mrmason.dto.Userdto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		dto.setLocation(userdb.getLocation());
//		dto.setPincodeNo(userdb.getPincodeNo());
		dto.setVerified(userdb.getVerified());
		dto.setUserType(String.valueOf(userdb.getUserType()));
		dto.setStatus(userdb.getStatus());
		dto.setBusinessName(userdb.getBusinessName());
		dto.setBodSeqNo(userdb.getBodSeqNo());
		dto.setRegisteredDate(userdb.getRegisteredDate());
		dto.setUpdatedDate(userdb.getUpdatedDate());
		dto.setServiceCategory(userdb.getServiceCategory());
		return dto;

	}

}

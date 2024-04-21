package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;


import com.application.mrmason.dto.Userdto;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.User;


@Service
public class UserService {
	@Autowired
	OtpGenerationServiceImpl otpService;

	@Autowired
	UserDAO userDAO;

	@Autowired
	ServicePersonLoginDAO serviceLoginRepo;
	@Autowired
	BCryptPasswordEncoder byCrypt;

	public boolean isEmailExists(String email) {
		return userDAO.existsByEmail(email);
	}

	public boolean isMobileExists(String mobile) {
		return userDAO.existsByMobile(mobile);
	}

	public Userdto addDetails(User user) {
		String encryptPassword = byCrypt.encode(user.getPassword());
		user.setPassword(encryptPassword);
		userDAO.save(user);

		ServicePersonLogin service = new ServicePersonLogin();
		service.setEmail(user.getEmail());
		service.setMobile(user.getMobile());
		service.setMobVerify("No");
		service.setEVerify("No");

		serviceLoginRepo.save(service);

		Userdto dto = new Userdto();
		dto.setName(user.getName());
		dto.setMobile(user.getMobile());
		dto.setEmail(user.getEmail());
		dto.setAddress(user.getAddress());
		dto.setCity(user.getCity());
		dto.setDistrict(user.getDistrict());
		dto.setState(user.getState());
		dto.setPincodeNo(user.getPincodeNo());
		dto.setVerified(user.getVerified());
		dto.setUserType(String.valueOf(user.getUserType()));
		dto.setStatus(user.getStatus());
		dto.setBusinessName(user.getBusinessName());
		dto.setBodSeqNo(user.getBodSeqNo());
		dto.setRegisteredDate(user.getRegisteredDate());
		dto.setUpdatedDate(user.getUpdatedDate());
		dto.setServiceCategory(user.getServiceCategory());

		return dto;

	}

	public User updateDataWithEmail(String email) {
		Optional<User> existedByEmail = Optional.of(userDAO.findByEmail(email));
		if (existedByEmail.isPresent()) {
			existedByEmail.get().setVerified("Yes");
			existedByEmail.get().setStatus("Active");
			return userDAO.save(existedByEmail.get());
		}
		return null;
	}

	public User updateProfile(User registrationDetails, String email) {
		Optional<User> existedByEmail = Optional.of(userDAO.findByEmail(email));
		if (existedByEmail.isPresent()) {

			existedByEmail.get().setName(registrationDetails.getName());
			existedByEmail.get().setPincodeNo(registrationDetails.getPincodeNo());
			existedByEmail.get().setState(registrationDetails.getState());
			existedByEmail.get().setDistrict(registrationDetails.getDistrict());
			existedByEmail.get().setAddress(registrationDetails.getAddress());
			existedByEmail.get().setCity(registrationDetails.getCity());

			return userDAO.save(existedByEmail.get());
		}
		return null;
	}

	public String changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
		Optional<User> user = Optional.of(userDAO.findByEmail(email));
		if (user.isPresent()) {
			if (byCrypt.matches(oldPassword, user.get().getPassword())) {
				if (newPassword.equals(confirmPassword)) {
					String encryptPassword = byCrypt.encode(confirmPassword);
					user.get().setPassword(encryptPassword);
					userDAO.save(user.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		} else {
			return "invalid";
		}

	}

	public String sendMail(String email) {
		Optional<User> userOp = Optional.of(userDAO.findByEmail(email));
		if (userOp.isPresent()) {
			otpService.generateOtp(email);
			return "otp";
		}
		return null;
	}

	public String forgetPassword(String email, String otp, String newPassword, String confirmPassword) {

		Optional<User> userOp = Optional.of(userDAO.findByEmail(email));
		if (userOp.isPresent()) {
			if (otpService.verifyOtp(email, otp)) {
				if (newPassword.equals(confirmPassword)) {
					String encryptPassword = byCrypt.encode(confirmPassword);
					userOp.get().setPassword(encryptPassword);
					userDAO.save(userOp.get());
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

	public Userdto getServiceProfile(String email) {

		Optional<User> user = Optional.ofNullable(userDAO.findByEmail(email));

		if (user.isPresent()) {
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
			dto.setUserType(String.valueOf(userdb.getUserType()));
			dto.setStatus(userdb.getStatus());
			dto.setBusinessName(userdb.getBusinessName());
			dto.setBodSeqNo(userdb.getBodSeqNo());
			dto.setRegisteredDate(userdb.getRegisteredDate());
			dto.setUpdatedDate(userdb.getUpdatedDate());
			dto.setServiceCategory(userdb.getServiceCategory());
			return dto;
		}

		return null;

	}
	public List<User> getServicePersonData(String email,String phNo,String location,String status,String category,String fromDate,String toDate) {
		if(fromDate==null && toDate==null&& location==null  && category==null && status!=null|| email!=null || phNo!=null) {
			return userDAO.findByEmailOrMobileOrStatusOrderByRegisteredDateDesc(email, phNo, status);
		}else if(category!=null){
			return userDAO.findByServiceCategory(category);
		}else if(location!=null) {
			return userDAO.findByState(location);
		}
		else {
			return userDAO.findByRegisteredDateBetween(fromDate, toDate);
		}

	}
}

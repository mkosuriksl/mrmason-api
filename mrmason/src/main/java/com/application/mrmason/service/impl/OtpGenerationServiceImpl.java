package com.application.mrmason.service.impl;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.User;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.EmailService;
import com.application.mrmason.service.OtpGenerationService;

@Service
public class OtpGenerationServiceImpl implements OtpGenerationService {
	@Autowired
	private EmailService mailService;

	LocalTime local = LocalTime.now();
	@Autowired
	SmsService smsService;
	
	@Autowired
	ServicePersonLoginDAO userDAO;
	@Autowired
	public AdminDetailsRepo adminRepo;
	private final Map<String, String> otpStorage = new HashMap<>(); // Store OTPs temporarily

	// Generate and send OTP to the user (via email, SMS, etc.)
	@Override
	public String generateOtp(String mail, RegSource regSource) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mail, otp);
//		Optional<ServicePersonLogin> userOpt = Optional.ofNullable(userDAO.findByEmail(mail));
		Optional<ServicePersonLogin> userOpt = userDAO.findByEmailAndRegSource(mail,regSource);
		if (userOpt.isPresent()) {
			ServicePersonLogin user = userOpt.get();
	        user.setEOtp(otp);
	        userDAO.save(user);
	    }
		mailService.sendEmail(mail, otp,regSource);
		return otp;
	}
	
	@Override
	public String generateOtp(String mail) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mail, otp);
		Optional<AdminDetails> userOpt = Optional.ofNullable(adminRepo.findByEmail(mail));
	    if (userOpt.isPresent()) {
	        AdminDetails user = userOpt.get();
	        user.setOtp(otp);
	        adminRepo.save(user);
	    }
		mailService.sendEmail(mail, otp);
		return otp;
	}

	@Override
	public boolean verifyOtp(String email, String enteredOtp,RegSource regSource) {
//		String storedOtp = otpStorage.get(email);
		ServicePersonLogin stored = userDAO.findByEmailEOtpAndRegSourceIgnoreCase(email,enteredOtp,regSource);
		String storedOtp=stored.getEOtp();
		return storedOtp!= null && storedOtp.equals(enteredOtp);
	}

	@Override
	public String generateMobileOtp(String mobile, RegSource regSource) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mobile, otp);
//		Optional<ServicePersonLogin> userOpt = Optional.ofNullable(userDAO.findByMobile(mobile));
		Optional<ServicePersonLogin> userOpt = userDAO.findByMobileAndRegSource(mobile,regSource);
		if (userOpt.isPresent()) {
			ServicePersonLogin user = userOpt.get();
	        user.setMOtp(otp);
	        userDAO.save(user);
	    }
		// String message = "Thanks for registering with us. Your OTP to verify your
		// mobile number is " + otp + " - www.mrmason.in";
		smsService.sendSMSMessage(mobile, otp, regSource);

		return otp;
	}

	@Override
	public boolean verifyMobileOtp(String mobile, String enteredOtp) {
		String storedOtp = otpStorage.get(mobile);
		return storedOtp != null && storedOtp.equals(enteredOtp);
	}

}
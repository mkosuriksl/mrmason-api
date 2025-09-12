package com.application.mrmason.service.impl;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.MaterialSupplierQuotationLogin;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.User;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerEmailOtpRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.MaterialSupplierQuotatuionLoginDAO;
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
	MaterialSupplierQuotatuionLoginDAO msuserDAO;
	
	@Autowired
	public AdminDetailsRepo adminRepo;
	
	@Autowired
	CustomerEmailOtpRepo emailLoginRepo;
	
	@Autowired
	CustomerRegistrationRepo customerRegistrationRepo;
	
	private final Map<String, String> otpStorage = new HashMap<>(); // Store OTPs temporarily

	// Generate and send OTP to the user (via email, SMS, etc.)
	@Override
	public String generateOtp(String mail, RegSource regSource) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mail, otp);
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
	public String generateMsOtp(String mail, RegSource regSource) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mail, otp);
		Optional<MaterialSupplierQuotationLogin> userOpt = msuserDAO.findByEmailAndRegSource(mail,regSource);
		if (userOpt.isPresent()) {
			MaterialSupplierQuotationLogin user = userOpt.get();
	        user.setEOtp(otp);
	        msuserDAO.save(user);
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
	public String generateEmailOtpByCustomer(String mail) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mail, otp);
		Optional<CustomerEmailOtp> userOpt = Optional.ofNullable(emailLoginRepo.findByEmail(mail));
	    if (userOpt.isPresent()) {
	    	CustomerEmailOtp user = userOpt.get();
	        user.setOtp(otp);
	        emailLoginRepo.save(user);
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
	public boolean verifyOtp(String email, String enteredOtp) {
		String storedOtp = otpStorage.get(enteredOtp);
		return storedOtp != null && storedOtp.equals(enteredOtp);
	}
	@Override
	public boolean verifyEmailOtpWithCustomer(String email, String enteredOtp) {
	    return Optional.ofNullable(emailLoginRepo.findByEmailAndOtp(email, enteredOtp))
	                   .map(CustomerEmailOtp::getOtp)
	                   .filter(otp -> otp.equals(enteredOtp))
	                   .isPresent();
	}


	@Override
	public String generateMobileOtp(String mobile, RegSource regSource) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mobile, otp);
		Optional<ServicePersonLogin> userOpt = userDAO.findByMobileAndRegSource(mobile,regSource);
		if (userOpt.isPresent()) {
			ServicePersonLogin user = userOpt.get();
	        user.setMOtp(otp);
	        userDAO.save(user);
	    }
		smsService.sendSMSMessage(mobile, otp, regSource);
		return otp;
	}
	
	@Override
	public String generateMsMobileOtp(String mobile, RegSource regSource) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
		otpStorage.put(mobile, otp);
		Optional<MaterialSupplierQuotationLogin> userOpt = msuserDAO.findByMobileAndRegSource(mobile,regSource);
		if (userOpt.isPresent()) {
			MaterialSupplierQuotationLogin user = userOpt.get();
	        user.setMOtp(otp);
	        msuserDAO.save(user);
	    }
		smsService.sendSMSMessage(mobile, otp, regSource);
		return otp;
	}


	@Override
	public boolean verifyMobileOtp(String mobile, String enteredOtp) {
		String storedOtp = otpStorage.get(mobile);
		return storedOtp != null && storedOtp.equals(enteredOtp);
	}

	
}
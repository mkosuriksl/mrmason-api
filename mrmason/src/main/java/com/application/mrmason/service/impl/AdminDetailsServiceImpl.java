package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.AdminDetailsDto;
import com.application.mrmason.dto.ResponceAdminDetailsDto;
import com.application.mrmason.dto.ResponseSpLoginDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.security.JwtService;
import com.application.mrmason.service.AdminDetailsService;
import com.application.mrmason.service.OtpGenerationService;

@Service
public class AdminDetailsServiceImpl implements AdminDetailsService {
	@Autowired
	public AdminDetailsRepo adminRepo;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	OtpGenerationService otpService;
	@Override
	public AdminDetails registerDetails(AdminDetails admin) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();

		Optional<AdminDetails> user = Optional
				.ofNullable(adminRepo.findByEmailOrMobile(admin.getEmail(), admin.getMobile()));
		if (!user.isPresent()) {
			String encryptPassword = byCrypt.encode(admin.getPassword());
			admin.setPassword(encryptPassword);
			return adminRepo.save(admin);
		}
		return null;
	}

	@Override
	public AdminDetailsDto getDetails(String email, String phno) {
		Optional<AdminDetails> user = Optional
				.ofNullable(adminRepo.findByEmailOrMobile(email, phno));
		AdminDetails adminDetails = user.get();
		if (user.isPresent()) {
			AdminDetailsDto adminDto = new AdminDetailsDto();
			adminDto.setId(adminDetails.getId());
			adminDto.setAdminName(adminDetails.getAdminName());
			adminDto.setAdminType(String.valueOf(adminDetails.getUserType()));
			adminDto.setEmail(adminDetails.getEmail());
			adminDto.setMobile(adminDetails.getMobile());
			adminDto.setStatus(adminDetails.getStatus());
			adminDto.setRegDate(adminDetails.getRegDate());

			return adminDto;
		}
		return null;
	}

	@Override
	public AdminDetailsDto getAdminDetails(AdminDetailsDto admin) {

		Optional<AdminDetails> user = Optional
				.ofNullable(adminRepo.findByEmailOrMobile(admin.getEmail(), admin.getMobile()));
		AdminDetails adminDetails = user.get();
		if (user.isPresent()) {
			admin.setId(adminDetails.getId());
			admin.setAdminName(adminDetails.getAdminName());
			admin.setAdminType(String.valueOf(adminDetails.getUserType()));
			admin.setEmail(adminDetails.getEmail());
			admin.setMobile(adminDetails.getMobile());
			admin.setStatus(adminDetails.getStatus());
			admin.setRegDate(adminDetails.getRegDate());
			return admin;
		}

		return null;
	}

	@Override
	public String updateAdminData(AdminDetails admin) {
		Optional<AdminDetails> user = Optional
				.ofNullable(adminRepo.findByEmailOrMobile(admin.getEmail(), admin.getMobile()));
		if (user.isPresent()) {
			user.get().setAdminName(admin.getAdminName());
			user.get().setUserType(admin.getUserType());

			adminRepo.save(user.get());
			return "Success";
		} else {
			return null;
		}
	}
	
	

	@Override
	public ResponceAdminDetailsDto adminLoginDetails(String userEmail, String phno, String userPassword) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
		ResponceAdminDetailsDto response=new ResponceAdminDetailsDto();
		if (adminRepo.findByEmailOrMobile(userEmail, phno) != null) {
			Optional<AdminDetails> user = Optional.ofNullable(adminRepo.findByEmailOrMobile(userEmail, phno));
			
			if (user.isPresent()) {
				AdminDetails loginDb = user.get();
				if (loginDb.getStatus().equalsIgnoreCase("active")) {
					if (userEmail != null || phno != null) {

						if (byCrypt.matches(userPassword, loginDb.getPassword())) {
							String jwtToken = jwtService.generateToken(loginDb);
							response.setJwtToken(jwtToken);
							response.setMessage("Login Successful.");
							response.setData(getDetails(userEmail, phno));
							return response;
						} else {
							response.setMessage("Invalid Password.");
							return response;
						}

					}
				} else {
					response.setMessage("Inactive User");
					return response;
				}
			}
		}
		response.setMessage("Invalid User.!");
		return response;
	}
	
	@Override
	public String changePassword(String usermail, String oldPass, String newPass, String confPass, String phno) {
		BCryptPasswordEncoder byCrypt=new BCryptPasswordEncoder();
		Optional<AdminDetails> user= Optional.of(adminRepo.findByEmailOrMobile(usermail, phno));
		if(user.isPresent()) {
			if(byCrypt.matches(oldPass,user.get().getPassword() )) {
				if(newPass.equals(confPass)) {
					String encryptPassword =byCrypt.encode(confPass);
					user.get().setPassword(encryptPassword);
					adminRepo.save(user.get());
					return "changed";
				}else {
					return "notMatched";
				}
			}else {
				return "incorrect";
			}
		}else {
			return "invalid";
		}
	}
	
	@Override
	public String sendMail(String email) {
		Optional<AdminDetails> userOp = Optional.of(adminRepo.findByEmail(email));
		if (userOp.isPresent()) {
			otpService.generateOtp(email);
			return "otp";
		}
		return null;
	}

	@Override
	public String forgetPassword(String email,String mobile, String otp, String newPass, String confPass) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();

		Optional<AdminDetails> userOp = Optional.of(adminRepo.findByEmailOrMobile(email,mobile));
		if (userOp.isPresent()) {
			if (otpService.verifyOtp(email, otp)) {
				if (newPass.equals(confPass)) {
					String encryptPassword = byCrypt.encode(confPass);
					userOp.get().setPassword(encryptPassword);
					adminRepo.save(userOp.get());
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

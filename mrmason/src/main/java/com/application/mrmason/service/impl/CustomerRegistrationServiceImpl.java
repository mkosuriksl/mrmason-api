package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.repository.CustomerEmailOtpRepo;
import com.application.mrmason.repository.CustomerLoginRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.service.CustomerRegistrationService;
@Service
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService{

	@Autowired
	CustomerEmailOtpRepo emailRepo;
	@Autowired
	public CustomerRegistrationRepo repo;
	@Autowired
	public CustomerLoginRepo loginRepo;

	
	@Override
	public CustomerRegistrationDto saveData(CustomerRegistration customer) {
		BCryptPasswordEncoder byCrypt=new BCryptPasswordEncoder();
		String encryptPassword =byCrypt.encode(customer.getUserPassword());
		customer.setUserPassword(encryptPassword);
		repo.save(customer);
	    
		CustomerLogin loginEntity=new CustomerLogin();
		loginEntity.setUserEmail(customer.getUserEmail());
		loginEntity.setUserMobile(customer.getUserMobile());
		loginEntity.setUserPassword(customer.getUserPassword());
		loginEntity.setMobileVerified("yes");
		loginEntity.setEmailVerified("no");
		loginEntity.setStatus("inactive");
		
		loginRepo.save(loginEntity);
		
		CustomerEmailOtp emailLoginEntity=new CustomerEmailOtp();
		emailLoginEntity.setEmail(customer.getUserEmail());
		
		emailRepo.save(emailLoginEntity);
		
		CustomerRegistrationDto customerDto=new CustomerRegistrationDto();
		customerDto.setId(customer.getId());
		customerDto.setUserName(customer.getUserName());
		customerDto.setUserEmail(customer.getUserEmail());
		customerDto.setUserid(customer.getUserid());
		customerDto.setUserMobile(customer.getUserMobile());
		customerDto.setRegDate(customer.getRegDate());
		customerDto.setUserPincode(customer.getUserPincode());
		customerDto.setUserState(customer.getUserState());
		customerDto.setUserTown(customer.getUserTown());
		customerDto.setUsertype(customer.getUsertype());
		customerDto.setUserDistrict(customer.getUserDistrict());
		
		return customerDto;
	}
	@Override
	public boolean isUserUnique(CustomerRegistration customer) {
		CustomerRegistration user=repo.findByUserEmailOrUserMobile(customer.getUserEmail(), customer.getUserMobile());
		return user==null;
	}
	@Override
	public List<CustomerRegistration> getCustomerData(String email,String phNo,String userState,String fromDate,String toDate) {
		if(fromDate==null && toDate==null && email!=null || phNo!=null || userState!=null) {
			return repo.findAllByUserEmailOrUserMobileOrUserState(email, phNo, userState);
		}else {
			return repo.findByRegDateBetween(fromDate, toDate);
		}
		
	}
	@Override
	public String updateCustomerData(String userName,String userTown,String userState,String userDist,String userPinCode,String userid) {
		Optional<CustomerRegistration> existedById = Optional.of(repo.findByUserid(userid));
		if(existedById.isPresent()) {
			existedById.get().setUserName(userName);
			existedById.get().setUserPincode(userPinCode);
			existedById.get().setUserState(userState);
			existedById.get().setUserTown(userTown);
			existedById.get().setUserDistrict(userDist);
			repo.save(existedById.get());
			return "Success";
		}else {
			return "invalid";
		}
	}
	@Override
	public CustomerRegistration getCustomer(String email,String phno) {
		return repo.findByUserEmailOrUserMobile(email,phno);
	}
	public CustomerRegistrationDto getProfileData(String userid) {
		CustomerRegistrationDto customerDto=new CustomerRegistrationDto();
		Optional<CustomerRegistration> user=Optional.of(repo.findByUserid(userid));
		customerDto.setId(user.get().getId());
		customerDto.setRegDate(user.get().getRegDate());
		customerDto.setUserDistrict((user.get().getUserDistrict()));
		customerDto.setUserEmail(user.get().getUserEmail());
		customerDto.setUserid(user.get().getUserid());
		customerDto.setUserMobile(user.get().getUserMobile());
		customerDto.setUserName(user.get().getUserName());
		customerDto.setUserPincode(user.get().getUserPincode());
		customerDto.setUserState(user.get().getUserState());
		customerDto.setUserTown(user.get().getUserTown());
		customerDto.setUsertype(user.get().getUsertype());
		return customerDto;
	}
	
	
	@Override
	public String changePassword(String usermail, String oldPass, String newPass, String confPass, String phno) {
		BCryptPasswordEncoder byCrypt=new BCryptPasswordEncoder();
		Optional<CustomerLogin> user= Optional.of(loginRepo.findByUserEmailOrUserMobile(usermail, phno));
		if(user.isPresent()) {
			if(byCrypt.matches(oldPass,user.get().getUserPassword() )) {
				if(newPass.equals(confPass)) {
					String encryptPassword =byCrypt.encode(confPass);
					user.get().setUserPassword(encryptPassword);
					loginRepo.save(user.get());
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
}

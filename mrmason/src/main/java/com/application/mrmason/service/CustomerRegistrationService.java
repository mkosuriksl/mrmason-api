package com.application.mrmason.service;

import java.util.List;
import java.util.Map;

import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.dto.CustomerResponseDTO;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.entity.CustomerRegistration;

public interface CustomerRegistrationService {

	CustomerRegistrationDto saveData(CustomerRegistration customer);

	boolean isUserUnique(CustomerRegistration customer);

	String updateCustomerData(String userName, String userTown, String userState, String userDist, String userPinCod,
			String userid);

	CustomerRegistration getCustomer(String email, String phno);

	CustomerRegistrationDto getProfileData(String userid);

	String changePassword(String usermail, String oldPass, String newPass, String confPass, String phno);

	List<CustomerResponseDTO> getCustomerData(String email, String phNo, String userState, String fromDate,
			String toDate,Map<String, String> requestParams);

	public ResponseLoginDto loginDetails(String userEmail, String phno, String userPassword);
}

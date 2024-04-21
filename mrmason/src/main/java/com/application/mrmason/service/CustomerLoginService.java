package com.application.mrmason.service;


import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.entity.CustomerLogin;

public interface CustomerLoginService {

	String existedInData(String email);
//	String readHtmlContent(String filePath);
	CustomerLogin updateDataWithEmail(String email);
	ResponseLoginDto loginDetails(String userEmail , String phno, String userPassword);
	String forgetPassword(String email,String otp,String newPass,String confPass);
	String sendMail(String email);
	public CustomerRegistrationDto getCustomerDetails(String email,String phno);
}

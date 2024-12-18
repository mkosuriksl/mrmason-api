package com.application.mrmason.service;


import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.enums.RegSource;

public interface CustomerLoginService {

	String existedInData(String email);
//	String readHtmlContent(String filePath);
	CustomerLogin updateDataWithEmail(String email);
	CustomerLogin updateDataWithMobile(String mobile);
	ResponseLoginDto loginDetails(String userEmail , String phno, String userPassword);
	String forgetPassword(String mobile,String email,String otp,String newPass,String confPass);
	String sendMail(String email,RegSource regSource);
	String sendSms(String mobile,RegSource regSource);
	CustomerRegistrationDto getCustomerDetails(String email,String phno);
}

package com.application.mrmason.service;


import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.enums.RegSource;

public interface CustomerLoginService {

	String existedInData(String email);
//	String readHtmlContent(String filePath);
//	CustomerLogin updateDataWithEmail(String email);
	public CustomerLogin updateDataWithEmailAndRegSource(String email,RegSource regSource);
	public CustomerLogin updateDataWithMobile(String mobile,RegSource regSource) ;
	ResponseLoginDto loginDetails(String userEmail , String phno, String userPassword,RegSource regSource);
	public String forgetPassword(String mobile,String email, String otp, String newPass, String confPass,RegSource regSource);
	String sendMail(String email,RegSource regSource);
	String sendSms(String mobile,RegSource regSource);
	CustomerRegistrationDto getCustomerDetails(String email,String phno);
}

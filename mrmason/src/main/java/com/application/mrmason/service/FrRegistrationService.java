package com.application.mrmason.service;

import com.application.mrmason.dto.FrLoginRequest;
import com.application.mrmason.dto.FrRegRequestDto;
import com.application.mrmason.dto.FrRegResponseDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.OtpDto;
import com.application.mrmason.dto.ResetChangePassOtpDto;
import com.application.mrmason.dto.ResponseFrLoginDto;
import com.application.mrmason.dto.ResponseFrRegistrationDto;
import com.application.mrmason.entity.FrProfile;

public interface FrRegistrationService {

    public GenericResponse<FrRegResponseDto> registerUser(FrRegRequestDto dto);

    public GenericResponse<Void> sendOtp(OtpDto dto);

    public GenericResponse<String> verifyOtp(OtpDto dto);

	    
    public ResponseFrLoginDto login(FrLoginRequest request);
    
	public GenericResponse<FrProfile> addOrUpdateProfile(FrProfile profile);
	
	public GenericResponse<Void> forgotSendOtp(OtpDto dto) ;
	
	public GenericResponse<Void> forgotVerifyOtp(OtpDto dto);
	
	public GenericResponse<Void> changePassword(ResetChangePassOtpDto dto);

	public ResponseFrRegistrationDto getFrReg(
	        String frUserId,
	        String frEmail,
	        String frMobile,
	        String frLinkedInProfile,
	        String regSource,
	        String userType,
	        String country,
	        String category,
	        String subCategory,
	        int page,
	        int size);
}


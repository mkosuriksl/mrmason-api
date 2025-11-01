package com.application.mrmason.service;

import com.application.mrmason.dto.FrLoginRequest;
import com.application.mrmason.dto.FrRegRequestDto;
import com.application.mrmason.dto.FrRegResponseDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.OtpDto;
import com.application.mrmason.dto.ResponseFrLoginDto;
import com.application.mrmason.entity.FrProfile;

public interface FrRegistrationService {

    public GenericResponse<FrRegResponseDto> registerUser(FrRegRequestDto dto);

    public GenericResponse<Void> sendOtp(OtpDto dto);

    public GenericResponse<String> verifyOtp(OtpDto dto);

//    public GenericResponse<String> forgotPasswordSendOtp(OtpDto dto);
//
//    public GenericResponse<String> forgotPasswordVerifyOtp(OtpDto dto);
//
//    public GenericResponse<String> changePassword(ChangePasswordFrDto dto);
    
    public ResponseFrLoginDto login(FrLoginRequest request);
    
	public GenericResponse<FrProfile> addOrUpdateProfile(FrProfile profile);
    
}


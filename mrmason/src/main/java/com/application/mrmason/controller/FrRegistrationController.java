package com.application.mrmason.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.FrLoginRequest;
import com.application.mrmason.dto.FrRegRequestDto;
import com.application.mrmason.dto.FrRegResponseDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.OtpDto;
import com.application.mrmason.dto.ResetChangePassOtpDto;
import com.application.mrmason.dto.ResponseFrLoginDto;
import com.application.mrmason.entity.FrProfile;
import com.application.mrmason.service.FrRegistrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrRegistrationController {

    private final FrRegistrationService service;

    @PostMapping("/register")
    public GenericResponse<FrRegResponseDto> register(@RequestBody FrRegRequestDto dto) {
        return service.registerUser(dto);
    }

    @PostMapping("/send-otp")
    public GenericResponse<Void> sendOtp(@RequestBody OtpDto dto) {
        return service.sendOtp(dto);
    }


    @PostMapping("/verify-otp")
    public GenericResponse<String> verifyOtp(@RequestBody OtpDto dto) {
        return service.verifyOtp(dto);
    }
    
    @PostMapping("/login")
    public ResponseFrLoginDto login(@RequestBody FrLoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/forgot/send-otp")
    public GenericResponse<Void> forgotSendOtp(@RequestBody OtpDto dto) {
        return service.forgotSendOtp(dto);
    }

    @PostMapping("/forgot/verify-otp")
    public GenericResponse<Void> forgotVerifyOtp(@RequestBody OtpDto dto) {
        return service.forgotVerifyOtp(dto);
    }

   
    @PostMapping("/fr-profile")
    public ResponseEntity<GenericResponse<FrProfile>> saveOrUpdateProfile(@RequestBody FrProfile profile) {
        GenericResponse<FrProfile> response = service.addOrUpdateProfile(profile);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/change-password")
    public GenericResponse<Void> changePassword(@RequestBody ResetChangePassOtpDto dto) {
    	return service.changePassword(dto);

    }

}


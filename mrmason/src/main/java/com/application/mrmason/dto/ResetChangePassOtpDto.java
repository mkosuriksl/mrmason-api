package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetChangePassOtpDto {
    private String emailOrMobile;
    private String newPass;
    private String confPass;
    private String oldPassword;
    
    private RegSource regSource;
}


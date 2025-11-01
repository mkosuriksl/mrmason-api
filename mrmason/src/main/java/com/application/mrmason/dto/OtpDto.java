package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpDto {
    private String emailOrMobile;
    private String otp;
    private RegSource regSource;
}


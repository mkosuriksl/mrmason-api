package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FrRegistrationResponseDto {

    private String frUserId;
    private String frEmail;
    private String frMobile;
    private String frLinkedInProfile;
    private String regSource;
    private String userType;
    private String country;
    private String category;
    private String subCategory;
    
}

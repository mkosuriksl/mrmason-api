package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;
import com.application.mrmason.entity.UserType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrLoginDetailsDto {
    private String frUserid;
    private String frEmail;
    private String frMobile;
    private String frLinkedInProfile;
    private String status;
    private String emailVerified;
    private String mobileVerified;
    private String updatedDate;
    private RegSource regSource;
    private UserType userType;
}


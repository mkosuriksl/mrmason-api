package com.application.mrmason.dto;

import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrRegRequestDto {
    private String frEmail;
    private String frMobile;
    private String frPassword;
    private String frLinkedInProfile;
    private UserType userType;
    private RegSource regSource;
}

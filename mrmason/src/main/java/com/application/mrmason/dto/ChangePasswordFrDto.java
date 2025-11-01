package com.application.mrmason.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordFrDto {
    private String emailOrMobile;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}

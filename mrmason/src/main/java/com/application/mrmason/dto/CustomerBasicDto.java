package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerBasicDto {
    private String userid;
    private String userEmail;
    private String userMobile;
}

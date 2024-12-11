package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class AdminMembershipPlanResponseDTO {
    private String message;
    private String status;
    private AdminMembershipPlanDTO data;
}

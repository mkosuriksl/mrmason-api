package com.application.mrmason.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class AdminMembershipGetResponseDTO {
    private String message;
    private String status;
    private List<AdminMembershipPlanDTO> data;
}

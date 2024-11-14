package com.application.mrmason.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMembershipPlanDTO {

    private String membershipPlanId;
    private int amount;
    private String noOfDaysValid;
    private String planName;
    private String status;
    private String updatedBy;
    private LocalDateTime updatedDate;

}

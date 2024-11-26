package com.application.mrmason.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminMembershipPlanDTO {

    private String membershipPlanId;
    private int amount;
    private int noOfDaysValid;
    private String planName;
    private String status;
    private String defaultPlan;
    private String updatedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss")
    private LocalDateTime updatedDate;

}

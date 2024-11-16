package com.application.mrmason.dto;

import java.sql.Timestamp;

import com.application.mrmason.entity.AdminMembershipPlanEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminStoreVerificationResponseDTO {
    private String storeId;
    private String bodSeqNo;
    private String bodSeqNoStoreId;
    private String verificationStatus;
    private String verificationComment;
    private String updatedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss", timezone = "Asia/Kolkata")
    private Timestamp updatedDate;
    private String defaultPlan;
    private AdminMembershipPlanEntity defaultPlanData;
}

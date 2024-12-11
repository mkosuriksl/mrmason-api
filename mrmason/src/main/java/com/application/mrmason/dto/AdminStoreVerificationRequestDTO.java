package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminStoreVerificationRequestDTO {
    private String storeId;
    private String bodSeqNo;
    private String bodSeqNoStoreId;
    private String verificationStatus;
    private String defaultPlan;
    private String verificationComment;
    private String updatedBy;

}
